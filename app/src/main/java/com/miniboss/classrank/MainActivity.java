package com.miniboss.classrank;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        Button signInButton = findViewById(R.id.signInButton);
        Button signUpButton = findViewById(R.id.signUpButton);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart() {
        setMainContentVisibility(View.INVISIBLE);
        setProgressBarVisibility(View.VISIBLE);
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser != null) {
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    intent.putExtra("user_id", currentUser.getUid());
                    startActivity(intent);
                    finish(); // To prevent returning to the MainActivity when pressing the back button
                } else {
                    setMainContentVisibility(View.VISIBLE);
                    setProgressBarVisibility(View.GONE);
                }
            }
        });
    }

    private void setMainContentVisibility(int visibility) {
        findViewById(R.id.main_content).setVisibility(visibility);
    }

    private void setProgressBarVisibility(int visibility) {
        ProgressBar progressBar = findViewById(R.id.loading_progress_bar);
        progressBar.setVisibility(visibility);
    }
}