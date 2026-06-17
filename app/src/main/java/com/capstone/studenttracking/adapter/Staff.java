package com.capstone.studenttracking.adapter;

public class Staff {
    private String id, staffId, name, email, mobile, department, latitude, longitude, status, role, password;

    public Staff(String id, String staffId, String name, String email, String mobile, String department, String latitude, String longitude, String status, String role, String password) {
        this.id = id;
        this.staffId = staffId;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.department = department;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.role = role;
        this.password = password;
    }

    public String getId() { return id; }
    public String getStaffId() { return staffId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getMobile() { return mobile; }
    public String getDepartment() { return department; }
    public String getLatitude() { return latitude; }
    public String getLongitude() { return longitude; }
    public String getStatus() { return status; }
    public String getRole() { return role; }
    public String getPassword() { return password; }
}
