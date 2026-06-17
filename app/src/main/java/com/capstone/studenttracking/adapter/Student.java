package com.capstone.studenttracking.adapter;

public class Student {
    private String id;
    private String enrollment;
    private String name;
    private String email;
    private String mobile;
    private String department;
    private String latitude;
    private String longitude;
    private String status;
    private String role;



    private String class_year;

    public Student(String id, String enrollment, String name, String email, String mobile, String class_year, String department, String latitude, String longitude, String status, String role) {
        this.id = id;
        this.enrollment = enrollment;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.class_year = class_year;
        this.department = department;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.role = role;

    }

    public String getId() { return id; }
    public String getEnrollment() { return enrollment; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getMobile() { return mobile; }
    public String getDepartment() { return department; }
    public String getLatitude() { return latitude; }
    public String getLongitude() { return longitude; }
    public String getStatus() { return status; }
    public String getRole() { return role; }
    public String getClass_year() {
        return class_year;
    }
}
