package com.capstone.studenttracking.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.capstone.studenttracking.DBClass;
import com.capstone.studenttracking.R;
import com.capstone.studenttracking.adapter.Student;
import com.capstone.studenttracking.adapter.StudentAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageStudentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StudentAdapter studentAdapter;
    private List<Student> studentList;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_students);
        recyclerView = findViewById(R.id.recycler_students);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        studentList = new ArrayList<>();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading students...");
        progressDialog.setCancelable(false);

        fetchStudents(this);
    }

    private void fetchStudents(Context context) {
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, DBClass.urlgetStudents,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.getString("status").equals("success")) {
                                JSONArray data = jsonResponse.getJSONArray("data");
                                studentList.clear();

                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject obj = data.getJSONObject(i);
                                    studentList.add(new Student(
                                            obj.getString("id"),
                                            obj.getString("enrollment"),
                                            obj.getString("name"),
                                            obj.getString("email"),
                                            obj.getString("mobileno"),
                                            obj.getString("class_year"),
                                            obj.getString("department"),
                                            obj.getString("latitude"),
                                            obj.getString("longitude"),
                                            obj.getString("status"),
                                            obj.getString("role")
                                    ));
                                }

                                studentAdapter = new StudentAdapter(context, studentList, new StudentAdapter.OnStudentActionListener() {
                                    @Override
                                    public void onApproveClick(Student student) {
                                        approveStudent(student.getId());
                                    }

                                    @Override
                                    public void onEditClick(Student student) {
                                        //Toast.makeText(context, "Edit: " + student.getName(), Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(context, EditStudentActivity.class);
                                        intent.putExtra("id", student.getId());
                                        intent.putExtra("enrollment", student.getEnrollment());
                                        intent.putExtra("name", student.getName());
                                        intent.putExtra("email", student.getEmail());
                                        intent.putExtra("mobile", student.getMobile());
                                        intent.putExtra("department", student.getDepartment());
                                        intent.putExtra("class_year", student.getClass_year());
                                        context.startActivity(intent);
                                    }
                                });
                                recyclerView.setAdapter(studentAdapter);
                            } else {
                                Toast.makeText(context, "Failed to fetch students", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Parsing error", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(context, "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        Volley.newRequestQueue(context).add(request);
    }

    private void approveStudent(String studentId) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, DBClass.urlApproveStudent,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");

                        if (status.equals("success")) {
                            Toast.makeText(ManageStudentsActivity.this, "Student Approved Successfully", Toast.LENGTH_SHORT).show();
                            // You can refresh the list or update UI
                        } else {
                            Toast.makeText(ManageStudentsActivity.this, "Approval failed: " + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ManageStudentsActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(ManageStudentsActivity.this, "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", studentId);
                params.put("status", "approved"); // Updating status to Active
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(ManageStudentsActivity.this);
        requestQueue.add(stringRequest);
    }
}