package com.miniboss.classrank.fragments;

public class Department {
    private String id;
    private String department_name;
    private String dept_name_short;

    public Department() {
        // Required empty public constructor for Firestore
    }

    public Department(String id, String department_name, String dept_name_short) {
        this.id = id;
        this.department_name = department_name;
        this.dept_name_short = dept_name_short;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDepartmentName() {
        return department_name;
    }

    public String getDept_name_short() {
        return dept_name_short;
    }

    @Override
    public String toString() {
        return department_name;
    }
}