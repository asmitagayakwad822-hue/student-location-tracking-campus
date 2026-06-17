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
import com.capstone.studenttracking.adapter.LostFound;
import com.capstone.studenttracking.adapter.LostFoundAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LostFoundListActivity extends AppCompatActivity {

    private RecyclerView recyclerLostFound;
    private FloatingActionButton fabReportLostFound;
    private List<LostFound> lostFoundList;
    private LostFoundAdapter lostFoundAdapter;
    private ProgressDialog progressDialog;
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_found_list);

        recyclerLostFound = findViewById(R.id.recyclerLostFound);
        fabReportLostFound = findViewById(R.id.fabReportLostFound);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading items...");
        progressDialog.setCancelable(false);

        // Get user type
        userType = DBClass.getSingleValue("SELECT CValue FROM Configuration WHERE CName = 'usertype'");

        // Only students can add new lost/found reports
        if (userType.equals("student")) {
            fabReportLostFound.setVisibility(View.VISIBLE);
            fabReportLostFound.setOnClickListener(v -> {
                Intent intent = new Intent(LostFoundListActivity.this, ReportLostFoundActivity.class);
                startActivity(intent);
            });
        } else {
            fabReportLostFound.setVisibility(View.GONE);
        }

        // Initialize RecyclerView
        lostFoundList = new ArrayList<>();
        recyclerLostFound.setLayoutManager(new LinearLayoutManager(this));
        lostFoundAdapter = new LostFoundAdapter(this, lostFoundList, userType);
        recyclerLostFound.setAdapter(lostFoundAdapter);

        fetchLostFoundItems();
    }

    public void fetchLostFoundItems() {
        progressDialog.show(); // Show loading

        StringRequest request = new StringRequest(Request.Method.POST, DBClass.url + "get_lost_found.php",
                response -> {
                    progressDialog.dismiss();
                    Log.e("Response", response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equals("success")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            lostFoundList.clear(); // Clear old data

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                LostFound lostFound = new LostFound(
                                        obj.getString("id"),
                                        obj.getString("name"),
                                        obj.getString("description"),
                                        obj.getString("image_url"),
                                        obj.getString("status"),
                                        obj.getString("added_by")
                                );
                                lostFoundList.add(lostFound);
                            }
                            lostFoundAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(LostFoundListActivity.this, "No items found!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                        e.printStackTrace();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Log.e("Error", error.toString());
                    Toast.makeText(LostFoundListActivity.this, "Failed to fetch items", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usertype", userType);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}