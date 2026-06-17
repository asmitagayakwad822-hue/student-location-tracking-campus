package com.capstone.studenttracking;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TrackingActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Spinner spinnerUserType;
    private AutoCompleteTextView autoCompleteSearch;
    private Button btnViewLocation;
    private TextView tvStatus;

    // College Location
    private final double COLLEGE_LAT = 17.028399;
    private final double COLLEGE_LON = 74.620898;
    private final float RADIUS_METERS = 100; // 100 meters radius

    private List<String> studentsList = new ArrayList<>();
    private List<String> staffList = new ArrayList<>();
    private double selectedUserLat = 0, selectedUserLon = 0;
    private String userType; // User type from database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        spinnerUserType = findViewById(R.id.spinnerUserType);
        autoCompleteSearch = findViewById(R.id.autoCompleteSearch);
        btnViewLocation = findViewById(R.id.btnViewLocation);
        tvStatus = findViewById(R.id.tvStatus);

        // Fetch User Type from Configuration table
        userType = DBClass.getSingleValue("SELECT CValue FROM Configuration WHERE CName = 'usertype'");

        setupUserTypeSpinner(); // Setup user type options based on role

        fetchActiveStudents();
        fetchStaff();

        btnViewLocation.setOnClickListener(v -> {
            String selectedUser = autoCompleteSearch.getText().toString();
            if (!selectedUser.isEmpty()) {
                parseLocationFromSelection(selectedUser);
                checkLocationStatus();
            } else {
                Toast.makeText(this, "Please select a student or staff", Toast.LENGTH_SHORT).show();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void setupUserTypeSpinner() {
        ArrayAdapter<String> adapter;
        if (userType.equals("admin")) {
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Student", "Staff"});
        } else if (userType.equals("staff")) {
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Student"});
            spinnerUserType.setVisibility(View.GONE); // Hide spinner for staff
        } else { // If user is Student
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Staff"});
            spinnerUserType.setVisibility(View.GONE); // Hide spinner for students
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUserType.setAdapter(adapter);

        // Set text color to black
        spinnerUserType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                String selectedType = spinnerUserType.getSelectedItem().toString();
                loadSearchDropdown(selectedType);

                // **Set Hint Based on Selection**
                if (selectedType.equals("Student")) {
                    autoCompleteSearch.setHint("Search Student...");
                } else {
                    autoCompleteSearch.setHint("Search Staff...");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // **Set Default Hint Based on User Type**
        if (userType.equals("staff")) {
            loadSearchDropdown("Student");
            autoCompleteSearch.setHint("Search Student...");
        } else if (userType.equals("student")) {
            loadSearchDropdown("Staff");
            autoCompleteSearch.setHint("Search Staff...");
        }
    }
    private void fetchActiveStudents() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, DBClass.urlGetAllStudents,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equals("success")) {
                            JSONArray studentsArray = jsonObject.getJSONArray("data");
                            studentsList.clear();

                            for (int i = 0; i < studentsArray.length(); i++) {
                                JSONObject student = studentsArray.getJSONObject(i);
                                String studentInfo = student.getString("name") + " - " +
                                        student.getString("latitude") + ", " + student.getString("longitude");
                                studentsList.add(studentInfo);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Toast.makeText(TrackingActivity.this, "Error fetching students", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void fetchStaff() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, DBClass.urlGetAllStaff,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equals("success")) {
                            JSONArray staffArray = jsonObject.getJSONArray("data");
                            staffList.clear();

                            for (int i = 0; i < staffArray.length(); i++) {
                                JSONObject staff = staffArray.getJSONObject(i);
                                String staffInfo = staff.getString("name") + " - " +
                                        staff.getString("latitude") + ", " + staff.getString("longitude");
                                staffList.add(staffInfo);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Toast.makeText(TrackingActivity.this, "Error fetching staff", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void loadSearchDropdown(String userType) {
        ArrayAdapter<String> adapter;
        if (userType.equals("Student")) {
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, studentsList);
        } else {
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, staffList);
        }
        autoCompleteSearch.setAdapter(adapter);
        autoCompleteSearch.setThreshold(1);
    }

    private void parseLocationFromSelection(String selectedUser) {
        try {
            String[] parts = selectedUser.split(" - ");
            String[] latLon = parts[1].split(", ");
            selectedUserLat = Double.parseDouble(latLon[0]);
            selectedUserLon = Double.parseDouble(latLon[1]);
        } catch (Exception e) {
            Toast.makeText(this, "Invalid location data", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkLocationStatus() {
        float[] distance = new float[1];
        Location.distanceBetween(COLLEGE_LAT, COLLEGE_LON, selectedUserLat, selectedUserLon, distance);

        if (distance[0] > RADIUS_METERS) {
            tvStatus.setText("Status: OUT OF CAMPUS");
            tvStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            tvStatus.setText("Status: IN CAMPUS");
            tvStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLng collegeLocation = new LatLng(COLLEGE_LAT, COLLEGE_LON);
        mMap.addMarker(new MarkerOptions().position(collegeLocation).title("College Location"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(collegeLocation, 17));
    }
}
