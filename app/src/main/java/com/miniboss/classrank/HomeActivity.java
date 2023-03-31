package com.miniboss.classrank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {

    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private TextView firstNameText, lastNameText, emailText;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firstNameText = findViewById(R.id.firstNameText);
        lastNameText = findViewById(R.id.lastNameText);
        emailText = findViewById(R.id.emailText);
        logoutButton = findViewById(R.id.logoutButton);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        String userId = getIntent().getStringExtra("user_id");

        mFirestore.collection("Users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String firstName = documentSnapshot.getString("firstName");
                    String lastName = documentSnapshot.getString("lastName");
                    String email = documentSnapshot.getString("email");

                    firstNameText.setText(firstName);
                    lastNameText.setText(lastName);
                    emailText.setText(email);
                })
                .addOnFailureListener(e -> {
                    Log.e("HomeActivity", "Error fetching user data from Firestore", e);
                    Toast.makeText(HomeActivity.this, "Failed to fetch user data. Please try again.", Toast.LENGTH_SHORT).show();
                });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(HomeActivity.this, SignInActivity.class);
                startActivity(intent);
                finish(); // This prevents the user from pressing back and returning to the HomeActivity after logging out
            }
        });
    }
}