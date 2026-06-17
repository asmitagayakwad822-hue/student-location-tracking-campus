package com.capstone.studenttracking.utils;
import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.capstone.studenttracking.DBClass;

import java.util.HashMap;
import java.util.Map;


public class LocationService extends Service {

    private static final long INTERVAL = 2 * 60 * 1000; // 2 minutes
    private static final double COLLEGE_LAT = 16.6959385;
    private static final double COLLEGE_LNG = 74.2424201;
    private static final float CAMPUS_RADIUS = 100; // 100 meters

    private LocationManager locationManager;
    private Location currentLocation;
    private Handler handler = new Handler();
    private String userId, role;

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Location permission not granted!", Toast.LENGTH_SHORT).show();
            stopSelf();
            return;
        }

        currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (currentLocation != null) {
            Log.d("LocationService", "Got last known location: " + currentLocation.getLatitude() + ", " + currentLocation.getLongitude());
        } else {
            Log.d("LocationService", "No last known location available.");
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 1, locationListener);
        handler.post(locationChecker);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            userId = intent.getStringExtra("user_id");
            role = intent.getStringExtra("role");
            Log.d("LocationService", "Received User ID: " + userId + ", Role: " + role);
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            currentLocation = location;
            Log.d("LocationService", "Location updated: " + location.getLatitude() + ", " + location.getLongitude());
        }
    };

    private Runnable locationChecker = new Runnable() {
        @Override
        public void run() {
            if (currentLocation != null) {
                double latitude = currentLocation.getLatitude();
                double longitude = currentLocation.getLongitude();

                if (isInCampus(latitude, longitude)) {
                    Log.d("LocationService", "In campus. Sending update...");
                    updateLocation(userId, role, latitude, longitude);
                } else {
                    Toast.makeText(LocationService.this, "Out of Campus!", Toast.LENGTH_SHORT).show();
                    Log.d("LocationService", "User is out of campus.");
                }
            } else {
                Log.d("LocationService", "Current location is null.");
            }
            handler.postDelayed(this, INTERVAL);
        }
    };

    private boolean isInCampus(double lat, double lng) {
        float[] results = new float[1];
        Location.distanceBetween(COLLEGE_LAT, COLLEGE_LNG, lat, lng, results);
        return results[0] <= CAMPUS_RADIUS;
    }

    private void updateLocation(String userId, String role, double lat, double lng) {
        Log.d("LocationService", "Updating location: " + userId + ", " + role + ", " + lat + ", " + lng);

        StringRequest request = new StringRequest(Request.Method.POST, DBClass.urlUpdateLocation,
                response -> Log.d("LocationService", "Location updated on server: " + response),
                error -> Log.e("LocationService", "Location update failed: " + error.getMessage())
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                params.put("role", role);
                params.put("latitude", String.valueOf(lat));
                params.put("longitude", String.valueOf(lng));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(locationChecker);
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }
}