package com.example.evchargingapp.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.evchargingapp.R;
import com.example.evchargingapp.api.BookingApi;
import com.example.evchargingapp.utils.SharedPrefsHelper;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import org.json.JSONObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EVOperatorScanActivity extends AppCompatActivity {

    private TextView tvResult, tvStatus;
    private Button btnScan, btnConfirmActive, btnMarkComplete;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private String currentBookingId = null;
    private JSONObject currentBookingData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evoperator_scan);

        tvResult = findViewById(R.id.tvResult);
        tvStatus = findViewById(R.id.tvStatus);
        btnScan = findViewById(R.id.btnScan);
        btnConfirmActive = findViewById(R.id.btnConfirmActive);
        btnMarkComplete = findViewById(R.id.btnMarkComplete);

        btnScan.setOnClickListener(v -> startQRScanner());
        btnConfirmActive.setOnClickListener(v -> updateBookingStatus(4)); // Active
        btnMarkComplete.setOnClickListener(v -> updateBookingStatus(3)); // Completed

        btnConfirmActive.setEnabled(false);
        btnMarkComplete.setEnabled(false);
    }

    private void startQRScanner() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 101);
        } else {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setPrompt("Scan EV Owner's Booking QR");
            integrator.setBeepEnabled(true);
            integrator.initiateScan();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                currentBookingId = result.getContents();
                fetchBookingDetails(currentBookingId);
            } else {
                Toast.makeText(this, "Scan cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchBookingDetails(String bookingId) {
        executor.execute(() -> {
            try {
                String token = SharedPrefsHelper.getToken(this);
                JSONObject booking = BookingApi.getBookingById(bookingId, token);

                currentBookingData = booking;
                runOnUiThread(() -> {
                    try {
                        tvResult.setText("Booking ID: " + booking.optString("bookingId") +
                                "\nStation: " + booking.optString("stationId") +
                                "\nStart: " + booking.optString("startTimeUtc") +
                                "\nEnd: " + booking.optString("endTimeUtc"));
                        tvStatus.setText("Current Status: " + booking.optInt("status"));

                        int status = booking.optInt("status");
                        btnConfirmActive.setEnabled(status == 2); // Approved
                        btnMarkComplete.setEnabled(status == 4); // Active
                    } catch (Exception e) {
                        Toast.makeText(this, "Error parsing booking", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Booking not found", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private String getReadableStatus(int code) {
        switch (code) {
            case 0: return "Pending";
            case 1: return "Approved";
            case 2: return "Cancelled";
            case 3: return "Completed";
            case 4: return "Active";
            default: return "Unknown";
        }
    }


    private void updateBookingStatus(int newStatus) {
        if (currentBookingId == null) {
            Toast.makeText(this, "Scan a booking first", Toast.LENGTH_SHORT).show();
            return;
        }

        executor.execute(() -> {
            try {
                String token = SharedPrefsHelper.getToken(this);
                JSONObject body = new JSONObject();
                body.put("status", newStatus);

                JSONObject resp = BookingApi.updateBooking(currentBookingId, body, token);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Status updated!", Toast.LENGTH_SHORT).show();
                    fetchBookingDetails(currentBookingId);
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startQRScanner();
        } else {
            Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show();
        }
    }
}
