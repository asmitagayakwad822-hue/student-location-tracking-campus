package com.capstone.studenttracking.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.capstone.studenttracking.DBClass;
import com.capstone.studenttracking.R;
import com.capstone.studenttracking.adapter.Staff;
import com.capstone.studenttracking.adapter.StaffAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ManageStaffActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StaffAdapter staffAdapter;
    private List<Staff> staffList;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_staff);

         recyclerView = findViewById(R.id.recycler_staff);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        staffList = new ArrayList<>();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading staff...");
        progressDialog.setCancelable(false);

        fetchStaff(this);

        // Floating Action Button for Adding Staff
        findViewById(R.id.fab_add_staff).setOnClickListener(v -> {
            Intent intent = new Intent(this, AddStaffActivity.class);
            startActivity(intent);
        });
    }
    public void fetchStaff(Context context) {
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, DBClass.urlgetStaffs,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.e("Response", response);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.getString("status").equals("success")) {
                                JSONArray data = jsonResponse.getJSONArray("data");
                                staffList.clear();

                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject obj = data.getJSONObject(i);
                                    staffList.add(new Staff(
                                            obj.getString("id"),
                                            obj.getString("staff_id"),
                                            obj.getString("name"),
                                            obj.getString("email"),
                                            obj.getString("mobileno"),
                                            obj.getString("department"),
                                            obj.getString("latitude"),
                                            obj.getString("longitude"),
                                            obj.getString("status"),
                                            obj.getString("role"),
                                            obj.getString("password")
                                    ));
                                }

                                staffAdapter = new StaffAdapter(ManageStaffActivity.this, staffList);
                                recyclerView.setAdapter(staffAdapter);
                                staffAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(context, "Failed to fetch staff", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("Exception", e.toString());
                            e.printStackTrace();
                            Toast.makeText(context, "Parsing error", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error", error.toString());
                        progressDialog.dismiss();
                        Toast.makeText(context, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        Volley.newRequestQueue(context).add(request);
    }
}