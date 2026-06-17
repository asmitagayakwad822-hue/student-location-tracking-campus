package com.capstone.studenttracking.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.capstone.studenttracking.DBClass;
import com.capstone.studenttracking.R;
import com.capstone.studenttracking.ViewScheduleImageActivity;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {
    private Context context;
    private List<Schedule> scheduleList;
    private boolean isStaff; // Check if user is staff

    public ScheduleAdapter(Context context, List<Schedule> scheduleList, boolean isStaff) {
        this.context = context;
        this.scheduleList = scheduleList;
        this.isStaff = isStaff;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_schedule, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        Schedule schedule = scheduleList.get(position);
        holder.tvTitle.setText(schedule.getTitle());
        holder.tvDescription.setText(schedule.getDescription());

        String fullImageUrl = DBClass.url + "uploads/" + schedule.getImageUrl();
        Glide.with(context).load(fullImageUrl).placeholder(R.drawable.placeholder).into(holder.ivScheduleImage);

        Log.e("Adapter Data", "Title: " + schedule.getTitle() + ", Image: " + schedule.getImageUrl()); // 🔹 Debugging Log

        // Click to View Full Image
        holder.ivScheduleImage.setOnClickListener(v -> {
            Intent intent = new Intent(context, ViewScheduleImageActivity.class);
            intent.putExtra("image_url", fullImageUrl);
            context.startActivity(intent);
        });

        // Only show delete button for staff
        if (isStaff) {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(v -> deleteSchedule(schedule.getId(), position));
        } else {
            holder.btnDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    public static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription;
        ImageView ivScheduleImage;
        MaterialButton btnDelete;

        public ScheduleViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivScheduleImage = itemView.findViewById(R.id.ivScheduleImage);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    private void deleteSchedule(String scheduleId, int position) {
        String DELETE_URL = "https://yourwebsite.com/api/delete_schedule.php";
        StringRequest request = new StringRequest(Request.Method.POST, DELETE_URL,
                response -> {
                    Toast.makeText(context, "Schedule Deleted Successfully!", Toast.LENGTH_SHORT).show();
                    scheduleList.remove(position);
                    notifyItemRemoved(position);
                },
                error -> Toast.makeText(context, "Error deleting schedule!", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", scheduleId);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }
}
