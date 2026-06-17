package com.capstone.studenttracking.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.capstone.studenttracking.DBClass;
import com.capstone.studenttracking.LostFoundListActivity;
import com.capstone.studenttracking.R;
import com.capstone.studenttracking.ViewImageActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LostFoundAdapter extends RecyclerView.Adapter<LostFoundAdapter.LostFoundViewHolder> {

    private Context context;
    private List<LostFound> lostFoundList;
    private String userType;

    public LostFoundAdapter(Context context, List<LostFound> lostFoundList, String userType) {
        this.context = context;
        this.lostFoundList = lostFoundList;
        this.userType = userType;
    }

    @NonNull
    @Override
    public LostFoundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_lost_found, parent, false);
        return new LostFoundViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LostFoundViewHolder holder, int position) {
        LostFound lostFound = lostFoundList.get(position);
        holder.tvName.setText(lostFound.getName());
        holder.tvDescription.setText(lostFound.getDescription());
        holder.tvStatus.setText("Status: " + lostFound.getStatus());
        holder.tvAddedBy.setText("Added by: " + lostFound.getAddedBy());

        String fullImageUrl = DBClass.url + lostFound.getImageUrl().trim();
        Log.e("Image URL", fullImageUrl);
        Glide.with(context).load(fullImageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error_img)
                .into(holder.ivItemImage);

        holder.ivItemImage.setOnClickListener(v -> {
            Intent intent = new Intent(context, ViewImageActivity.class);
            intent.putExtra("image_url", fullImageUrl);
            context.startActivity(intent);
        });

        // Show "Update Status" button only for admin/staff
        if (userType.equals("admin") || userType.equals("staff")) {
            holder.btnUpdateStatus.setVisibility(View.VISIBLE);
            holder.btnUpdateStatus.setOnClickListener(v -> showStatusDialog(lostFound));
        } else {
            holder.btnUpdateStatus.setVisibility(View.GONE);
        }
        if (holder.tvStatus.getText().toString().equals("Returned")) {
            holder.btnUpdateStatus.setVisibility(View.GONE);
            holder.tvStatus.setTextColor(Color.GREEN);  // Use Color.GREEN
        }
    }

    private void showStatusDialog(LostFound lostFound) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Update Status");

        String[] statusOptions = {"Lost", "Found", "Returned"};
        builder.setItems(statusOptions, (dialog, which) -> {
            String selectedStatus = statusOptions[which];
            updateStatus(lostFound.getId(), selectedStatus);
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    @Override
    public int getItemCount() {
        return lostFoundList.size();
    }

    private void updateStatus(String itemId, String newStatus) {
        StringRequest request = new StringRequest(Request.Method.POST, DBClass.url + "update_lost_found_status.php",
                response -> {
                    Log.e("Response", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equals("success")) {
                            Toast.makeText(context, "Status updated!", Toast.LENGTH_SHORT).show();
                            ((LostFoundListActivity) context).fetchLostFoundItems(); // Refresh list
                        } else {
                            Toast.makeText(context, "Failed to update!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", itemId);
                params.put("status", newStatus);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(request);
    }

    public static class LostFoundViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription, tvStatus, tvAddedBy;
        ImageView ivItemImage;
        Button btnUpdateStatus;

        public LostFoundViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvAddedBy = itemView.findViewById(R.id.tvAddedBy);
            ivItemImage = itemView.findViewById(R.id.ivItemImage);
            btnUpdateStatus = itemView.findViewById(R.id.btnUpdateStatus);
        }
    }
}
