package com.miniboss.classrank.fragments;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class CourseRating {
    private String courseId;
    private float rating;
    private String comment;
    private String userId;
    private Timestamp timestamp;

    public CourseRating() {
        // Empty constructor needed for Firestore
    }

    public CourseRating(String courseId, float rating, String comment, String userId, Timestamp timestamp) {
        this.courseId = courseId;
        this.rating = rating;
        this.comment = comment;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    // Getters for all fields

    public String getCourseId() {
        return courseId;
    }

    public float getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public String getUserId() {
        return userId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        if (timestamp != null) {
            return formatter.format(timestamp.toDate());
        } else {
            return "";
        }
    }

    public String getTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
        if (timestamp != null) {
            return formatter.format(timestamp.toDate());
        } else {
            return "";
        }
    }
}