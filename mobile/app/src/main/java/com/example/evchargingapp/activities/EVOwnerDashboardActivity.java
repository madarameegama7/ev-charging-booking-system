package com.example.evchargingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.evchargingapp.R;
import com.example.evchargingapp.database.BookingDAO;
import com.example.evchargingapp.models.Booking;
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
        btnViewStations.setOnClickListener(v -> startActivity(new Intent(this, EVOwnerReservationActivity.class)));

        loadBookingStats();
        setupMap();
    }

    private void loadBookingStats() {
        BookingDAO bookingDAO = new BookingDAO(this);
        bookingDAO.open();
        List<Booking> bookings = bookingDAO.getBookingsByOwner(SharedPrefsHelper.getNic(this));
        int pending = 0, approved = 0;
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);

        for (Booking b : bookings) {
            try {
                Date start = sdf.parse(b.getStartTimeUtc());
                if (b.getStatus().equalsIgnoreCase("Pending")) pending++;
                else if (b.getStatus().equalsIgnoreCase("Approved") && start.after(now)) approved++;
            } catch (Exception ignored) {}
        }

        tvPendingCount.setText("Pending Reservations: " + pending);
        tvApprovedCount.setText("Approved Future Reservations: " + approved);
        bookingDAO.close();
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
