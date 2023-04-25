package com.miniboss.classrank.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.miniboss.classrank.CourseRatingsActivity;
import com.miniboss.classrank.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {
    private Set<String> favoriteCourseIds;

    private List<Course> courseList;
    private List<Department> departmentList;
    private LayoutInflater layoutInflater;
    private boolean showAddClassOption = false;
    private Drawable starFilledDrawable;

    public CourseAdapter(Context context, List<Course> courseList, List<Department> departmentList) {
        this.courseList = courseList;
        this.departmentList = departmentList;
        this.layoutInflater = LayoutInflater.from(context);
        this.starFilledDrawable = ContextCompat.getDrawable(context, R.drawable.ic_star_filled); // Make sure R.drawable.ic_star_filled references the correct icon
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.list_item_course, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Course course = null;

        if (showAddClassOption && position == getItemCount() - 1) {
            holder.addClassContainer.setVisibility(View.VISIBLE);
            holder.courseDataContainer.setVisibility(View.GONE);
        } else {
            holder.addClassContainer.setVisibility(View.GONE);
            holder.courseDataContainer.setVisibility(View.VISIBLE);

            course = courseList.get(position);
            holder.departName.setText(course.getDepartmentName());
            holder.name.setText(course.getDepartmentShortName());
            holder.classNumber.setText(String.valueOf(course.getClassNumber()));
            holder.professorName.setText(course.getProfessorName());
        }
        if (course != null) {
            Boolean isFavorited = course.isFavorited();
            Log.d("CourseAdapter", "Course favorited: " + isFavorited);
            if (isFavorited != null && isFavorited) {
                holder.favoriteIcon.setImageDrawable(starFilledDrawable);
                holder.favoriteIcon.setVisibility(View.VISIBLE);
            } else {
                holder.favoriteIcon.setVisibility(View.GONE);
            }
        } else {
            holder.favoriteIcon.setVisibility(View.GONE);
        }

        final Course currentCourse = course;

        holder.textAddClass.setOnClickListener(v -> {
            FragmentActivity fragmentActivity = (FragmentActivity) v.getContext();
            if (fragmentActivity instanceof AddClassDialogFragment.OnCourseAddedListener) {
                AddClassDialogFragment addClassDialog = AddClassDialogFragment.newInstance(new ArrayList<>(departmentList), (AddClassDialogFragment.OnCourseAddedListener) fragmentActivity);
                addClassDialog.show(fragmentActivity.getSupportFragmentManager(), "AddClassDialog");
            } else {
                Log.e("CourseAdapter", "FragmentActivity must implement OnCourseAddedListener");
            }
        });
        holder.courseDataContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentCourse != null) { // Update this line
                    Intent intent = new Intent(v.getContext(), com.miniboss.classrank.CourseRatingsActivity.class);
                    intent.putExtra("course_id", currentCourse.getId()); // Update this line
                    intent.putExtra("class_number", currentCourse.getClassNumber()); // Update this line
                    intent.putExtra("department_name_short", currentCourse.getDepartmentShortName()); // Update this line
                    intent.putExtra("professor_name", currentCourse.getProfessorName()); // Update this line
                    v.getContext().startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return showAddClassOption ? courseList.size() + 1 : courseList.size();
    }

    public void filterList(List<Course> filteredList) {
        courseList = filteredList;
        setShowAddClassOption(filteredList.isEmpty());
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView departName, name, classNumber, textAddClass, professorName;
        ImageView favoriteIcon;
        RelativeLayout addClassContainer, courseDataContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            departName = itemView.findViewById(R.id.departName);
            name = itemView.findViewById(R.id.name);
            classNumber = itemView.findViewById(R.id.classNumber);
            professorName = itemView.findViewById(R.id.professorName);
            favoriteIcon = itemView.findViewById(R.id.favoriteIcon);

            addClassContainer = itemView.findViewById(R.id.add_class_container);
            courseDataContainer = itemView.findViewById(R.id.course_data_container);

            textAddClass = itemView.findViewById(R.id.text_add_class);
        }
    }

    public void setShowAddClassOption(boolean showAddClassOption) {
        this.showAddClassOption = showAddClassOption;
    }
}