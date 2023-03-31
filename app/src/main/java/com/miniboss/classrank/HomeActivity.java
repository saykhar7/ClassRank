package com.miniboss.classrank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {

    private FirebaseFirestore mFirestore;
    private TextView firstNameText, lastNameText, emailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firstNameText = findViewById(R.id.firstNameText);
        lastNameText = findViewById(R.id.lastNameText);
        emailText = findViewById(R.id.emailText);

        mFirestore = FirebaseFirestore.getInstance();

        String userId = getIntent().getStringExtra("user_id");

        mFirestore.collection("Users").document(userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String firstName = documentSnapshot.getString("firstName");
                        String lastName = documentSnapshot.getString("lastName");
                        String email = documentSnapshot.getString("email");

                        firstNameText.setText(firstName);
                        lastNameText.setText(lastName);
                        emailText.setText(email);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
//                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("HomeActivity", "Error fetching user data from Firestore", e);
                        Toast.makeText(HomeActivity.this, "Failed to fetch user data. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}