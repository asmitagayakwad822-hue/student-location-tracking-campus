package com.capstone.studenttracking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.capstone.studenttracking.adapter.Notification;
import com.capstone.studenttracking.adapter.NotificationAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationsActivity extends AppCompatActivity {

    private EditText etNotificationContent;
    private RadioGroup rgTarget;
    private Button btnSendNotification;
    private RecyclerView recyclerNotifications;
    private NotificationAdapter adapter;
    private List<Notification> notificationList;
    private ProgressDialog progressDialog;
    public String target="";

    private static final String TAG = "NotificationsActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        etNotificationContent = findViewById(R.id.et_notification_content);
        rgTarget = findViewById(R.id.rg_target);
        btnSendNotification = findViewById(R.id.btn_send_notification);
        recyclerNotifications = findViewById(R.id.recycler_notifications);

        recyclerNotifications.setLayoutManager(new LinearLayoutManager(this));
        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(notificationList);
        recyclerNotifications.setAdapter(adapter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        fetchNotifications();

        btnSendNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNotification();
            }
        });
    }

    private void sendNotification() {
        final String message = etNotificationContent.getText().toString().trim();
        target = "all";

        int selectedId = rgTarget.getCheckedRadioButtonId();
        if (selectedId == R.id.rb_students) {
            target = "students";
        } else if (selectedId == R.id.rb_staff) {
            target = "staff";
        }

        if (message.isEmpty()) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, DBClass.urlNotifications,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.d(TAG, "Response: " + response);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String status = jsonResponse.getString("status");
                            String message = jsonResponse.getString("message");
                            Toast.makeText(NotificationsActivity.this, message, Toast.LENGTH_SHORT).show();
                            if (status.equals("success")) {
                                fetchNotifications();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(NotificationsActivity.this, "Parsing error", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Log.e(TAG, "Volley Error: " + error.getMessage());
                        Toast.makeText(NotificationsActivity.this, "Failed to send notification: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("target", target);
                params.put("message", message);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void fetchNotifications() {
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.GET, DBClass.urlNotifications,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray data = jsonResponse.getJSONArray("data");
                            notificationList.clear();

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);
                                notificationList.add(new Notification(
                                        obj.getString("id"),
                                        obj.getString("target"),
                                        obj.getString("message"),
                                        obj.getString("created_at")
                                ));
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(NotificationsActivity.this, "Parsing error", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Log.e(TAG, "Volley Error: " + error.getMessage());
                        Toast.makeText(NotificationsActivity.this, "Failed to fetch notifications: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        Volley.newRequestQueue(this).add(request);
    }
}