package com.capstone.studenttracking.adapter;

public class Announcement {
    private String id;
    private String title;
    private String message;
    private String addedBy;
    private String date;
    private String time;

    public Announcement(String id, String title, String message, String addedBy, String date, String time) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.addedBy = addedBy;
        this.date = date;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
