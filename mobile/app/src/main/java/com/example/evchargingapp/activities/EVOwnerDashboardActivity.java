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
import com.example.evchargingapp.api.StationApi;
import com.example.evchargingapp.database.BookingDAO;
import com.example.evchargingapp.models.Booking;
import com.example.evchargingapp.api.BookingApi;
import com.example.evchargingapp.models.GeoLocation;
import com.example.evchargingapp.utils.SharedPrefsHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
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

        String name = SharedPrefsHelper.getName(this);
        tvWelcome.setText("Welcome, " + name);

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
                        else if (s.equals("active")) status = 4;
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

    private void loadStationsFromApi() {
        new Thread(() -> {
            try {
                String token = SharedPrefsHelper.getToken(this);
                JSONArray stationsArray = StationApi.getAllStations(token);

                runOnUiThread(() -> {
                    for (int i = 0; i < stationsArray.length(); i++) {
                        try {
                            JSONObject obj = stationsArray.getJSONObject(i);
                            JSONObject loc = obj.getJSONObject("location");

                            double lat = loc.optDouble("lat", 0.0);   // from backend
                            double lng = loc.optDouble("lng", 0.0);

                            String name = obj.optString("name", "Unknown Station");
                            String type = obj.optString("type", "Unknown Type");
                            int availableSlots = obj.optInt("availableSlots", 0);

                            addStationMarker(name, type, availableSlots, new GeoLocation(lat, lng));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    // focus camera on first station
                    if (stationsArray.length() > 0) {
                        try {
                            JSONObject first = stationsArray.getJSONObject(0);
                            JSONObject loc = first.getJSONObject("location");
                            double lat = loc.optDouble("lat", 0.0);
                            double lng = loc.optDouble("lng", 0.0);
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 12f));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

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

        // Load stations dynamically
        loadStationsFromApi();
    }


    private void addStationMarker(String name, String type, int availableSlots, GeoLocation location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        float color;
        if (type.equalsIgnoreCase("AC")) {
            color = BitmapDescriptorFactory.HUE_GREEN;   // AC = Green
        } else if (type.equalsIgnoreCase("DC")) {
            color = BitmapDescriptorFactory.HUE_RED;     // DC = Red
        } else {
            color = BitmapDescriptorFactory.HUE_AZURE;   // Unknown = Blue
        }

        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(name)
                .snippet("Type: " + type + "\nAvailable Slots: " + availableSlots)
                .icon(BitmapDescriptorFactory.defaultMarker(color)));
    }
}
