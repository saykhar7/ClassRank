package com.miniboss.classrank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);

        Button signInButton = findViewById(R.id.signInButton);
        signInButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            signIn(email, password);
        });

        TextView signUpLink = findViewById(R.id.signUpLink);
        signUpLink.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                            intent.putExtra("user_id", user.getUid());
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        if (task.getException() != null) {
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(SignInActivity.this, "Authentication failed: " + errorMessage, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(SignInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser != null) {
                Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                intent.putExtra("user_id", currentUser.getUid());
                startActivity(intent);
                finish(); // To prevent returning to the SignInActivity when pressing the back button
            }
        });
    }
}