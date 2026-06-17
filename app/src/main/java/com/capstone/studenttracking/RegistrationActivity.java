package com.capstone.studenttracking;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private EditText etxtName, etxtEmail, etxtMobileno, etxtEnroll, etxtPassword;
    private Spinner spnDept, spnClass;
    private Button btnSignUp;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        etxtName = findViewById(R.id.etxtName);
        etxtEmail = findViewById(R.id.etxtEmail);
        etxtMobileno = findViewById(R.id.etxtMobileno);
        etxtEnroll = findViewById(R.id.etxtEnroll);
        etxtPassword = findViewById(R.id.etxtPassword);
        spnDept = findViewById(R.id.spnDept);
        spnClass = findViewById(R.id.spnClass);
        btnSignUp = findViewById(R.id.btnSignUp);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering...");

        // Set Class Spinner Adapter
        ArrayAdapter<CharSequence> classAdapter = ArrayAdapter.createFromResource(this,
                R.array.class_array, R.layout.spinner_item);
        classAdapter.setDropDownViewResource(R.layout.spinner_item);
        spnClass.setAdapter(classAdapter);

        // Set Department Spinner Adapter
        ArrayAdapter<CharSequence> deptAdapter = ArrayAdapter.createFromResource(this,
                R.array.department_array, R.layout.spinner_item);
        deptAdapter.setDropDownViewResource(R.layout.spinner_item);
        spnDept.setAdapter(deptAdapter);

        btnSignUp.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        final String name = etxtName.getText().toString().trim();
        final String email = etxtEmail.getText().toString().trim();
        final String mobile = etxtMobileno.getText().toString().trim();
        final String enrollment = etxtEnroll.getText().toString().trim();
        final String password = etxtPassword.getText().toString().trim();
        final String department = spnDept.getSelectedItem().toString();
        final String studentClass = spnClass.getSelectedItem().toString();

        if (name.isEmpty() || email.isEmpty() || mobile.isEmpty() || enrollment.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, DBClass.urlRegistration,
                response -> {
                    progressDialog.dismiss();
                    Toast.makeText(RegistrationActivity.this, response, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(RegistrationActivity.this, StudentLoginActivity.class);
                    startActivity(intent);
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(RegistrationActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("email", email);
                params.put("mobileno", mobile);
                params.put("enrollment", enrollment);
                params.put("password", password);
                params.put("department", department);
                params.put("class", studentClass); // Pass Class as 'FY', 'SY', or 'TY'
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void btnLoginClick(View view) {
        Intent intent = new Intent(RegistrationActivity.this, StudentLoginActivity.class);
        startActivity(intent);
    }
}