/*
 * File: HomeActivity.java
 * Author: Janudi Adhikari
 * Date: 2025-09-26
 * Purpose: Home screen for EVChargingApp with navigation to Add/View Reservations and Logout.
 */

package com.example.evchargingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private Button btnAddReservation, btnViewReservations, btnLogout, btnProfile;
    private String userNic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Get NIC from login intent
        userNic = getIntent().getStringExtra("USER_NIC");

        // Bind UI elements
        tvWelcome = findViewById(R.id.tvWelcome);
        btnAddReservation = findViewById(R.id.btnAddReservation);
        btnViewReservations = findViewById(R.id.btnViewReservations);
        btnLogout = findViewById(R.id.btnLogout);
        btnProfile = findViewById(R.id.btnProfile);

        // Set welcome text
        tvWelcome.setText("Welcome, " + userNic + "!");

        // Button listeners
        btnAddReservation.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AddReservationActivity.class);
            intent.putExtra("USER_NIC", userNic);
            startActivity(intent);
        });

        btnViewReservations.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ViewReservationsActivity.class);
            intent.putExtra("USER_NIC", userNic);
            startActivity(intent);
        });

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, UpdateProfileActivity.class);
            intent.putExtra("USER_NIC", userNic); // pass logged-in user's NIC
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            // Finish activity to return to login
            finish();
        });
    }
}
