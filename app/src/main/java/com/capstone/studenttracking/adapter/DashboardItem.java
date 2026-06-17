package com.capstone.studenttracking.adapter;

public class DashboardItem {
    private String title;
    private int iconResId;

    public DashboardItem(String title, int iconResId) {
        this.title = title;
        this.iconResId = iconResId;
    }

    public String getTitle() {
        return title;
    }

    public int getIconResId() {
        return iconResId;
    }
}
