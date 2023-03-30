package com.miniboss.classrank;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView email = (TextView) findViewById(R.id.email);
    TextView password = (TextView) findViewById(R.id.password);

    Button login = (Button) findViewById(R.id.signInButton);

    public void click(View view)
    {

        Log.i("Info", "Sign in Button Clicked");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}