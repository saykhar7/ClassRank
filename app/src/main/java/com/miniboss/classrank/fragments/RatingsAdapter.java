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

import android.widget.LinearLayout;
import android.widget.TextView;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

public class RatingsAdapter extends RecyclerView.Adapter<RatingsAdapter.RatingViewHolder> {
    private List<CourseRating> ratingsList;
    private LayoutInflater layoutInflater;
    private String currentUserId;

    public RatingsAdapter(Context context, List<CourseRating> ratingsList, String currentUserId) {
        this.ratingsList = ratingsList;
        this.layoutInflater = LayoutInflater.from(context);
        this.currentUserId = currentUserId;
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
        String userComment = courseRating.getComment();

        if (userComment == null || userComment.isEmpty()) {
            SpannableStringBuilder noCommentBuilder = new SpannableStringBuilder("No comment");
            noCommentBuilder.setSpan(new ForegroundColorSpan(Color.GRAY), 0, noCommentBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.comment.setText(noCommentBuilder);
        } else {
            holder.comment.setText(userComment);
        }

        holder.timestamp.setText(String.format("Posted on %s at %s", courseRating.getDate(), courseRating.getTime()));

        String userName = courseRating.getUserId() == null ? "" : courseRating.getUserId();
        String ratingString = String.format(", Rating: %.1f", courseRating.getRating());

        if (userName != null && currentUserId != null && userName.equals(currentUserId)) {
            userName = "You";
            holder.ratingCardLayout.setBackgroundResource(R.drawable.highlighted_rating_border);
        } else {
            holder.ratingCardLayout.setBackgroundResource(0); // reset the background
        }

        if (position == getItemCount() - 1) {
            holder.divider.setVisibility(View.GONE);
        } else {
            holder.divider.setVisibility(View.VISIBLE);
        }

        SpannableStringBuilder userDetailsBuilder = new SpannableStringBuilder();

        if (!userName.isEmpty()) {
            userDetailsBuilder.append(userName);
            userDetailsBuilder.setSpan(new StyleSpan(Typeface.BOLD), 0, userName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        int ratingStartIndex = userDetailsBuilder.length();
        userDetailsBuilder.append(ratingString);

        userDetailsBuilder.setSpan(new ForegroundColorSpan(Color.GRAY), ratingStartIndex, userDetailsBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.userDetails.setText(userDetailsBuilder);
    }

    @Override
    public int getItemCount() {
        return ratingsList.size();
    }

    class RatingViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ratingCardLayout;
        TextView userDetails;
        TextView comment;
        TextView timestamp;
        View divider;

        public RatingViewHolder(@NonNull View itemView) {
            super(itemView);
            ratingCardLayout = itemView.findViewById(R.id.ratingCardLayout);
            userDetails = itemView.findViewById(R.id.tv_user_details);
            comment = itemView.findViewById(R.id.tv_comment);
            timestamp = itemView.findViewById(R.id.tv_timestamp);
            divider = itemView.findViewById(R.id.divider);
        }
    }
}