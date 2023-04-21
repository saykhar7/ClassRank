package com.miniboss.classrank.fragments;

import com.google.firebase.firestore.PropertyName;

public class Course {

    private String department_name;
    private String dept_short_name;
    private int class_number;
    private String professorName;
    private String professorEmail;
    private boolean favorited;

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Course() {
        // Empty constructor needed for Firestore
    }

    public Course(String id, String department_name, String dept_short_name, int class_number, String professorName, String professorEmail, boolean favorited) {
        this.id = id;
        this.department_name = department_name;
        this.dept_short_name = dept_short_name;
        this.class_number = class_number;
        this.professorName = professorName;
        this.professorEmail = professorEmail;
        this.favorited = favorited;
    }
    public boolean isFavorited() {
        return favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }
    @PropertyName("department_name")
    public String getDepartmentName() {
        return department_name;
    }

    @PropertyName("department_name")
    public void setDepartmentName(String department_name) {
        this.department_name = department_name;
    }
    @PropertyName("professor_name")
    public String getProfessorName() {
        return professorName;
    }

    @PropertyName("professor_name")
    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }

    @PropertyName("professor_email")
    public String getProfessorEmail() {
        return professorEmail;
    }

    @PropertyName("professor_email")
    public void setProfessorEmail(String professorEmail) {
        this.professorEmail = professorEmail;
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