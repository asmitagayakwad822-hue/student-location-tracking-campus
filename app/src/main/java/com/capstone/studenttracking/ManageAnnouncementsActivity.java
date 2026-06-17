package com.capstone.studenttracking;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ManageAnnouncementsActivity extends AppCompatActivity {

    private EditText etTitle, etMessage;
    private Spinner spUserType, spDepartment, spClassYear;
    private Button btnSendAnnouncement;
    private ProgressDialog progressDialog;
    private String loggedInUserType, loggedInDepartment, loggedInName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_announcements);

        etTitle = findViewById(R.id.etTitle);
        etMessage = findViewById(R.id.etMessage);
        spUserType = findViewById(R.id.spUserType);
        spDepartment = findViewById(R.id.spDepartment);
        spClassYear = findViewById(R.id.spClassYear);
        btnSendAnnouncement = findViewById(R.id.btnSendAnnouncement);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending announcement...");
        progressDialog.setCancelable(false);

        // Retrieve stored user details from SQLite
        loggedInUserType = DBClass.getSingleValue("SELECT CValue FROM Configuration WHERE CName = 'usertype'");
        loggedInDepartment = DBClass.getSingleValue("SELECT CValue FROM Configuration WHERE CName = 'department'");
        loggedInName = DBClass.getSingleValue("SELECT CValue FROM Configuration WHERE CName = 'name'");

        // Populate user type options
        if (loggedInUserType.equals("admin")) {
            ArrayAdapter<CharSequence> userTypeAdapter = ArrayAdapter.createFromResource(this, R.array.user_type_admin, R.layout.spinner_item);
            spUserType.setAdapter(userTypeAdapter);
        } else {
            ArrayAdapter<CharSequence> userTypeAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, new String[]{"Students"});
            spUserType.setAdapter(userTypeAdapter);
            spUserType.setEnabled(false); // Prevent changing selection for staff

            spDepartment.setEnabled(false);
            spDepartment.setVisibility(View.GONE); // Hide department spinner for staff
            spClassYear.setVisibility(View.VISIBLE);
            ArrayAdapter<CharSequence> classYearAdapter = ArrayAdapter.createFromResource(this, R.array.class_array, R.layout.spinner_item);
            spClassYear.setAdapter(classYearAdapter);
        }

        btnSendAnnouncement.setOnClickListener(v -> sendAnnouncement());
    }

    private void sendAnnouncement() {
        final String title = etTitle.getText().toString().trim();
        final String message = etMessage.getText().toString().trim();
        final String userType = spUserType.getSelectedItem().toString();
        final String classYear = (spClassYear.getSelectedItem() != null) ? spClassYear.getSelectedItem().toString() : "";

        if (title.isEmpty() || message.isEmpty()) {
            Toast.makeText(this, "Please enter title and message!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Determine department & class_year based on user type
        String finalDepartment = loggedInUserType.equals("staff") ? loggedInDepartment : "";
        String finalClassYear = loggedInUserType.equals("staff") ? classYear : (userType.equals("Students") ? classYear : "");

        if (loggedInUserType.equals("staff") && finalClassYear.isEmpty()) {
            Toast.makeText(this, "Please select a class year!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show(); // Show loading

        StringRequest request = new StringRequest(Request.Method.POST, DBClass.urlAddAnnouncement,
                response -> {
                    progressDialog.dismiss();
                    Log.e("Response", response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");
                        String message1 = jsonResponse.getString("message");

                        if (status.equals("success")) {
                            Toast.makeText(ManageAnnouncementsActivity.this, message1, Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(ManageAnnouncementsActivity.this, message1, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(ManageAnnouncementsActivity.this, "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Log.e("Volley Error", error.toString());
                    Toast.makeText(ManageAnnouncementsActivity.this, "Failed to send announcement", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("title", title);
                params.put("message", message);
                params.put("usertype", loggedInUserType);
                params.put("added_by", loggedInName);

                if (loggedInUserType.equals("staff")) {
                    params.put("department", finalDepartment);
                    params.put("class_year", finalClassYear);
                }

                Log.e("Params", params.toString());
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
