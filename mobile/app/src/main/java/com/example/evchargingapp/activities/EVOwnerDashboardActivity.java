/**
 * Activity: EVOwnerDashboardActivity
 * Description: Displays the EV Owner Dashboard with welcome message,
 * booking statistics, quick action buttons, and nearby stations map.
 */

package com.example.evchargingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.evchargingapp.R;
import com.example.evchargingapp.database.BookingDAO;
import com.example.evchargingapp.models.Booking;
import com.example.evchargingapp.api.BookingApi;
import com.example.evchargingapp.models.GeoLocation;
import com.example.evchargingapp.utils.SharedPrefsHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

public class EVOwnerDashboardActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView tvWelcome, tvPendingCount, tvApprovedCount;
    private Button btnLogout, btnViewStations, btnMyReservations, btnProfile;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evowner_dashboard);

        tvWelcome = findViewById(R.id.tvWelcome);
        tvPendingCount = findViewById(R.id.tvPendingCount);
        tvApprovedCount = findViewById(R.id.tvApprovedCount);
        btnLogout = findViewById(R.id.btnLogout);
        btnViewStations = findViewById(R.id.btnViewStations);
        btnMyReservations = findViewById(R.id.btnMyReservations);
        btnProfile = findViewById(R.id.btnProfile);

//        String nic = SharedPrefsHelper.getNic(this);
//        tvWelcome.setText("Welcome, " + nic);
        tvWelcome.setText("Welcome, Janudi");

        btnLogout.setOnClickListener(v -> {
            SharedPrefsHelper.clear(this);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        btnProfile.setOnClickListener(v -> startActivity(new Intent(this, EVOwnerProfileActivity.class)));
        btnMyReservations.setOnClickListener(v -> startActivity(new Intent(this, EVOwnerReservationActivity.class)));
        btnViewStations.setOnClickListener(v -> startActivity(new Intent(this, ViewStationsActivity.class)));

        loadBookingStats();
        setupMap();
    }

    private void loadBookingStats() {
        new Thread(() -> {
            try {
                String nic = SharedPrefsHelper.getNic(this);
                String token = SharedPrefsHelper.getToken(this);
                JSONArray bookingsJson = BookingApi.getBookingsByOwner(nic, token);

                BookingDAO bookingDAO = new BookingDAO(this);
                bookingDAO.open();
                bookingDAO.deleteAllBookings(); // optional: refresh local DB

                int pending = 0, approved = 0;

                for (int i = 0; i < bookingsJson.length(); i++) {
                    JSONObject obj = bookingsJson.getJSONObject(i);
                    Booking b = new Booking();
                    b.setBookingId(obj.getString("bookingId"));
                    b.setOwnerNic(obj.getString("ownerNic"));
                    b.setStationId(obj.getString("stationId"));
                    b.setStartTimeUtc(obj.getString("start"));
                    b.setEndTimeUtc(obj.getString("end"));
                    Object statusObj = obj.get("status");
                    int status = 0;

                    if (statusObj instanceof Integer) {
                        status = (Integer) statusObj;
                    } else if (statusObj instanceof String) {
                        String s = ((String) statusObj).toLowerCase(Locale.ROOT);
                        if (s.equals("pending")) status = 0;
                        else if (s.equals("approved")) status = 1;
                        else if (s.equals("cancelled")) status = 2;
                        else if (s.equals("completed")) status = 3;
                    }
                    b.setStatus(status);


                    bookingDAO.insertOrUpdateBooking(b);

                    if (b.getStatus() == 0) pending++;
                    else if (b.getStatus() == 1) approved++;
                }

                int finalPending = pending;
                int finalApproved = approved;
                runOnUiThread(() -> {
                    tvPendingCount.setText(String.valueOf(finalPending));
                    tvApprovedCount.setText(String.valueOf(finalApproved));
                });

                bookingDAO.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.googleMap = map;

        // Example: add station markers
        addStationMarker("Colombo Central Station", new GeoLocation(6.9271, 79.8612));
        // TODO: Add more stations from your database dynamically

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(6.9271, 79.8612), 12f));
    }

    private void addStationMarker(String name, GeoLocation location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.addMarker(new MarkerOptions().position(latLng).title(name));
    }
}
