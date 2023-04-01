package com.miniboss.classrank;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import android.os.Bundle;
import android.content.Intent;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.miniboss.classrank.fragments.HomeFragment;
import com.miniboss.classrank.fragments.ProfileFragment;

public class HomeActivity extends AppCompatActivity implements HomeFragment.HomeController, ProfileFragment.OnLogoutClickListener {
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;

    private ViewPager2 viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        viewPager = findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setUserInputEnabled(false);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_search:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_profile:
                    viewPager.setCurrentItem(2);
                    return true;
            }
            return false;
        });

        String userId = getIntent().getStringExtra("user_id");

        mFirestore.collection("Users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String email = documentSnapshot.getString("email");
                    String firstName = documentSnapshot.getString("firstName");
                    String lastName = documentSnapshot.getString("lastName");

                    viewPagerAdapter.setEmail(email);
                    viewPagerAdapter.notifyDataSetChanged();

                    onFirstNameLastNameFetched(firstName, lastName);

                })
                .addOnFailureListener(e -> {
                    // Handle error
                });

        getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentStarted(@NonNull FragmentManager fm, @NonNull Fragment f) {
                if (f instanceof ProfileFragment) {
                    ((ProfileFragment) f).setOnLogoutClickListener(HomeActivity.this);
                }
            }

            @Override
            public void onFragmentStopped(@NonNull FragmentManager fm, @NonNull Fragment f) {
                if (f instanceof ProfileFragment) {
                    ((ProfileFragment) f).setOnLogoutClickListener(null);
                }
            }
        }, true);
    }

    @Override
    public void onFirstNameLastNameFetched(String firstName, String lastName) {
        getSupportFragmentManager().executePendingTransactions();
        HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag("f0");
        if (homeFragment != null) {
            homeFragment.updateNames(firstName, lastName);
        }
    }

    @Override
    public void onLogout() {
        FirebaseAuth.getInstance().signOut();

        // Navigate back to LoginActivity
        Intent intent = new Intent(HomeActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    }
}