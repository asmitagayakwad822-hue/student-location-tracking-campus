package com.capstone.studenttracking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ViewImageActivity extends AppCompatActivity {

    private ImageView ivFullImage, ivClose;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        ivFullImage = findViewById(R.id.ivFullImage);
        ivClose = findViewById(R.id.ivClose);

        // Get the image URL from intent
        Intent intent = getIntent();
        String imageUrl = intent.getStringExtra("image_url");

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl) // Fetch from server
                    .placeholder(R.drawable.placeholder)
                    .into(ivFullImage);
        }

        ivClose.setOnClickListener(v -> finish()); // Close activity on click
    }
}