package com.capstone.studenttracking.admin;

import static androidx.fragment.app.FragmentManager.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.util.HashMap;
import java.util.Map;

public class AddStaffActivity extends AppCompatActivity {

    private EditText etStaffName, etStaffId, etStaffEmail, etStaffMobile, etStaffRole,etStaffPassword;
    private Spinner spDepartment;
    private Button btnSubmitStaff;
    private ProgressDialog progressDialog;

    private boolean isEditMode = false;
    private String staffId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_staff);

        etStaffName = findViewById(R.id.et_staff_name);
        etStaffId = findViewById(R.id.et_staff_id);
        etStaffEmail = findViewById(R.id.et_staff_email);
        etStaffMobile = findViewById(R.id.et_staff_mobile);
        etStaffPassword = findViewById(R.id.et_staff_password);
        spDepartment = findViewById(R.id.spnDept);
        btnSubmitStaff = findViewById(R.id.btn_submit_staff);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Adding staff...");
        progressDialog.setCancelable(false);

        ArrayAdapter<CharSequence> deptAdapter = ArrayAdapter.createFromResource(this,
                R.array.department_array, R.layout.spinner_item);
        deptAdapter.setDropDownViewResource(R.layout.spinner_item);
        spDepartment.setAdapter(deptAdapter);

        // **Check if the activity is opened for editing**
        Intent intent = getIntent();
        if (intent.hasExtra("editMode")) {
            isEditMode = intent.getBooleanExtra("editMode", false);
            staffId = intent.getStringExtra("id");  // ID from intent

            // Pre-fill form with existing details
            etStaffId.setText(intent.getStringExtra("staff_id"));
            etStaffName.setText(intent.getStringExtra("name"));
            etStaffEmail.setText(intent.getStringExtra("email"));
            etStaffMobile.setText(intent.getStringExtra("mobileno"));
            etStaffPassword.setText(intent.getStringExtra("password")); // Only if sending password

            // Disable staff ID editing in edit mode
            etStaffId.setEnabled(false);

            btnSubmitStaff.setText("Update Staff");
        }

        btnSubmitStaff.setOnClickListener(v -> saveStaff());
    }

    private void saveStaff() {
        final String name = etStaffName.getText().toString().trim();
        final String staff_id = etStaffId.getText().toString().trim();
        final String email = etStaffEmail.getText().toString().trim();
        final String mobile = etStaffMobile.getText().toString().trim();
        final String department = spDepartment.getSelectedItem().toString();
        final String password = etStaffPassword.getText().toString().trim();

        if (name.isEmpty() || staff_id.isEmpty() || email.isEmpty() || mobile.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();



        StringRequest request = new StringRequest(Request.Method.POST, DBClass.urlAddStaff,
                response -> {
                    progressDialog.dismiss();
                    Log.e("Response: ", response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");
                        String message = jsonResponse.getString("message");

                        Toast.makeText(AddStaffActivity.this, message, Toast.LENGTH_SHORT).show();

                        if (status.equals("success")) finish();


                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(AddStaffActivity.this, "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Log.e("Volley Error: ", error.getMessage());
                    Toast.makeText(AddStaffActivity.this, "Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                if (isEditMode) params.put("id", staffId); // Only pass for edit mode
                params.put("staff_id", staff_id);
                params.put("name", name);
                params.put("email", email);
                params.put("mobileno", mobile);
                params.put("department", department);
                params.put("password", password);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}