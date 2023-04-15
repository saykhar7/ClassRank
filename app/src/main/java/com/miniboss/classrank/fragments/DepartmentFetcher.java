package com.miniboss.classrank.fragments;

import android.util.Log;
import com.miniboss.classrank.api.DepartmentService;
import com.miniboss.classrank.model.Department;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DepartmentFetcher {

    public interface OnFetchCompleteListener {
        void onFetchComplete(List<Department> departments);
    }

    private OnFetchCompleteListener onFetchCompleteListener;

    public void fetchDepartments(OnFetchCompleteListener callback) {
        this.onFetchCompleteListener = callback;
        String baseURL = "https://firebasestorage.googleapis.com/";

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        DepartmentService departmentService = retrofit.create(DepartmentService.class);
        Call<List<Department>> call = departmentService.getDepartments();

        call.enqueue(new Callback<List<Department>>() {
            @Override
            public void onResponse(Call<List<Department>> call, Response<List<Department>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("DepartmentFetcher", "Fetch Success: " + response.body().size() + " departments");
                    callback.onFetchComplete(response.body());
                } else {
                    Log.e("DepartmentFetcher", "Fetch Failure: Response not successful or body is null");
                    Log.e("DepartmentFetcher", "Response: " + response);
                }
            }

            @Override
            public void onFailure(Call<List<Department>> call, Throwable t) {
                Log.e("DepartmentFetcher", "Error in FetchDepartmentsTask", t);
            }
        });
    }
}