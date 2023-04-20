package com.miniboss.classrank;

import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.miniboss.classrank.fragments.CourseRating;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.miniboss.classrank.R;
import com.miniboss.classrank.fragments.Course;
import com.miniboss.classrank.fragments.RatingsAdapter;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.Timestamp;

public class CourseRatingsActivity extends AppCompatActivity {

    private RecyclerView ratingsRecyclerView;
    private TextView classNumber, professorName, deptNameShort;
    private RatingBar ratingBar;
    private EditText commentEditText;
    private Button submitButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_ratings);
        db = FirebaseFirestore.getInstance();

        ratingsRecyclerView = findViewById(R.id.ratingsRecyclerView);

        classNumber = findViewById(R.id.classNumber);
        professorName = findViewById(R.id.professorName);
        deptNameShort = findViewById(R.id.deptNameShort);
        ratingBar = findViewById(R.id.ratingBar);
        commentEditText = findViewById(R.id.commentEditText);
        submitButton = findViewById(R.id.submitButton);
        String courseId = getIntent().getStringExtra("course_id");
        fetchUserRating(courseId);
        fetchAllRatings(courseId);

        int classNumberValue = getIntent().getIntExtra("class_number", -1);
        String deptNameShortString = getIntent().getStringExtra("department_name_short");
        String professorNameString = getIntent().getStringExtra("professor_name");

        if (classNumberValue != -1) {
            String classNumberString = String.valueOf(classNumberValue);
            classNumber.setText(classNumberString);
        }
        deptNameShort.setText(deptNameShortString);
        professorName.setText(professorNameString);

        ratingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ratingsRecyclerView.setAdapter(new RatingsAdapter(this, getSampleRatings()));

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float ratingValue = ratingBar.getRating();
                String comment = commentEditText.getText().toString().trim();
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Timestamp timestamp = Timestamp.now();

                CourseRating courseRating = new CourseRating(courseId, ratingValue, comment, userId, timestamp);

                String documentId = (String) submitButton.getTag();

                if (documentId != null) {
                    // Update existing rating
                    db.collection("Ratings")
                            .document(documentId)
                            .set(courseRating)
                            .addOnSuccessListener(aVoid -> Toast.makeText(CourseRatingsActivity.this, "Updated", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Log.w("CourseRatingsActivity", "Error updating document", e));
                } else {
                    // Save a new rating
                    db.collection("Ratings")
                            .add(courseRating)
                            .addOnSuccessListener(documentReference -> Toast.makeText(CourseRatingsActivity.this, "Submitted", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Log.w("CourseRatingsActivity", "Error adding document", e));
                }
            }
        });
    }

    private List<CourseRating> getSampleRatings() {
        // Replace with a function to fetch real ratings from your data source
        return Arrays.asList();
    }

    private void fetchUserRating(String courseId) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("Ratings")
                .whereEqualTo("courseId", courseId)
                .whereEqualTo("userId", userId)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        CourseRating courseRating = document.toObject(CourseRating.class);

                        // Display the comment and rating
                        displayUserRating(courseRating, document.getId());
                    }
                });
    }
    private void displayUserRating(CourseRating courseRating, String documentId) {
        ratingBar.setRating(courseRating.getRating());
        commentEditText.setText(courseRating.getComment());

        // Attach documentId to the Submit button to later update the existing rating
        submitButton.setTag(documentId);
    }

    private void fetchAllRatings(String courseId) {
        RecyclerView ratingsRecyclerView = findViewById(R.id.ratingsRecyclerView); // Use correct id
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        ratingsRecyclerView.setLayoutManager(layoutManager);

        db.collection("Ratings")
                .whereEqualTo("courseId", courseId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        List<CourseRating> ratingsList = task.getResult().toObjects(CourseRating.class);
                        RatingsAdapter ratingsAdapter = new RatingsAdapter(this, ratingsList);
                        ratingsRecyclerView.setAdapter(ratingsAdapter);
                    }
                });
    }
}

