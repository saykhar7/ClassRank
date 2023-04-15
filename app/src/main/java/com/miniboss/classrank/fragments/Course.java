package com.miniboss.classrank.fragments;

import com.google.firebase.firestore.PropertyName;

public class Course {

    private String department_name;
    private String dept_short_name;
    private int class_number;

    public Course() {
        // Empty constructor needed for Firestore
    }

    public Course(String department_name, String dept_short_name, int class_number) {
        this.department_name = department_name;
        this.dept_short_name = dept_short_name;
        this.class_number = class_number;
    }

    @PropertyName("department_name")
    public String getDepartmentName() {
        return department_name;
    }

    @PropertyName("department_name")
    public void setDepartmentName(String department_name) {
        this.department_name = department_name;
    }

    @PropertyName("dept_short_name")
    public String getDepartmentShortName() {
        return dept_short_name;
    }

    @PropertyName("dept_short_name")
    public void setDepartmentShortName(String dept_short_name) {
        this.dept_short_name = dept_short_name;
    }

    @PropertyName("class_number")
    public int getClassNumber() {
        return class_number;
    }

    @PropertyName("class_number")
    public void setClassNumber(int class_number) {
        this.class_number = class_number;
    }

    public boolean matchesQuery(String query) {
        String lowerQuery = query.toLowerCase();
        if (department_name.toLowerCase().contains(lowerQuery) ||
                dept_short_name.toLowerCase().contains(lowerQuery) ||
                Integer.toString(class_number).contains(lowerQuery)) {
            return true;
        }

        return false;
    }

}