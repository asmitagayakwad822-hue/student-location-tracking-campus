package com.capstone.studenttracking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.capstone.studenttracking.admin.AdminDashboardActivity;
import com.capstone.studenttracking.staff.StaffDashboardActivity;
import com.capstone.studenttracking.student.StudentDashboardActivity;
import com.capstone.studenttracking.utils.LocationService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class StudentLoginActivity extends AppCompatActivity {

    TextView forgotPassword;
    EditText etxtEmail, etxtPassword;
    ProgressDialog pDialog;
    boolean loginSuccess = false;
    Intent intent;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    String id, email, mobileno, usertype, department, name, enrollment, password, class_year;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);
        forgotPassword = findViewById(R.id.forgotPassword);
        etxtEmail = findViewById(R.id.etxtEmail);
        etxtPassword = findViewById(R.id.etxtPassword);


        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(StudentLoginActivity.this, "Forgot Password Clicked..!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(StudentLoginActivity.this, ForgotPasswordActivity.class);
                intent.putExtra("for", "students");
                startActivity(intent);
            }
        });
    }

    public void btnLoginClick(View view) {
        // Check location permission first
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startLoginAfterPermission(); // If already granted
        }
    }
    public void startLoginAfterPermission() {
         email = etxtEmail.getText().toString();
         password = etxtPassword.getText().toString();
        if (email.equals("")) {
            etxtEmail.setError("Please Enter  Email.");
            etxtEmail.requestFocus();
            return;
        }
        if (password.equals("")) {
            etxtPassword.setError("Please Enter  Password.");
            etxtPassword.requestFocus();
            return;
        }

        pDialog = new ProgressDialog(StudentLoginActivity.this);
        pDialog.setMessage("validating your details, please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();



        StringRequest stringRequest = new StringRequest(Request.Method.POST, DBClass.urlLogin,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject = null;
                        Log.d("Response", ">> " + response);
                        pDialog.dismiss();
                        try {
                            jsonObject = new JSONObject(response);
                            loginSuccess = false;

                            Log.e("onResponse: ", jsonObject.getString("status"));

                            String status = jsonObject.getString("status");

                            switch (status) {
                                case "success":
                                    id = jsonObject.getString("id");
                                    name = jsonObject.getString("name");
                                    email = jsonObject.getString("email");
                                    usertype = jsonObject.getString("role");
                                    mobileno = jsonObject.getString("mobileno");
                                    enrollment = jsonObject.optString("enrollment", "");
                                    department = jsonObject.getString("department");
                                    class_year = jsonObject.getString("class_year");
                                    loginSuccess = true;
                                    break;

                                case "not_approved":
                                    Toast.makeText(getApplicationContext(), "Your account is not approved yet.", Toast.LENGTH_LONG).show();
                                    return;

                                case "failed":
                                default:
                                    Toast.makeText(getApplicationContext(), "Invalid email or password.", Toast.LENGTH_LONG).show();
                                    return;
                            }

                            if (loginSuccess) {
                                String query = "DELETE FROM Configuration";
                                DBClass.execNonQuery(query);

                                query = "INSERT INTO Configuration(CName, CValue) VALUES('usertype', '" + usertype.replace("'", "''") + "')";
                                DBClass.execNonQuery(query);

                                query = "INSERT INTO Configuration(CName, CValue) VALUES('id', '" + id.replace("'", "''") + "')";
                                DBClass.execNonQuery(query);

                                query = "INSERT INTO Configuration(CName, CValue) VALUES('name', '" + name.replace("'", "''") + "')";
                                DBClass.execNonQuery(query);

                                query = "INSERT INTO Configuration(CName, CValue) VALUES('enrollment', '" + enrollment.replace("'", "''") + "')";
                                DBClass.execNonQuery(query);

                                query = "INSERT INTO Configuration(CName, CValue) VALUES('department', '" + department.replace("'", "''") + "')";
                                DBClass.execNonQuery(query);

                                query = "INSERT INTO Configuration(CName, CValue) VALUES('class_year', '" + class_year.replace("'", "''") + "')";
                                DBClass.execNonQuery(query);

                                query = "INSERT INTO Configuration(CName, CValue) VALUES('email', '" + email.replace("'", "''") + "')";
                                DBClass.execNonQuery(query);

                                query = "INSERT INTO Configuration(CName, CValue) VALUES('mobileno', '" + mobileno.replace("'", "''") + "')";
                                DBClass.execNonQuery(query);

                                // Start LocationService
                                Intent i = new Intent(StudentLoginActivity.this, LocationService.class);
                                i.putExtra("user_id", id);
                                i.putExtra("role", usertype);
                                startService(i);

                                intent = new Intent(getApplicationContext(), StudentDashboardActivity.class);
                                startActivity(intent);
                                finish();

                                Toast.makeText(getApplicationContext(), "Login Successfully...", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Email not found...", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "Check Internet Connection...", Toast.LENGTH_LONG).show();
                            Log.e("Exception", ">> " + e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        Log.e("Exception", error.toString());
                        Toast.makeText(getApplicationContext(), "Check Internet Connection...", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                Log.e("Params", params.toString());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void btnRegisterClick(View view) {
        Toast.makeText(this, "Student Registration Clicked..!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(StudentLoginActivity.this, RegistrationActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLoginAfterPermission(); // Proceed with login
            } else {
                Toast.makeText(this, "Location permission is required to login.", Toast.LENGTH_LONG).show();
            }
        }
    }
}