package com.miniboss.classrank;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private EditText firstNameEdit, lastNameEdit, emailEdit, passwordEdit, confirmPasswordEdit;
    private Button signUpButton;
    private TextView loginLink;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        firstNameEdit = findViewById(R.id.firstName);
        lastNameEdit = findViewById(R.id.lastName);
        emailEdit = findViewById(R.id.email);
        passwordEdit = findViewById(R.id.password);
        confirmPasswordEdit = findViewById(R.id.confirmPassword);
        signUpButton = findViewById(R.id.signUpButton);
        loginLink = findViewById(R.id.loginLink);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = firstNameEdit.getText().toString().trim();
                String lastName = lastNameEdit.getText().toString().trim();
                String email = emailEdit.getText().toString().trim();
                String password = passwordEdit.getText().toString().trim();
                String confirmPassword = confirmPasswordEdit.getText().toString().trim();
                
                if(TextUtils.isEmpty(firstName))
                {
                    Toast.makeText(SignUpActivity.this, "Please enter your first name", Toast.LENGTH_LONG).show();
                    firstNameEdit.setError("First Name Required");
                    firstNameEdit.requestFocus();
                } else if (TextUtils.isEmpty(lastName)) {
                    Toast.makeText(SignUpActivity.this, "Please enter your last name", Toast.LENGTH_LONG).show();
                    lastNameEdit.setError("Last Name Required");
                    lastNameEdit.requestFocus();
                    
                } else if (TextUtils.isEmpty(email)) {
                    Toast.makeText(SignUpActivity.this, "Please enter your full email", Toast.LENGTH_LONG).show();
                   emailEdit.setError("Email Required");
                    emailEdit.requestFocus();
                    
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(SignUpActivity.this, "Please entter correct email in form example@domain.com", Toast.LENGTH_SHORT).show();
                    emailEdit.setError("Correct Email Required");
                    emailEdit.requestFocus();
                    

                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(SignUpActivity.this,"Password required", Toast.LENGTH_SHORT).show();
                    passwordEdit.setError("Enter password");
                    passwordEdit.requestFocus();
                    
                } else if (TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(SignUpActivity.this,"Password required", Toast.LENGTH_SHORT).show();
                    confirmPasswordEdit.setError("Enter password");
                    confirmPasswordEdit.requestFocus();
                } else if (password.length()<8) {
                    Toast.makeText(SignUpActivity.this, "Password should be more than 8 char", Toast.LENGTH_SHORT).show();
                    passwordEdit.setError("8 or more char required");
                    passwordEdit.requestFocus();

                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(SignUpActivity.this, "Password should be matched", Toast.LENGTH_SHORT).show();
                    confirmPasswordEdit.setError("Password do not match");
                    confirmPasswordEdit.requestFocus();
                    confirmPasswordEdit.clearComposingText();
                    passwordEdit.clearComposingText();

                }
                else{
                    signUpUser(firstName, lastName, email, password);
                }

//                if(email.)
//
//                if (password.equals(confirmPassword)) {
//
//                } else {
//                    Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
//                }
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
    }

    private void signUpUser(String firstName, String lastName, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();
                        saveUserDataToFirestore(userId, firstName, lastName, email);
                    } else {
                        if (task.getException() != null) {
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(SignUpActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(SignUpActivity.this, "Error creating a new user", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveUserDataToFirestore(String userId, String firstName, String lastName, String email) {
        Map<String, Object> user = new HashMap<>();
        user.put("firstName", firstName);
        user.put("lastName", lastName);
        user.put("email", email);

        mFirestore.collection("Users")
                .document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(SignUpActivity.this, "User signed up successfully!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                    intent.putExtra("user_id", userId);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SignUpActivity.this, "Failed to save user data. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }
}