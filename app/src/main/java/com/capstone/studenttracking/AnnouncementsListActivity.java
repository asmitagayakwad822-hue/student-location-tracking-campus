package com.capstone.studenttracking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.capstone.studenttracking.adapter.Announcement;
import com.capstone.studenttracking.adapter.AnnouncementAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnouncementsListActivity extends AppCompatActivity {

    private RecyclerView recyclerAnnouncements;
    private FloatingActionButton fabAddAnnouncement;
    private List<Announcement> announcementList;
    private AnnouncementAdapter announcementAdapter;
    private ProgressDialog progressDialog;
    private String userType, department, classYear;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcements_list);

        recyclerAnnouncements = findViewById(R.id.recyclerAnnouncements);
        fabAddAnnouncement = findViewById(R.id.fabAddAnnouncement);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading announcements...");
        progressDialog.setCancelable(false);

        // Get user details
        userType = DBClass.getSingleValue("SELECT CValue FROM Configuration WHERE CName = 'usertype'");
        department = DBClass.getSingleValue("SELECT CValue FROM Configuration WHERE CName = 'department'");
        classYear = DBClass.getSingleValue("SELECT CValue FROM Configuration WHERE CName = 'class_year'");

        // Show FAB only for admin & staff
        if (userType.equals("admin") || userType.equals("staff")) {
            fabAddAnnouncement.setVisibility(View.VISIBLE);
            fabAddAnnouncement.setOnClickListener(v -> {
                Intent intent = new Intent(AnnouncementsListActivity.this, ManageAnnouncementsActivity.class);
                startActivity(intent);
            });
        } else {
            fabAddAnnouncement.setVisibility(View.GONE);
        }

        // Initialize RecyclerView
        announcementList = new ArrayList<>();
        recyclerAnnouncements.setLayoutManager(new LinearLayoutManager(this));
        announcementAdapter = new AnnouncementAdapter(this, announcementList);
        recyclerAnnouncements.setAdapter(announcementAdapter);

        fetchAnnouncements();
    }

    private void fetchAnnouncements() {
        progressDialog.show(); // Show loading

        StringRequest request = new StringRequest(Request.Method.POST, DBClass.urlGetAnnoucement,
                response -> {
                    progressDialog.dismiss();
                    Log.e("Response", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equals("success")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            announcementList.clear(); // Clear old data

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                Announcement announcement = new Announcement(
                                        obj.getString("id"),
                                        obj.getString("title"),
                                        obj.getString("message"),
                                        obj.getString("added_by"),
                                        obj.getString("date"),
                                        obj.getString("time")
                                );
                                announcementList.add(announcement);
                            }
                            announcementAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(AnnouncementsListActivity.this, "No announcements found!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                        e.printStackTrace();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Log.e("Error", error.toString());
                    Toast.makeText(AnnouncementsListActivity.this, "Failed to fetch announcements", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usertype", userType);
                params.put("department", department);
                if (userType.equals("student")) {
                    params.put("class_year", classYear);
                }
                Log.e("Params", params.toString());
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}