package com.miniboss.classrank.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.miniboss.classrank.R;

public class HomeFragment extends Fragment {

    private TextView firstNameText, lastNameText;

    public interface HomeController {
        void onFirstNameLastNameFetched(String firstName, String lastName);
    }

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        firstNameText = view.findViewById(R.id.firstNameText);
      //  lastNameText = view.findViewById(R.id.lastNameText);
        return view;
    }

    public void updateNames(String firstName, String lastName) {
        if (firstNameText != null) {
            firstNameText.setText("Welcome " + firstName+ " "+ lastName);
        }

    }
}