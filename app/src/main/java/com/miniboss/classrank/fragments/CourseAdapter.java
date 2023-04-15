package com.miniboss.classrank.fragments;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.miniboss.classrank.R;
import com.miniboss.classrank.model.Department;

import java.util.ArrayList;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

    private List<Course> courseList;
    private List<Department> departmentList;
    private LayoutInflater layoutInflater;
    private boolean showAddClassOption = false;

    public CourseAdapter(Context context, List<Course> courseList, List<Department> departmentList) {
        this.courseList = courseList;
        this.departmentList = departmentList;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.list_item_course, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (showAddClassOption && position == getItemCount() - 1) {
            holder.addClassContainer.setVisibility(View.VISIBLE);
            holder.courseDataContainer.setVisibility(View.GONE);
        } else {
            holder.addClassContainer.setVisibility(View.GONE);
            holder.courseDataContainer.setVisibility(View.VISIBLE);

            Course course = courseList.get(position);
            holder.departName.setText(course.getDepartmentName());
            holder.name.setText(course.getDepartmentShortName());
            holder.classNumber.setText(String.valueOf(course.getClassNumber()));
        }

        holder.textAddClass.setOnClickListener(v -> {
            FragmentActivity fragmentActivity = (FragmentActivity) v.getContext();
            if (fragmentActivity instanceof AddClassDialogFragment.OnCourseAddedListener) {
                AddClassDialogFragment addClassDialog = AddClassDialogFragment.newInstance(new ArrayList<>(departmentList), (AddClassDialogFragment.OnCourseAddedListener) fragmentActivity);
                addClassDialog.show(fragmentActivity.getSupportFragmentManager(), "AddClassDialog");
            } else {
                Log.e("CourseAdapter", "FragmentActivity must implement OnCourseAddedListener");
            }
        });
        Log.d("CourseAdapter", "Department list size: " + departmentList.size());
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
        TextView departName, name, classNumber, textAddClass;
        RelativeLayout addClassContainer, courseDataContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            departName = itemView.findViewById(R.id.departName);
            name = itemView.findViewById(R.id.name);
            classNumber = itemView.findViewById(R.id.classNumber);

            addClassContainer = itemView.findViewById(R.id.add_class_container);
            courseDataContainer = itemView.findViewById(R.id.course_data_container);

            textAddClass = itemView.findViewById(R.id.text_add_class);
        }
    }

    public void setShowAddClassOption(boolean showAddClassOption) {
        this.showAddClassOption = showAddClassOption;
    }
}