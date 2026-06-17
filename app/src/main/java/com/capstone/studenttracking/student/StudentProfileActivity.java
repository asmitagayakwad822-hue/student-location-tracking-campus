package com.capstone.studenttracking.student;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.capstone.studenttracking.DBClass;
import com.capstone.studenttracking.R;
import com.capstone.studenttracking.SplashScreenActivity;

public class StudentProfileActivity extends AppCompatActivity {


    private ImageView ivProfileImage;
    private TextView tvStudentName, tvEnrollment, tvEmail, tvMobile, tvDepartment, tvClassYear, tvStatus;
    private Button btnLogout;
    private ProgressDialog progressDialog;
    private String studentId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvStudentName = findViewById(R.id.tvStudentName);
        tvEnrollment = findViewById(R.id.tvEnrollment);
        tvEmail = findViewById(R.id.tvEmail);
        tvMobile = findViewById(R.id.tvMobile);
        tvDepartment = findViewById(R.id.tvDepartment);
        tvClassYear = findViewById(R.id.tvClassYear);
        tvStatus = findViewById(R.id.tvStatus);
        btnLogout = findViewById(R.id.btnLogout);

        tvStudentName.setText("Name : "+DBClass.getSingleValue("SELECT CValue FROM Configuration WHERE CName = 'name'"));
        tvEnrollment.setText("Enroll. No. : "+DBClass.getSingleValue("SELECT CValue FROM Configuration WHERE CName = 'enrollment'"));
        tvEmail.setText("Email : "+DBClass.getSingleValue("SELECT CValue FROM Configuration WHERE CName = 'email'"));
        tvMobile.setText("Mobile No. : "+ DBClass.getSingleValue("SELECT CValue FROM Configuration WHERE CName = 'mobileno'"));
        tvDepartment.setText("Department : "+DBClass.getSingleValue("SELECT CValue FROM Configuration WHERE CName = 'department'"));
        tvClassYear.setText("Class : "+DBClass.getSingleValue("SELECT CValue FROM Configuration WHERE CName = 'class_year'"));

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = "DELETE FROM Configuration";
                DBClass.execNonQuery(query);
                Intent intent = new Intent(StudentProfileActivity.this, SplashScreenActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}