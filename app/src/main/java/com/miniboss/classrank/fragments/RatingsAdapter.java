package com.miniboss.classrank.fragments;

import android.content.Context;
import com.miniboss.classrank.fragments.CourseRating;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.miniboss.classrank.R;

import java.util.List;
import android.widget.TextView;

public class RatingsAdapter extends RecyclerView.Adapter<RatingsAdapter.RatingViewHolder> {
    private List<CourseRating> ratingsList;
    private LayoutInflater layoutInflater;

    public RatingsAdapter(Context context, List<CourseRating> ratingsList) {
        this.ratingsList = ratingsList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.rating_item, parent, false);
        return new RatingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingViewHolder holder, int position) {
        CourseRating courseRating = ratingsList.get(position);
        holder.userDetails.setText(String.format("User: %s, Rating: %.1f", courseRating.getUserId(), courseRating.getRating()));
        holder.comment.setText(courseRating.getComment());
        holder.timestamp.setText(String.format("Posted on %s at %s", courseRating.getDate(), courseRating.getTime()));
    }

    @Override
    public int getItemCount() {
        return ratingsList.size();
    }

    class RatingViewHolder extends RecyclerView.ViewHolder {
        TextView userDetails;
        TextView comment;
        TextView timestamp;

        public RatingViewHolder(@NonNull View itemView) {
            super(itemView);
            userDetails = itemView.findViewById(R.id.tv_user_details);
            comment = itemView.findViewById(R.id.tv_comment);
            timestamp = itemView.findViewById(R.id.tv_timestamp);
        }
    }
}