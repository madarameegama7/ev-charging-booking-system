package com.example.evchargingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.evchargingapp.R;
import com.example.evchargingapp.utils.SharedPrefsHelper;

public class EVOperatorDashboardActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private Button btnManageStations, btnViewReservations, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evoperator_dashboard);

        tvWelcome = findViewById(R.id.tvWelcome);
        btnViewReservations = findViewById(R.id.btnViewReservations);
        btnLogout = findViewById(R.id.btnLogout);

        // Show welcome message with NIC
        String nic = SharedPrefsHelper.getNic(this);
        tvWelcome.setText("Welcome, " + nic);

        // Logout action
        btnLogout.setOnClickListener(v -> {
            SharedPrefsHelper.clear(this);
            Intent intent = new Intent(EVOperatorDashboardActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // TODO: Implement navigation to station management
        btnManageStations.setOnClickListener(v -> {
            // Example: navigate to StationManagementActivity
            // Intent intent = new Intent(EVOperatorDashboardActivity.this, StationManagementActivity.class);
            // startActivity(intent);
        });

        // TODO: Implement navigation to view reservations
        btnViewReservations.setOnClickListener(v -> {
            // Example: navigate to ReservationListActivity
            // Intent intent = new Intent(EVOperatorDashboardActivity.this, ReservationListActivity.class);
            // startActivity(intent);
        });
    }
}
