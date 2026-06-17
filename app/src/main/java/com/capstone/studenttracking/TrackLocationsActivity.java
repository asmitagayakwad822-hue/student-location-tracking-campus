package com.capstone.studenttracking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class TrackLocationsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private RadioGroup rgUserType;
    private EditText etSearchInput;
    private MapView mapView;
    private GoogleMap googleMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_locations);

        rgUserType = findViewById(R.id.rg_user_type);
        etSearchInput = findViewById(R.id.et_search_input);
        mapView = findViewById(R.id.map_view);

        // Initialize Map
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(TrackLocationsActivity.this);

        // Track Button Click
        findViewById(R.id.btn_track).setOnClickListener(v -> trackLocation());
    }
    private void trackLocation() {
        String searchQuery = etSearchInput.getText().toString().trim();

        // Check if input is empty
        if (TextUtils.isEmpty(searchQuery)) {
            Toast.makeText(this, "Enter a valid input", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedType = rgUserType.getCheckedRadioButtonId();
        if (selectedType == -1) {
            Toast.makeText(this, "Select Student or Staff", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isStudent = selectedType == R.id.rb_student;

        // Fetch location data (Dummy data for now)
        LatLng location = fetchLocation(isStudent, searchQuery);

        if (location != null) {
            // Display location on map
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions().position(location).title(isStudent ? "Student" : "Staff"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
        } else {
            Toast.makeText(this, "No location found for the entered input", Toast.LENGTH_SHORT).show();
        }
    }

    private LatLng fetchLocation(boolean isStudent, String query) {
        // Dummy location data based on query
        if (isStudent && query.equalsIgnoreCase("12345")) {
            return new LatLng(17.028399, 74.620898); // Example coordinates for a student
        } else if (!isStudent && query.equalsIgnoreCase("Mam")) {
            return new LatLng(17.029399, 74.621898); // Example coordinates for a staff member
        }
        return null;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}