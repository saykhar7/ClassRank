package com.miniboss.classrank;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import android.os.Bundle;

import com.miniboss.classrank.fragments.HomeFragment;
import com.miniboss.classrank.fragments.ProfileFragment;
import com.miniboss.classrank.fragments.SearchFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private String email;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                SearchFragment searchFragment = new SearchFragment();
                Bundle args = new Bundle();
                args.putString("emailKey", email);
                searchFragment.setArguments(args);
                return searchFragment;
            case 2:
                return new ProfileFragment();
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
