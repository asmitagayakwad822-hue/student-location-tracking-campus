package com.capstone.studenttracking.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.studenttracking.AnnouncementsListActivity;
import com.capstone.studenttracking.ManageAnnouncementsActivity;
import com.capstone.studenttracking.R;
import com.capstone.studenttracking.TrackingActivity;
import com.capstone.studenttracking.admin.ManageStaffActivity;
import com.capstone.studenttracking.admin.ManageStudentsActivity;
import com.capstone.studenttracking.NotificationsActivity;

import java.util.List;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.DashboardViewHolder> {

    private List<DashboardItem> items;
    private Context context;

    public DashboardAdapter(Context context, List<DashboardItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public DashboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dashboard_card, parent, false);
        return new DashboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardViewHolder holder, int position) {
        DashboardItem item = items.get(position);
        holder.cardTitle.setText(item.getTitle());
        holder.cardIcon.setImageResource(item.getIconResId());

        // Add click listener for navigation
        holder.itemView.setOnClickListener(v -> {
            switch (item.getTitle()) {
                case "Manage Students":
                    context.startActivity(new Intent(context, ManageStudentsActivity.class));
                    break;
                case "Manage Staff":
                    context.startActivity(new Intent(context, ManageStaffActivity.class));
                    break;
                case "Track Locations":
                    context.startActivity(new Intent(context, TrackingActivity.class));
                    break;
                case "Annoucements":
                    context.startActivity(new Intent(context, AnnouncementsListActivity.class));
                    break;
                // Add more cases as needed
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class DashboardViewHolder extends RecyclerView.ViewHolder {
        TextView cardTitle;
        ImageView cardIcon;

        public DashboardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardTitle = itemView.findViewById(R.id.card_title);
            cardIcon = itemView.findViewById(R.id.card_icon);
        }
    }
}