package com.example.evchargingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.evchargingapp.R;
import com.example.evchargingapp.utils.SharedPrefsHelper;

public class EVOwnerDashboardActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private Button btnLogout, btnViewStations, btnMyReservations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evowner_dashboard);

        tvWelcome = findViewById(R.id.tvWelcome);
        btnLogout = findViewById(R.id.btnLogout);
        btnViewStations = findViewById(R.id.btnViewStations);
        btnMyReservations = findViewById(R.id.btnMyReservations);

        // Show welcome message with NIC
        String nic = SharedPrefsHelper.getNic(this);
        tvWelcome.setText("Welcome, " + nic);

        // Logout action
        btnLogout.setOnClickListener(v -> {
            SharedPrefsHelper.clear(this);
            Intent intent = new Intent(EVOwnerDashboardActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // TODO: Implement navigation to station list
        btnViewStations.setOnClickListener(v -> {
            // Example: navigate to StationListActivity
            // Intent intent = new Intent(EVOwnerDashboardActivity.this, StationListActivity.class);
            // startActivity(intent);
        });

        // TODO: Implement navigation to reservation history
        btnMyReservations.setOnClickListener(v -> {
            // Example: navigate to ReservationHistoryActivity
            // Intent intent = new Intent(EVOwnerDashboardActivity.this, ReservationHistoryActivity.class);
            // startActivity(intent);
        });
    }
}
