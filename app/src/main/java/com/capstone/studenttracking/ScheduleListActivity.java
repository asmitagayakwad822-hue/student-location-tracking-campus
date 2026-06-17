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
import com.capstone.studenttracking.adapter.Schedule;
import com.capstone.studenttracking.adapter.ScheduleAdapter;
import com.capstone.studenttracking.staff.ShareScheduleActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleListActivity extends AppCompatActivity {

    private RecyclerView recyclerSchedule;
    private FloatingActionButton fabAddSchedule;
    private List<Schedule> scheduleList;
    private ScheduleAdapter scheduleAdapter;
    private ProgressDialog progressDialog;
    private boolean isStaff;
    private String userType;
    String classYear="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_list);

        recyclerSchedule = findViewById(R.id.recyclerSchedule);
        fabAddSchedule = findViewById(R.id.fabAddSchedule);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading schedules...");
        progressDialog.setCancelable(false);

        // Get user type from session (stored in SharedPreferences or DB)
        userType = DBClass.getSingleValue("SELECT CValue FROM Configuration WHERE CName = 'usertype'");

        // Set visibility of Floating Action Button
        if (userType.equals("staff")) {
            fabAddSchedule.setVisibility(View.VISIBLE);
            fabAddSchedule.setOnClickListener(v -> {
                Intent intent = new Intent(ScheduleListActivity.this, ShareScheduleActivity.class);
                startActivity(intent);
            });
        } else {
            fabAddSchedule.setVisibility(View.GONE);
        }

        // Setup RecyclerView
        scheduleList = new ArrayList<>();
        scheduleAdapter = new ScheduleAdapter(this, scheduleList, userType.equals("staff"));
        recyclerSchedule.setLayoutManager(new LinearLayoutManager(this));
        recyclerSchedule.setAdapter(scheduleAdapter);

        fetchSchedules();

        Log.e("Schedule List Size", String.valueOf(scheduleList.size()));
    }

    private void fetchSchedules() {
        progressDialog.show();


        String staff_id = DBClass.getSingleValue("SELECT CValue FROM Configuration WHERE CName = 'id'");
        String department = DBClass.getSingleValue("SELECT CValue FROM Configuration WHERE CName = 'department'");

        if (userType.equals("student")) {
            classYear = DBClass.getSingleValue("SELECT CValue FROM Configuration WHERE CName = 'class_year'");
        }

        StringRequest request = new StringRequest(Request.Method.POST, DBClass.urlGetSchedule,
                response -> {
                    Log.e("API Response", response);
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equals("success")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            scheduleList.clear();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject scheduleObj = jsonArray.getJSONObject(i);
                                Schedule schedule = new Schedule(
                                        scheduleObj.getString("id"),
                                        scheduleObj.getString("title"),
                                        scheduleObj.getString("description"),
                                        scheduleObj.getString("image_url")
                                );
                                scheduleList.add(schedule);
                            }

                            Log.e("Schedule List Size", String.valueOf(scheduleList.size())); // 🔹 Debugging Log
                            scheduleAdapter.notifyDataSetChanged();
                            recyclerSchedule.post(() -> recyclerSchedule.invalidate());
                        } else {
                            Toast.makeText(ScheduleListActivity.this, "No schedules found!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.e("Volley Error", error.toString());
                    progressDialog.dismiss();
                    Toast.makeText(ScheduleListActivity.this, "Failed to fetch schedules", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usertype", userType);
                params.put("staff_id", staff_id);
                params.put("department", department);

                if (userType.equals("student")) {
                    params.put("class_year", classYear);
                }

                Log.e("Params Sent", params.toString()); // 🔹 Debugging Log
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

}