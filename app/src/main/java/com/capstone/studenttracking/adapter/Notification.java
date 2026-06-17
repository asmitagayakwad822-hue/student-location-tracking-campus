package com.capstone.studenttracking.adapter;

public class Notification {

    public String id;
    private String target; // "Students" or "Staff"
    private String message;
    private String timestamp;

    public Notification(String id, String target, String message, String timestamp) {
        this.id = id;
        this.target = target;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getTarget() {
        return target;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }
}

