package com.capstone.studenttracking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import java.util.*;

public class ViewMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private TextView tvLocationName, tvLocationDetails;
    private Button btnDirections;
    private Spinner spinnerLocations;
    private LatLng selectedLocation;

    private LatLng collegeLatLng = new LatLng(17.028399, 74.620898);
    private HashMap<String, Marker> locationMarkers = new HashMap<>();
    private HashMap<String, String> locationDetailsMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        View bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        tvLocationName = findViewById(R.id.tv_location_name);
        tvLocationDetails = findViewById(R.id.tv_location_details);
        btnDirections = findViewById(R.id.btn_directions);
        spinnerLocations = findViewById(R.id.spinner_locations);

        btnDirections.setOnClickListener(v -> showWalkingDirections(selectedLocation));
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(collegeLatLng, 18));

        addMarker(new LatLng(17.028754, 74.621026), "IT Dept. Lab 3", "Advanced computing lab", R.drawable.ic_lab);
        addMarker(new LatLng(17.0283451, 74.6209934), "Main Gate", "Main entrance to campus", R.drawable.ic_gate);
        addMarker(new LatLng(17.028490, 74.620483), "Office", "Administration Office", R.drawable.ic_office);
        addMarker(new LatLng(17.028616, 74.620491), "Principal's Cabin", "Meeting area for principal", R.drawable.ic_principal);
        addMarker(new LatLng(17.028531, 74.620306), "Library", "Books and study area", R.drawable.ic_library);
        addMarker(new LatLng(17.028761, 74.620366), "Cafeteria", "Snacks and refreshments", R.drawable.ic_cafe);
        addMarker(new LatLng(17.028709, 74.620539), "Parking", "Parking Area", R.drawable.ic_parking);

        for (Marker marker : locationMarkers.values()) {
            marker.setVisible(false);
        }

        List<String> locationNames = new ArrayList<>(locationMarkers.keySet());
        locationNames.add(0, "Select Location");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, locationNames);
        spinnerLocations.setAdapter(adapter);

        spinnerLocations.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedPlace = parent.getItemAtPosition(position).toString();
                updateMapForSelection(selectedPlace);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void addMarker(LatLng position, String title, String details, int iconRes) {
        Bitmap smallMarker = resizeMapIcon(iconRes, 100, 100);
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(position)
                .title(title)
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));

        // Ensure marker has a valid tag (details)
        if (details != null && !details.isEmpty()) {
            marker.setTag(details);
        } else {
            marker.setTag("No details available");
        }

        locationMarkers.put(title, marker);
        locationDetailsMap.put(title, details);
    }
    private void updateMapForSelection(String selectedPlace) {
        // Hide all markers initially
        for (Marker marker : locationMarkers.values()) {
            marker.setVisible(false);
        }

        if (!selectedPlace.equals("Select Location")) {
            Marker selectedMarker = locationMarkers.get(selectedPlace);
            if (selectedMarker != null) {
                selectedMarker.setVisible(true);
                selectedMarker.showInfoWindow(); // Show marker info
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedMarker.getPosition(), 18));

                // Show details and directions button
                showLocationDetails(selectedMarker);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                btnDirections.setVisibility(View.VISIBLE); // Ensure button is visible
            }
        } else {
            // Hide bottom sheet if no location is selected
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            btnDirections.setVisibility(View.GONE); // Hide directions button
        }
    }

    private void showLocationDetails(Marker marker) {
        tvLocationName.setText(marker.getTitle());

        // Ensure the marker has a tag (details)
        if (marker.getTag() != null) {
            tvLocationDetails.setText(marker.getTag().toString());
        } else {
            tvLocationDetails.setText("No details available");
        }

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED); // Expand bottom sheet
        btnDirections.setVisibility(View.VISIBLE); // Show button when marker is selected
    }
    private void showWalkingDirections(LatLng destination) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + destination.latitude + "," + destination.longitude + "&mode=w");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    private Bitmap resizeMapIcon(int iconRes, int width, int height) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), iconRes);
        return Bitmap.createScaledBitmap(imageBitmap, width, height, false);
    }
}
