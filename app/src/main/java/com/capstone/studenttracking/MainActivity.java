package com.capstone.studenttracking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    CardView card_staff_login, card_student_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        card_staff_login = findViewById(R.id.card_staff_login);
        card_student_login = findViewById(R.id.card_student_login);

        card_student_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StudentLoginActivity.class);
                startActivity(intent);
            }
        });
        card_staff_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StaffLoginActivity.class);
                startActivity(intent);
            }
        });
    }
}