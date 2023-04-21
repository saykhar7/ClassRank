package com.miniboss.classrank.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.miniboss.classrank.R;
//import com.miniboss.classrank.model.Department;
import com.miniboss.classrank.fragments.Department;
import com.miniboss.classrank.fragments.Course;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
public class AddClassDialogFragment extends DialogFragment {
    private static final String ARG_DEPARTMENTS = "departments";
    private List<Department> departmentList;
    private FirebaseFirestore db;
    private OnCourseAddedListener onCourseAddedListener;

    private boolean isClassNumberValid(String input) {
        return input.matches("\\d+");
    }

    private boolean isProfessorNameValid(String input) {
        return input.matches("[a-zA-Z\\s]+");
    }

    private boolean isProfessorEmailValid(String input) {
        return input.matches("[a-zA-Z0-9._%+-]+@csulb\\.edu");
    }

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
        EditText etProfessorName = view.findViewById(R.id.et_professor_name);
        EditText etProfessorEmail = view.findViewById(R.id.et_professor_email);

        builder.setView(view)
                .setPositiveButton("Add", null)
                .setNegativeButton("Cancel", (dialog, id) -> {
                    // User cancelled the dialog
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(dialog -> {
            Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                Department selectedDepartment = (Department) spinnerDepartments.getSelectedItem();
                String departmentName = selectedDepartment.getDepartmentName();
                String departmentShortName = selectedDepartment.getDept_name_short();

                String classNumberStr = etClassNumber.getText().toString();
                String professorName = etProfessorName.getText().toString();
                String professorEmail = etProfessorEmail.getText().toString();

                boolean isValidInput = true;

                if (!isClassNumberValid(classNumberStr)) {
                    etClassNumber.setError("Invalid class number");
                    isValidInput = false;
                }

                if (!isProfessorNameValid(professorName)) {
                    etProfessorName.setError("Invalid professor name");
                    isValidInput = false;
                }

                if (!isProfessorEmailValid(professorEmail)) {
                    etProfessorEmail.setError("Invalid email format");
                    isValidInput = false;
                }

                if (isValidInput) {
                    int classNumber = Integer.parseInt(classNumberStr);
                    String courseId = UUID.randomUUID().toString();
                    Course newCourse = new Course(courseId, departmentName, departmentShortName, classNumber, professorName, professorEmail, true);
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

                    alertDialog.dismiss();
                }
            });
        });

        return alertDialog;
    }
}