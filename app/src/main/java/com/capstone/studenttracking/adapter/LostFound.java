package com.capstone.studenttracking.adapter;

public class LostFound {
    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private String status;
    private String addedBy;

    public LostFound(String id, String name, String description, String imageUrl, String status, String addedBy) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.status = status;
        this.addedBy = addedBy;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public String getAddedBy() {
        return addedBy;
    }
}
