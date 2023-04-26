package com.miniboss.classrank;

import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.miniboss.classrank.fragments.CourseRating;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.miniboss.classrank.R;
import com.miniboss.classrank.fragments.Course;
import com.miniboss.classrank.fragments.RatingsAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.Timestamp;

public class CourseRatingsActivity extends AppCompatActivity {

    private RecyclerView ratingsRecyclerView;
    private TextView classNumber, professorName, deptNameShort;
    private RatingBar ratingBar;
    private EditText commentEditText;
    private Button submitButton;
    private FirebaseFirestore db;
    private ImageButton favoriteButton;
    private boolean isFavorited = false;
    private String favoriteId;
    private String userId;
    private String courseId;

    private TextView averageRating;
    private TextView totalRatingsCount;
    private TextView noReviewsMessage;
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
        averageRating = findViewById(R.id.averageRating);
        totalRatingsCount = findViewById(R.id.totalRatingsCount);
        courseId = getIntent().getStringExtra("course_id");
        fetchUserRating(courseId);
        fetchAllRatings(courseId);
        noReviewsMessage = findViewById(R.id.noReviewsMessage);

        favoriteButton = findViewById(R.id.favoriteButton);
        favoriteButton.setOnClickListener(v -> toggleFavorite());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        int classNumberValue = getIntent().getIntExtra("class_number", -1);
        String deptNameShortString = getIntent().getStringExtra("department_name_short");
        String professorNameString = getIntent().getStringExtra("professor_name");

        if (classNumberValue != -1 && deptNameShortString != null) {
            String toolbarTitle = deptNameShortString + " " + classNumberValue;
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(toolbarTitle);
            }
        }

        if (classNumberValue != -1) {
            String classNumberString = String.valueOf(classNumberValue);
            classNumber.setText(classNumberString);
        }
        deptNameShort.setText(deptNameShortString);
        professorName.setText(professorNameString);

        ratingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ratingsRecyclerView.setAdapter(new RatingsAdapter(this, getSampleRatings(), currentUserId));
        // In the onClick method of the submit button
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
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(CourseRatingsActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                                refreshView(); // Refresh the view
                            })
                            .addOnFailureListener(e -> Log.w("CourseRatingsActivity", "Error updating document", e));
                } else {
                    // Save a new rating
                    db.collection("Ratings")
                            .add(courseRating)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(CourseRatingsActivity.this, "Submitted", Toast.LENGTH_SHORT).show();
                                refreshView(); // Refresh the view
                            })
                            .addOnFailureListener(e -> Log.w("CourseRatingsActivity", "Error adding document", e));

                }
            }
        });
    }
    private void refreshView() {
        fetchUserRating(courseId);
        fetchAllRatings(courseId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private List<CourseRating> getSampleRatings() {
        // Replace with a function to fetch real ratings from your data source
        return Arrays.asList();
    }

    private void fetchUserRating(String courseId) {
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

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
        RecyclerView ratingsRecyclerView = findViewById(R.id.ratingsRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        ratingsRecyclerView.setLayoutManager(layoutManager);

        checkIfFavorited(courseId, userId);
        db.collection("Ratings")
                .whereEqualTo("courseId", courseId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<CourseRating> ratingsList = task.getResult().toObjects(CourseRating.class);

                        if (!ratingsList.isEmpty()) {
                            // Calculate the average rating
                            float totalRating = 0;
                            for (CourseRating rating : ratingsList) {
                                totalRating += rating.getRating();
                            }
                            float averageRatingValue = totalRating / ratingsList.size();
                            String averageRatingText = String.format("%.1f/5", averageRatingValue);
                            averageRating.setText(averageRatingText);

                            // Display the total ratings count
                            String totalRatingsCountText = String.format("%d Ratings", ratingsList.size());
                            totalRatingsCount.setText(totalRatingsCountText);

                            // Display the ratings, and pass the userId
                            RatingsAdapter ratingsAdapter = new RatingsAdapter(this, ratingsList, userId);
                            ratingsRecyclerView.setAdapter(ratingsAdapter);
                        } else {
                            // Display "0 Ratings", "0/5" and "Be the first one to review..."
                            totalRatingsCount.setText("0 Ratings");
                            averageRating.setText("0.0/5");
                            noReviewsMessage.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }
    private void checkIfFavorited(String courseId, String userId) {
        db.collection("Favorites")
                .whereEqualTo("userId", userId)
                .whereEqualTo("courseId", courseId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        isFavorited = true;
                        favoriteId = task.getResult().getDocuments().get(0).getId();
                        favoriteButton.setImageResource(R.drawable.ic_star_filled);
                    } else {
                        isFavorited = false;
                        favoriteButton.setImageResource(R.drawable.ic_star_border);
                    }
                });
    }

    private void toggleFavorite() {
        if (isFavorited) {
            removeFromFavorites();
        } else {
            addToFavorites();
        }
    }

    private void addToFavorites() {
        Map<String, Object> favorite = new HashMap<>();
        favorite.put("userId", userId);
        favorite.put("courseId", courseId);

        db.collection("Favorites")
                .add(favorite)
                .addOnSuccessListener(documentReference -> {
                    favoriteId = documentReference.getId();
                    isFavorited = true;
                    favoriteButton.setImageResource(R.drawable.ic_star_filled);
                });
    }

    private void removeFromFavorites() {
        if (favoriteId != null) {
            db.collection("Favorites").document(favoriteId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        isFavorited = false;
                        favoriteButton.setImageResource(R.drawable.ic_star_border);
                    });
        }
    }
}