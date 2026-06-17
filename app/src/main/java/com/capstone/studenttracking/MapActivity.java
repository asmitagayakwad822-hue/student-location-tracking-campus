package com.capstone.studenttracking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;
        // Set default location (e.g., your college location)
        LatLng collegeLocation = new LatLng(17.028399, 74.620898);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(collegeLocation, 16));
        addCampusPoints();
        mMap.setOnMarkerClickListener(marker -> {
            LatLng destination = marker.getPosition();
            showDirections(destination);
            return true;
        });

    }

    private void showDirections(LatLng destination) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + destination.latitude + "," + destination.longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

//    private void addCampusPoints() {
//        LatLng library = new LatLng(17.029000, 74.620500);
//        LatLng cafeteria = new LatLng(17.028700, 74.620700);
//
//        LatLng maingate = new LatLng(17.0283451, 74.6209934);
//        LatLng lab2 = new LatLng(17.0286009, 74.6210849);
//
//        LatLng bhosalesir = new LatLng(17.0287888, 74.6211633);
//        LatLng hodcabin = new LatLng(17.0285599, 74.6210098);
//
//
//        mMap.addMarker(new MarkerOptions().position(library).title("Library"));
//        mMap.addMarker(new MarkerOptions().position(cafeteria).title("Cafeteria"));
//
//        mMap.addMarker(new MarkerOptions().position(maingate).title("Main Gate"));
//        mMap.addMarker(new MarkerOptions().position(lab2).title("Lab 2"));
//        mMap.addMarker(new MarkerOptions().position(bhosalesir).title("Bhosale Sir Cabin"));
//        mMap.addMarker(new MarkerOptions().position(hodcabin).title("HOD Cabin"));
//
//    }

    private void addCampusPoints() {
        LatLng library = new LatLng(17.029000, 74.620500);
        LatLng cafeteria = new LatLng(17.028700, 74.620700);
        LatLng maingate = new LatLng(17.0283451, 74.6209934);
        LatLng lab2 = new LatLng(17.0286009, 74.6210849);
        LatLng bhosalesir = new LatLng(17.0287888, 74.6211633);
        LatLng hodcabin = new LatLng(17.0285599, 74.6210098);

        // Add markers with info windows
        mMap.addMarker(new MarkerOptions().position(library).title("Library")).showInfoWindow();
        mMap.addMarker(new MarkerOptions().position(cafeteria).title("Cafeteria")).showInfoWindow();
        mMap.addMarker(new MarkerOptions().position(maingate).title("Main Gate")).showInfoWindow();
        mMap.addMarker(new MarkerOptions().position(lab2).title("Lab 2")).showInfoWindow();
        mMap.addMarker(new MarkerOptions().position(bhosalesir).title("Bhosale Sir Cabin")).showInfoWindow();
        mMap.addMarker(new MarkerOptions().position(hodcabin).title("HOD Cabin")).showInfoWindow();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}