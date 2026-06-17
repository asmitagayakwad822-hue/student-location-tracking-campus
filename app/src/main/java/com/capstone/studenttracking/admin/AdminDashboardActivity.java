package com.capstone.studenttracking.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.capstone.studenttracking.DBClass;
import com.capstone.studenttracking.LostFoundListActivity;
import com.capstone.studenttracking.R;
import com.capstone.studenttracking.SplashScreenActivity;
import com.capstone.studenttracking.adapter.DashboardAdapter;
import com.capstone.studenttracking.adapter.DashboardItem;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {
    TextView txtTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        txtTitle = findViewById(R.id.txtTitle);

        String query = "SELECT CValue FROM Configuration WHERE CName = 'name'";
        String name = DBClass.getSingleValue(query);
        txtTitle.setText("Welcome , "+name);

        RecyclerView recyclerView = findViewById(R.id.recycler_dashboard);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columns

        List<DashboardItem> items = new ArrayList<>();
        items.add(new DashboardItem("Manage Students", R.drawable.audience));
        items.add(new DashboardItem("Manage Staff", R.drawable.team));
        items.add(new DashboardItem("Track Locations", R.drawable.pointer));
        items.add(new DashboardItem("Annoucements", R.drawable.notification));

        DashboardAdapter adapter = new DashboardAdapter(this, items);
        recyclerView.setAdapter(adapter);
    }

    public void logoutClick(View view) {
        String query = "DELETE FROM Configuration";
        DBClass.execNonQuery(query);
        Intent intent = new Intent(this, SplashScreenActivity.class);
        startActivity(intent);
        finish();
    }

    public void lostFoundClick(View view) {
        Intent intent = new Intent(this, LostFoundListActivity.class);
        startActivity(intent);
    }
}