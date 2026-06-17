package com.capstone.studenttracking.staff;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.capstone.studenttracking.AnnouncementsListActivity;
import com.capstone.studenttracking.DBClass;
import com.capstone.studenttracking.LostFoundListActivity;
import com.capstone.studenttracking.ManageAnnouncementsActivity;
import com.capstone.studenttracking.R;
import com.capstone.studenttracking.ScheduleListActivity;
import com.capstone.studenttracking.SplashScreenActivity;
import com.capstone.studenttracking.TrackLocationsActivity;
import com.capstone.studenttracking.TrackingActivity;
import com.capstone.studenttracking.admin.ManageStudentsActivity;
import com.capstone.studenttracking.NotificationsActivity;
import com.capstone.studenttracking.utils.LocationService;

public class StaffDashboardActivity extends AppCompatActivity {

    TextView txtTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_dashboard);

        txtTitle = findViewById(R.id.txtTitle);

        String query = "SELECT CValue FROM Configuration WHERE CName = 'name'";
        String name = DBClass.getSingleValue(query);
        txtTitle.setText("Welcome , "+name);

        query = "SELECT CValue FROM Configuration WHERE CName = 'id'";
        String user_id = DBClass.getSingleValue(query);
        query = "SELECT CValue FROM Configuration WHERE CName = 'usertype'";
        String usertype = DBClass.getSingleValue(query);

        Intent serviceIntent = new Intent(this, LocationService.class);
        serviceIntent.putExtra("user_id", user_id);  // Replace with logged-in user ID
        serviceIntent.putExtra("role", usertype); // Or \"staff\"
        startService(serviceIntent);
    }

    // Navigate to View Schedules Activity
    public void onViewSchedulesClicked(View view) {
        startActivity(new Intent(this, ScheduleListActivity.class));
    }

    // Navigate to Track Students Activity
    public void onTrackStudentsClicked(View view) {
        startActivity(new Intent(this, TrackingActivity.class));
    }

    // Navigate to Manage Students Activity
    public void onManageStudentsClicked(View view) {
        startActivity(new Intent(this, ManageStudentsActivity.class));
    }

    // Navigate to Send Notifications Activity
    public void onSendNotificationsClicked(View view) {
        startActivity(new Intent(this, AnnouncementsListActivity.class));
    }

    // Navigate to Profile Activity
    public void onProfileClicked(View view) {
        startActivity(new Intent(this, StaffProfileActivity.class));
    }
    public void onLogoutClicked(View view){
        String query = "DELETE FROM Configuration";
        DBClass.execNonQuery(query);
        Intent intent = new Intent(this, SplashScreenActivity.class);
        startActivity(intent);
        finish();
    }
    public  void lostFoundClick(View view)
    {
        startActivity(new Intent(this, LostFoundListActivity.class));
    }
}