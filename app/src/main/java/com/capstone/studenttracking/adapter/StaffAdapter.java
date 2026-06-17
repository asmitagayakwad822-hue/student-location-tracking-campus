package com.capstone.studenttracking.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.capstone.studenttracking.DBClass;
import com.capstone.studenttracking.R;
import com.capstone.studenttracking.admin.AddStaffActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.StaffViewHolder> {

    private Context context;
    private List<Staff> staffList;


    public StaffAdapter(Context context, List<Staff> staffList) {
        this.context = context;
        this.staffList = staffList;
    }

    @NonNull
    @Override
    public StaffViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_staff, parent, false);
        return new StaffViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StaffViewHolder holder, int position) {
        Staff staff = staffList.get(position);
        holder.name.setText(staff.getName());
        holder.staffId.setText("Staff ID: " + staff.getStaffId());
        holder.email.setText("Email: " + staff.getEmail());
        holder.mobile.setText("Mobile: " + staff.getMobile());
        holder.department.setText("Department: " + staff.getDepartment());
        holder.status.setText("Status: " + staff.getStatus());

//        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(staff));
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddStaffActivity.class);
            intent.putExtra("editMode", true);
            intent.putExtra("id", staff.getId());
            intent.putExtra("staff_id", staff.getStaffId());
            intent.putExtra("name", staff.getName());
            intent.putExtra("email", staff.getEmail());
            intent.putExtra("mobileno", staff.getMobile());
            intent.putExtra("department", staff.getDepartment());
            intent.putExtra("password", staff.getPassword());  // Only if necessary
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Staff")
                    .setMessage("Are you sure you want to delete " + staff.getName() + "?")
                    .setPositiveButton("Yes", (dialog, which) -> deleteStaff(staff.getId(), position))
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    private void deleteStaff(String staffId, int position) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Deleting...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, DBClass.urlDeleteStaff,
                response -> {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");

                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        if (status.equals("success")) {
                            staffList.remove(position);
                            notifyItemRemoved(position);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(context, "Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", staffId);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }


    @Override
    public int getItemCount() {
        return staffList.size();
    }

    public static class StaffViewHolder extends RecyclerView.ViewHolder {
        TextView name, staffId, email, mobile, department, status;
        Button btnDelete, btnEdit;

        public StaffViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvName);
            staffId = itemView.findViewById(R.id.tvStaffId);
            email = itemView.findViewById(R.id.tvEmail);
            mobile = itemView.findViewById(R.id.tvMobile);
            department = itemView.findViewById(R.id.tvDepartment);
            status = itemView.findViewById(R.id.tvStatus);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}