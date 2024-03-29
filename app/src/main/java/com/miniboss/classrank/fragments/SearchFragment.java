package com.miniboss.classrank.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.miniboss.classrank.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.miniboss.classrank.model.Department;
import com.miniboss.classrank.fragments.Department;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private CourseAdapter courseAdapter;
    private List<Course> courseList;
    private List<Department> departmentList;
    private DepartmentFetcher departmentFetcher;
    private SwipeRefreshLayout swipeRefreshLayout;

    private OnCourseAddedInActivityListener onCourseAddedInActivityListener;

    private static final String TAG = "SearchFragment";

    public void triggerOnCourseAddedInActivity(Course course) {
        if (onCourseAddedInActivityListener != null) {
            onCourseAddedInActivityListener.onCourseAddedInActivity(course);
        }
    }

    public void onCourseAddedInActivity(Course course) {
        if (onCourseAddedInActivityListener != null) {
            onCourseAddedInActivityListener.onCourseAddedInActivity(course);

            // Call the fetchCourses() method to refresh the data when a new course is added
            fetchCourses();
        }
    }

    public interface OnCourseAddedInActivityListener {
        void onCourseAddedInActivity(Course course);
    }

    public void setOnCourseAddedInActivityListener(OnCourseAddedInActivityListener listener) {
        this.onCourseAddedInActivityListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        departmentFetcher = new DepartmentFetcher();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        courseList = new ArrayList<>();
        departmentList = new ArrayList<>();
        courseAdapter = new CourseAdapter(getContext(), courseList, departmentList);
        recyclerView.setAdapter(courseAdapter);

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchCourses();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fetchCourses();

        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Course> filteredCourseList = new ArrayList<>();

                if (newText.isEmpty()) {
                    filteredCourseList.addAll(courseList);
                } else {
                    for (Course course : courseList) {
                        String departmentName = course.getDepartmentName();
                        boolean isDepartmentMatching = departmentName != null && departmentName.toLowerCase().contains(newText.toLowerCase());

                        if (course.matchesQuery(newText)
                                || course.getProfessorName().toLowerCase().contains(newText.toLowerCase())
                                || isDepartmentMatching) {

                            filteredCourseList.add(course);
                        }
                    }
                }

                if (filteredCourseList.isEmpty()) {
                    courseAdapter.setShowAddClassOption(true);
                } else {
                    courseAdapter.setShowAddClassOption(false);
                }

                courseAdapter.filterList(filteredCourseList);
                return true;
            }
        });
    }

    public void fetchCourses() {
        swipeRefreshLayout.setRefreshing(true); // Show loading indicator

        // Assign a unique userId
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Fetch user's favorites
        db.collection("Favorites")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(favoriteTask -> {
                    if (favoriteTask.isSuccessful()) {
                        List<String> favoriteCourseIds = new ArrayList<>();
                        for (DocumentSnapshot document : favoriteTask.getResult()) {
                            favoriteCourseIds.add(document.getString("courseId"));
                        }

                        departmentFetcher.fetchDepartments(departments -> {
                            departmentList.clear();
                            departmentList.addAll(departments);
                            courseAdapter.notifyDataSetChanged();

                            db.collection("Courses")
                                    .get()
                                    .addOnCompleteListener(courseTask -> {
                                        if (courseTask.isSuccessful()) {
                                            courseList.clear();
                                            for (QueryDocumentSnapshot courseDocument : courseTask.getResult()) {
                                                Course course = courseDocument.toObject(Course.class);
                                                course.setId(courseDocument.getId()); // Set the course ID

                                                // Set favorited property based on user's favorites
                                                if (favoriteCourseIds.contains(course.getId())) {
                                                    course.setFavorited(true);
                                                } else {
                                                    course.setFavorited(false);
                                                }

                                                courseList.add(course);
                                            }
                                            courseAdapter.notifyDataSetChanged();
                                            swipeRefreshLayout.setRefreshing(false); // Hide loading indicator
                                        } else {
                                            Log.d(TAG, "Error getting documents: ", courseTask.getException());
                                            swipeRefreshLayout.setRefreshing(false); // Hide loading indicator
                                        }
                                    });
                        });
                    }
                });
    }
}