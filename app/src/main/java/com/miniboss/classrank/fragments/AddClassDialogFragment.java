package com.miniboss.classrank.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.miniboss.classrank.R;
import com.miniboss.classrank.model.Department;
import com.miniboss.classrank.fragments.Course;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AddClassDialogFragment extends DialogFragment {
    private static final String ARG_DEPARTMENTS = "departments";
    private List<Department> departmentList;
    private FirebaseFirestore db;
    private OnCourseAddedListener onCourseAddedListener;

    public AddClassDialogFragment() {
        // Required empty public constructor
    }

    public interface OnCourseAddedListener {
        void onCourseAdded(Course course);
    }

    public static AddClassDialogFragment newInstance(ArrayList<Department> departmentList, OnCourseAddedListener onCourseAddedListener) {
        AddClassDialogFragment fragment = new AddClassDialogFragment();
        fragment.onCourseAddedListener = onCourseAddedListener;
        Bundle args = new Bundle();
        args.putSerializable(ARG_DEPARTMENTS, departmentList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            departmentList = (ArrayList<Department>) getArguments().getSerializable(ARG_DEPARTMENTS);
        }
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.list_item_add_class, null);

        Spinner spinnerDepartments = view.findViewById(R.id.spinner_departments);
        ArrayAdapter<Department> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, departmentList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepartments.setAdapter(adapter);

        EditText etClassNumber = view.findViewById(R.id.et_class_number);

        builder.setView(view)
                .setPositiveButton("Add", (dialog, id) -> {
                    Department selectedDepartment = (Department) spinnerDepartments.getSelectedItem();

                    String departmentName = selectedDepartment.getDepartmentName();
                    String departmentShortName = selectedDepartment.getDept_name_short();
                    int classNumber = Integer.parseInt(etClassNumber.getText().toString());

                    // Create a new course object
                    Course newCourse = new Course(departmentName, departmentShortName, classNumber);

                    // Save the new course to the database
                    db.collection("Courses")
                            .add(newCourse)
                            .addOnSuccessListener(documentReference -> {
                                // Notify the listener
                                if (onCourseAddedListener != null) {
                                    onCourseAddedListener.onCourseAdded(newCourse);
                                }
                            })
                            .addOnFailureListener(e -> Log.w("AddClassDialogFragment", "Error adding document", e));
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    // User cancelled the dialog
                });

        return builder.create();
    }
}