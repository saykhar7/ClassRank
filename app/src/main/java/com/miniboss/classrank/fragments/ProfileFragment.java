package com.miniboss.classrank.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.miniboss.classrank.R;

public class ProfileFragment extends Fragment {

    private Button btnLogout;
    private OnLogoutClickListener onLogoutClickListener;

    public ProfileFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        btnLogout = view.findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> {
            if (onLogoutClickListener != null) {
                onLogoutClickListener.onLogout();
            }
        });

        return view;
    }

    public void setOnLogoutClickListener(OnLogoutClickListener listener) {
        this.onLogoutClickListener = listener;
    }

    public interface OnLogoutClickListener {
        void onLogout();
    }
}