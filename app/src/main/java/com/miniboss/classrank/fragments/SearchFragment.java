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

public class SearchFragment extends Fragment {

    private TextView emailText;

    public SearchFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        emailText = view.findViewById(R.id.emailText);

        Bundle args = getArguments();
        if (args != null) {
            String email = args.getString("emailKey");
            if (email != null) {
                updateEmail(email);
            }
        }

        return view;
    }

    public void updateEmail(String email) {
        emailText.setText(email);
    }
}