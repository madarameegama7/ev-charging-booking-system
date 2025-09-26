package com.example.evchargingapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.evchargingapp.database.DBHelper;

import java.util.UUID;

public class AddReservationActivity extends AppCompatActivity {

    private EditText etStationID, etDateTime;
    private Button btnConfirmReservation;
    private DBHelper dbHelper;
    private String userNic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reservation);

        // Get logged-in user's NIC
        userNic = getIntent().getStringExtra("USER_NIC");

        dbHelper = new DBHelper(this);

        etStationID = findViewById(R.id.etStationID);
        etDateTime = findViewById(R.id.etDateTime);
        btnConfirmReservation = findViewById(R.id.btnConfirmReservation);

        btnConfirmReservation.setOnClickListener(v -> {
            String stationId = etStationID.getText().toString().trim();
            String dateTime = etDateTime.getText().toString().trim();

            if (stationId.isEmpty() || dateTime.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                String reservationId = UUID.randomUUID().toString(); // Unique ID
                boolean success = dbHelper.addOrUpdateReservation(reservationId, userNic, stationId, dateTime, "Pending");

                if (success) {
                    Toast.makeText(this, "Reservation added successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Return to HomeActivity
                } else {
                    Toast.makeText(this, "Failed to add reservation", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
