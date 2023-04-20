package com.miniboss.classrank;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;

import android.media.Rating;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.miniboss.classrank.fragments.CourseRating;
import com.miniboss.classrank.fragments.HomeFragment;
import com.miniboss.classrank.fragments.ProfileFragment;
import com.miniboss.classrank.fragments.RatingsAdapter;
import com.miniboss.classrank.fragments.SearchFragment;
import com.miniboss.classrank.fragments.Course;
import com.miniboss.classrank.fragments.AddClassDialogFragment;

import java.util.Arrays;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements HomeFragment.HomeController, ProfileFragment.OnLogoutClickListener, AddClassDialogFragment.OnCourseAddedListener {
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

    @Override
    public void onCourseAdded(Course course) {
        SearchFragment searchFragment = (SearchFragment) getSupportFragmentManager().findFragmentByTag("f1");
        if (searchFragment != null) {
            searchFragment.setOnCourseAddedInActivityListener(new SearchFragment.OnCourseAddedInActivityListener() {
                @Override
                public void onCourseAddedInActivity(Course addedCourse) {
                    // Call fetchCourses() to refresh the data from the Firestore database
                    searchFragment.fetchCourses();
                }
            });

            searchFragment.onCourseAddedInActivity(course);
        }
    }

    public static class CourseRatingsActivity extends AppCompatActivity {

        private RecyclerView ratingsRecyclerView;
        private Button rateCommentButton;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_course_ratings);

            ratingsRecyclerView = findViewById(R.id.ratingsRecyclerView);
//            rateCommentButton = findViewById(R.id.rateCommentButton);

            ratingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            // Replace SampleRatingsAdapter with the actual adapter for your ratings list
            ratingsRecyclerView.setAdapter(new RatingsAdapter(this, getSampleRatings()));

            rateCommentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Implement your Rate/Comment functionality here
                }
            });
        }

        private List<CourseRating> getSampleRatings() {
            // Replace with a function to fetch real ratings from your data source
            return Arrays.asList();
        }
    }
}