package com.capstone.studenttracking.staff;

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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.capstone.studenttracking.DBClass;
import com.capstone.studenttracking.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ShareScheduleActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAPTURE_IMAGE_REQUEST = 2;

    private Spinner spinnerDepartment, spinnerClassYear;
    private EditText etTitle, etDescription;
    private Button btnUploadSchedule, btnCaptureImage, btnSelectImage;
    private ImageView ivScheduleImage;
    private ProgressDialog progressDialog;
    private String selectedDepartment, selectedClassYear;
    private Bitmap scheduleImageBitmap; // Store image as Bitmap

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_schedule);

        spinnerDepartment = findViewById(R.id.spinnerDepartment);
        spinnerClassYear = findViewById(R.id.spinnerClassYear);
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        btnUploadSchedule = findViewById(R.id.btnUploadSchedule);
        btnCaptureImage = findViewById(R.id.btnCaptureImage);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        ivScheduleImage = findViewById(R.id.ivScheduleImage);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading Schedule...");
        progressDialog.setCancelable(false);

        ArrayAdapter<CharSequence> deptAdapter = ArrayAdapter.createFromResource(this,
                R.array.department_array, R.layout.spinner_item);
        deptAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepartment.setAdapter(deptAdapter);

        ArrayAdapter<CharSequence> classAdapter = ArrayAdapter.createFromResource(this,
                R.array.class_array, R.layout.spinner_item);
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClassYear.setAdapter(classAdapter);

        spinnerDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDepartment = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedDepartment = ""; // Default empty to avoid null
            }
        });

// Ensure selected class_year is always valid
        spinnerClassYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedClassYear = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedClassYear = ""; // Default empty to avoid null
            }
        });

        btnCaptureImage.setOnClickListener(v -> captureImage());
        btnSelectImage.setOnClickListener(v -> selectImageFromGallery());
        btnUploadSchedule.setOnClickListener(v -> uploadSchedule());
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAPTURE_IMAGE_REQUEST);
    }

    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            try {
                if (requestCode == PICK_IMAGE_REQUEST) {
                    Uri imageUri = data.getData();
                    scheduleImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                } else if (requestCode == CAPTURE_IMAGE_REQUEST) {
                    scheduleImageBitmap = (Bitmap) data.getExtras().get("data");
                }
                ivScheduleImage.setImageBitmap(scheduleImageBitmap);
                ivScheduleImage.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadSchedule() {
        if (scheduleImageBitmap == null) {
            Toast.makeText(this, "Please select an image!", Toast.LENGTH_SHORT).show();
            return;
        }

        final String title = etTitle.getText().toString().trim();
        final String description = etDescription.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || selectedClassYear.isEmpty() || selectedDepartment.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();
        String encodedImage = encodeImage(scheduleImageBitmap);

        StringRequest request = new StringRequest(Request.Method.POST, DBClass.urlUploadSchedule,
                response -> {
                    progressDialog.dismiss();
                    Toast.makeText(ShareScheduleActivity.this, "Schedule Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> {
                    progressDialog.dismiss();
                    Log.e("Volley Error", error.toString());
                    Toast.makeText(ShareScheduleActivity.this, "Upload failed! " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("department", selectedDepartment);
                params.put("class_year", selectedClassYear); // This was null before, now it will be valid
                params.put("title", title);
                params.put("description", description);
                params.put("image", encodedImage);
                params.put("added_by", DBClass.getSingleValue("SELECT CValue FROM Configuration WHERE CName = 'id'"));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}