/*
 * File: ViewReservationsActivity.java
 * Author: Janudi Adhikari
 * Date: 2025-09-26
 * Purpose: Displays the list of upcoming and past reservations for the logged-in user.
 */

package com.example.evchargingapp;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.evchargingapp.database.DBHelper;
import com.example.evchargingapp.models.Reservation;

import java.util.List;

public class ViewReservationsActivity extends AppCompatActivity {

    private ListView lvReservations;
    private DBHelper dbHelper;
    private String userNic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reservations);

        lvReservations = findViewById(R.id.lvReservations);
        dbHelper = new DBHelper(this);

        userNic = getIntent().getStringExtra("USER_NIC");
        if (userNic == null) {
            Toast.makeText(this, "No user found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load reservations
        List<Reservation> reservations = dbHelper.getReservationsListByNIC(userNic);
        if (reservations.isEmpty()) {
            Toast.makeText(this, "No reservations found", Toast.LENGTH_SHORT).show();
        } else {
            // Use a simple ArrayAdapter for now (or create a custom adapter later)
            ReservationAdapter adapter = new ReservationAdapter(this, reservations);
            lvReservations.setAdapter(adapter);
        }
    }
}
