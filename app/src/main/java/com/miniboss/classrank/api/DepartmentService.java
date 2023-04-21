package com.miniboss.classrank.api;

//import com.miniboss.classrank.model.Department;
import com.miniboss.classrank.fragments.Department;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface DepartmentService {
    @GET("v0/b/classrankcsulb.appspot.com/o/departments.json?alt=media&token=8edf03c7-9099-4269-bd53-dbe4b896f725")
    Call<List<Department>> getDepartments();
}