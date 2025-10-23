package com.example.evchargingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.evchargingapp.R;
import com.example.evchargingapp.activities.EVOperatorActiveBookingsActivity;
import com.example.evchargingapp.activities.EVOperatorCompletedBookingsActivity;
import com.example.evchargingapp.activities.QrScannerActivity;
import com.example.evchargingapp.utils.Constants;
import com.example.evchargingapp.utils.SharedPrefsHelper;

public class EVOperatorDashboardActivity extends AppCompatActivity {

    private TextView tvWelcome, tvStationName, tvPendingCount, tvApprovedCount, tvCompletedCount;
    private LinearLayout btnScanQr, btnActiveBookings, btnCompleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evoperator_dashboard);

        tvWelcome = findViewById(R.id.tvWelcome);
        tvStationName = findViewById(R.id.tvStationName);
        tvPendingCount = findViewById(R.id.tvPendingCount);
        tvApprovedCount = findViewById(R.id.tvApprovedCount);
        tvCompletedCount = findViewById(R.id.tvCompletedCount);

        btnScanQr = findViewById(R.id.btnScanQr);
        btnActiveBookings = findViewById(R.id.btnActiveBookings);
        btnCompleted = findViewById(R.id.btnCompleted);

        // Show Operator info
        String nic = SharedPrefsHelper.getNic(this);
        String role = SharedPrefsHelper.getRole(this);
        tvWelcome.setText("Welcome, " + nic);
        tvStationName.setText("Role: " + role);

        // TODO: Call API to get booking stats
        // e.g., fetchCountsFromApi();

        // Button listeners
        btnScanQr.setOnClickListener(v -> {
            Intent intent = new Intent(this, QrScannerActivity.class);
            startActivity(intent);
        });

        btnActiveBookings.setOnClickListener(v -> {
            Intent intent = new Intent(this, EVOperatorActiveBookingsActivity.class);
            startActivity(intent);
        });

        btnCompleted.setOnClickListener(v -> {
            Intent intent = new Intent(this, EVOperatorCompletedBookingsActivity.class);
            startActivity(intent);
        });
    }

    // TODO: create function fetchCountsFromApi() to load counts from backend
}
