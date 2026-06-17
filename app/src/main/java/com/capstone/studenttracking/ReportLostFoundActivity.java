package com.capstone.studenttracking;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ReportLostFoundActivity extends AppCompatActivity {

    private EditText etItemName, etDescription;
    private ImageView ivItemImage;
    private Button btnUploadImage, btnSubmit;
    private Bitmap selectedImage;
    private ProgressDialog progressDialog;
    private String userName, userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_lost_found);

        etItemName = findViewById(R.id.etItemName);
        etDescription = findViewById(R.id.etDescription);
        ivItemImage = findViewById(R.id.ivItemImage);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnSubmit = findViewById(R.id.btnSubmit);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");

        userId = DBClass.getSingleValue("SELECT CValue FROM Configuration WHERE CName = 'id'");
        userName = DBClass.getSingleValue("SELECT CValue FROM Configuration WHERE CName = 'name'");

        btnUploadImage.setOnClickListener(v -> selectImage());

        btnSubmit.setOnClickListener(v -> uploadLostFoundItem());
    }

    private void selectImage() {
        Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImage, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                ivItemImage.setImageBitmap(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadLostFoundItem() {
        final String itemName = etItemName.getText().toString().trim();
        final String description = etDescription.getText().toString().trim();

        if (itemName.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, DBClass.urlAddLostFound,
                response -> {
                    progressDialog.dismiss();
                    Log.e("Response", response);
                    Toast.makeText(this, "Reported successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> {
                    progressDialog.dismiss();
                    Log.e("Error", error.toString());
                    Toast.makeText(this, "Failed to upload!", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                params.put("name", itemName);
                params.put("description", description);
                params.put("added_by", userName);

                if (selectedImage != null) {
                    params.put("image", encodeImage(selectedImage));
                }
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        byte[] byteArray = stream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}