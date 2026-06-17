package com.capstone.studenttracking.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.capstone.studenttracking.DBClass;
import com.capstone.studenttracking.R;

import java.util.HashMap;
import java.util.Map;

public class EditStudentActivity extends AppCompatActivity {

    private EditText etEnrollment, etName, etEmail, etMobile, etDepartment, etClassYear;
    private Button btnUpdate;
    private ProgressDialog progressDialog;
    private String studentId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_student);

        etEnrollment = findViewById(R.id.etEnrollment);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etMobile = findViewById(R.id.etMobile);
        etDepartment = findViewById(R.id.etDepartment);
        etClassYear = findViewById(R.id.etClassYear);
        btnUpdate = findViewById(R.id.btnUpdate);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating student...");

        // Get student data from intent
        Intent intent = getIntent();
        studentId = intent.getStringExtra("id");
        etEnrollment.setText(intent.getStringExtra("enrollment"));
        etName.setText(intent.getStringExtra("name"));
        etEmail.setText(intent.getStringExtra("email"));
        etMobile.setText(intent.getStringExtra("mobile"));
        etDepartment.setText(intent.getStringExtra("department"));
        etClassYear.setText(intent.getStringExtra("class_year"));

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStudent();
            }
        });
    }

    private void updateStudent() {
        progressDialog.show();



        StringRequest stringRequest = new StringRequest(Request.Method.POST, DBClass.urlUpdateStudent,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Toast.makeText(EditStudentActivity.this, "Student updated successfully!", Toast.LENGTH_SHORT).show();
                        finish(); // Close activity after updating
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(EditStudentActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", studentId);
                params.put("enrollment", etEnrollment.getText().toString());
                params.put("name", etName.getText().toString());
                params.put("email", etEmail.getText().toString());
                params.put("mobile", etMobile.getText().toString());
                params.put("department", etDepartment.getText().toString());
                params.put("class_year", etClassYear.getText().toString());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}