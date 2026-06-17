package com.capstone.studenttracking.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.capstone.studenttracking.R;
import java.util.List;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.AnnouncementViewHolder> {

    private Context context;
    private List<Announcement> announcementList;

    public AnnouncementAdapter(Context context, List<Announcement> announcementList) {
        this.context = context;
        this.announcementList = announcementList;
    }

    @NonNull
    @Override
    public AnnouncementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_announcement, parent, false);
        return new AnnouncementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementViewHolder holder, int position) {
        Announcement announcement = announcementList.get(position);
        holder.tvTitle.setText(announcement.getTitle());
        holder.tvMessage.setText(announcement.getMessage());
        holder.tvAddedBy.setText("Added by: " + announcement.getAddedBy());
        holder.tvDate.setText("Date: " + announcement.getDate());
        holder.tvTime.setText("Time: " + announcement.getTime());
    }

    @Override
    public int getItemCount() {
        return announcementList.size();
    }

    public static class AnnouncementViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMessage, tvAddedBy, tvDate, tvTime;

        public AnnouncementViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvAddedBy = itemView.findViewById(R.id.tvAddedBy);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}
