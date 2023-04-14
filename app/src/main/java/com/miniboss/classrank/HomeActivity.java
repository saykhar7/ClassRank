package com.miniboss.classrank;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import android.os.Bundle;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

    String[] courses = {"Accountancy (ACCT)", "Africana Studies (AFRS)", "Am Sign Lang Ling&Deaf Culture (ASLD)",
            "American Indian Studies (AIS)", "American Studies (AMST)", "Anthropology (ANTH)", "Arabic (ARAB)",
            "Art (ART)", "Art History (AH)", "Asian & Asian American Studies (AAAS)", "Asian American Studies (ASAM)",
            "Asian Studies (A/ST)", "Astronomy (ASTR)", "Athletic Training (AT)", "Athletic Training Education Pg (ATEP)",
            "Biology (BIOL)", "Biomedical Engineering (BME)", "Business Law (BLAW)", "Business, College of (CBA)",
            "CSU OnLine - LB ONLY (OLNE)", "Cambodian (KHMR)", "Chemical Engineering (CH E)",
            "Chemistry & Biochemistry (CHEM)", "Chicano & Latino Studies (CHLS)", "Child Developmnt & Family Stds (CDFS)",
            "Chinese (CHIN)", "Civil Engineering (C E)", "Classics (CLSC)", "Communication Studies (COMM)",
            "Comparative World Literature (CWL)", "Computer Engr & Computer Sci (CECS)", "Concurrent Enrollment (XYZ)",
            "Construction Engineering Mgmt (CEM)", "Consumer Affairs (CAFF)", "Counseling (COUN)",
            "Criminology & Criminal Justice (CRJU)", "Dance (DANC)", "Design (DESN)", "Doctor of Physical Therapy (DPT)",
            "Economics (ECON)", "Educ Leadership Doctorate (EDLD)", "Education - Curr & Instruction (EDCI)",
            "Education - Early Childhood (EDEC)", "Education - Elementary (EDEL)", "Education - Secondary (EDSE)",
            "Education - Single Subject (EDSS)", "Education Specialist (EDSP)", "Educational Administration (EDAD)",
            "Educational Psychology (ED P)", "Educational Technology (ETEC)", "Electrical Engineering (E E)",
            "Emergency Services Adm (EMER)", "Engineering (ENGR)", "Engineering Technology (E T)", "English (ENGL)",
            "Environmental Engineering (ENV)", "Environmental Science & Policy (ES P)",
            "Fashion Merchandising & Design (FMD)", "Filipino (FIL)", "Film and Electronic Arts (FEA)", "Finance (FIN)",
            "Food Science (FSCI)", "French (FREN)", "Geography (GEOG)", "Geology (Pre Fall 2023) (GEOL)", "German (GERM)",
            "Gerontology (GERN)", "Graduate Business Admin (GBA)", "Greek (GK)", "Health Care Administration (HCA)",
            "Health Science (H SC)", "Hebrew (HEBW)", "History (HIST)", "Hospitality Management (HM)",
            "Human Development (HDEV)", "Human Resources Management (HRM)", "Information Systems (I S)",
            "International Education (INTL)", "International Studies (I/ST)", "Italian (ITAL)", "Japanese (JAPN)",
            "Journalism (JOUR)", "Kinesiology (KIN)", "Korean (KOR)", "Latin (LAT)", "Liberal Arts, College of (C/LA)",
            "Liberal Studies (L/ST)", "Linguistics (LING)", "Management (MGMT)", "Marketing (MKTG)"};

            ListView listView;

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