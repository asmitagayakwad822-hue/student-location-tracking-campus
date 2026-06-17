package com.capstone.studenttracking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ViewScheduleImageActivity extends AppCompatActivity {
    private ImageView ivFullImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_schedule_image);

        ivFullImage = findViewById(R.id.ivFullImage);

        // Get the Image URL from Intent
        Intent intent = getIntent();
        if (intent.hasExtra("image_url")) {
            String imageUrl = intent.getStringExtra("image_url");

            // Load Image with Glide
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error_img)
                    .into(ivFullImage);
        }
    }
}