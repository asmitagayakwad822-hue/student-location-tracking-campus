package com.capstone.studenttracking.student;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.studenttracking.AnnouncementsListActivity;
import com.capstone.studenttracking.DBClass;
import com.capstone.studenttracking.LostFoundListActivity;
import com.capstone.studenttracking.MapActivity;
import com.capstone.studenttracking.R;
import com.capstone.studenttracking.ScheduleListActivity;
import com.capstone.studenttracking.SplashScreenActivity;
import com.capstone.studenttracking.TrackingActivity;
import com.capstone.studenttracking.ViewMapActivity;
import com.capstone.studenttracking.utils.LocationService;

public class StudentDashboardActivity extends AppCompatActivity {

    TextView txtTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);
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
        //Toast.makeText(this, "View Schdeule Clicked", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, ScheduleListActivity.class));
    }

    // Navigate to Track Staff or Peers Activity
    public void onTrackPeopleClicked(View view) {
       // Toast.makeText(this, "Track Staff Clicked", Toast.LENGTH_SHORT).show();
      startActivity(new Intent(StudentDashboardActivity.this, TrackingActivity.class));
    }

    // Navigate to Notifications Activity
    public void onViewNotificationsClicked(View view) {
       // Toast.makeText(this, "Notifications Clicked", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, AnnouncementsListActivity.class));
    }

    // Navigate to Profile Activity
    public void onProfileClicked(View view) {
        startActivity(new Intent(this, StudentProfileActivity.class));
        //Toast.makeText(this, "View Profile Clicked", Toast.LENGTH_SHORT).show();
    }
    public void onCampusClicked(View view) {
        startActivity(new Intent(this, ViewMapActivity.class));
//        startActivity(new Intent(this, ProfileActivity.class));
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