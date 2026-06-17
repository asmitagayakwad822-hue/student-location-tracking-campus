package com.capstone.studenttracking.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.studenttracking.R;
import com.capstone.studenttracking.admin.EditStudentActivity;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private Context context;
    private List<Student> studentList;
    private OnStudentActionListener listener;

    public interface OnStudentActionListener {
        void onApproveClick(Student student);
        void onEditClick(Student student);
    }

    public StudentAdapter(Context context, List<Student> studentList, OnStudentActionListener listener) {
        this.context = context;
        this.studentList = studentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = studentList.get(position);
        holder.name.setText(student.getName());
        holder.enrollment.setText("Enrollment: " + student.getEnrollment());
        holder.email.setText("Email: " + student.getEmail());
        holder.mobile.setText("Mobile: " + student.getMobile());
        holder.department.setText("Department: " + student.getDepartment());
        holder.status.setText("Status: " + student.getStatus());

        if (!student.getStatus().equalsIgnoreCase("pending")) {
            holder.btnApprove.setVisibility(View.GONE);
        } else {
            holder.btnApprove.setVisibility(View.VISIBLE);
        }

        holder.btnApprove.setOnClickListener(v -> listener.onApproveClick(student));
        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(student));
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView name, enrollment, email, mobile, department, status;
        Button btnApprove, btnEdit;

        public StudentViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvName);
            enrollment = itemView.findViewById(R.id.tvEnrollment);
            email = itemView.findViewById(R.id.tvEmail);
            mobile = itemView.findViewById(R.id.tvMobile);
            department = itemView.findViewById(R.id.tvDepartment);
            status = itemView.findViewById(R.id.tvStatus);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}