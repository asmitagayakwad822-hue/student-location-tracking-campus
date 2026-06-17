package com.capstone.studenttracking.staff;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.capstone.studenttracking.DBClass;
import com.capstone.studenttracking.R;

import org.json.JSONException;
import org.json.JSONObject;

public class StaffProfileActivity extends AppCompatActivity {

    private TextView tvStaffName, tvStaffId, tvStaffEmail, tvStaffMobile, tvStaffDepartment, tvStaffRole;
    private ProgressDialog progressDialog;
    private static final String TAG = "StaffProfileActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_profile);

        tvStaffName = findViewById(R.id.tv_staff_name);
        tvStaffId = findViewById(R.id.tv_staff_id);
        tvStaffEmail = findViewById(R.id.tv_staff_email);
        tvStaffMobile = findViewById(R.id.tv_staff_mobile);
        tvStaffDepartment = findViewById(R.id.tv_staff_department);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching profile...");
        progressDialog.setCancelable(false);

        String query = "SELECT CValue FROM Configuration WHERE CName = 'id'";
        String staffId = DBClass.getSingleValue(query);

        fetchStaffDetails(staffId);
    }

    private void fetchStaffDetails(String staffId) {
        progressDialog.show();
        String url = DBClass.urlStaffProfile+"?staff_id=" + staffId;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.d(TAG, "Response: " + response);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.getString("status").equals("success")) {
                                JSONObject data = jsonResponse.getJSONObject("data");

                                tvStaffName.setText(data.getString("name"));
                                tvStaffId.setText(data.getString("staff_id"));
                                tvStaffEmail.setText(data.getString("email"));
                                tvStaffMobile.setText(data.getString("contact"));
                                tvStaffDepartment.setText(data.getString("department"));

                            } else {
                                Toast.makeText(StaffProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(StaffProfileActivity.this, "Parsing error", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Log.e(TAG, "Volley Error: " + error.getMessage());
                        Toast.makeText(StaffProfileActivity.this, "Failed to fetch profile: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        Volley.newRequestQueue(this).add(request);
    }
}