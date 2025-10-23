package com.example.evchargingapp.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.evchargingapp.R;
import com.example.evchargingapp.adapters.BookingAdapter;
import com.example.evchargingapp.api.BookingApi;
import com.example.evchargingapp.api.StationApi;
import com.example.evchargingapp.models.Booking;
import com.example.evchargingapp.utils.SharedPrefsHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EVOperatorActiveBookingsActivity extends AppCompatActivity {

    private RecyclerView recyclerBookings;
    private BookingAdapter adapter;
    private final List<Booking> bookingList = new ArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evoperator_active_bookings);

        recyclerBookings = findViewById(R.id.recyclerBookings);
        recyclerBookings.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BookingAdapter(this, bookingList, new BookingAdapter.BookingListener() {
            @Override
            public void onModify(Booking booking) {
                Toast.makeText(EVOperatorActiveBookingsActivity.this, "Modify not implemented yet", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(Booking booking) {
                Toast.makeText(EVOperatorActiveBookingsActivity.this, "Cancel booking feature coming soon", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onShowQR(Booking booking) {
                Toast.makeText(EVOperatorActiveBookingsActivity.this, "Show QR for booking: " + booking.getBookingId(), Toast.LENGTH_SHORT).show();
            }
        });
        recyclerBookings.setAdapter(adapter);

        fetchActiveBookings();
    }

    private void fetchActiveBookings() {
        executor.execute(() -> {
            try {
                String token = SharedPrefsHelper.getToken(this);
                String nic = SharedPrefsHelper.getNic(this);

                // Step 1: find the operator’s station by their NIC
                JSONObject userObj = StationApi.getStationById(nic); // if operator NIC = stationId mapping
                String stationId = userObj.optString("_id", "");

                if (stationId.isEmpty()) {
                    runOnUiThread(() -> Toast.makeText(this, "Station not found for operator", Toast.LENGTH_SHORT).show());
                    return;
                }

                // Step 2: fetch all bookings for that station
                JSONArray arr = BookingApi.getBookingsByStation(stationId, token);

                // Step 3: filter only active ones (Pending, Approved)
                bookingList.clear();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    String status = obj.optString("status", "");
                    if (status.equalsIgnoreCase("Pending") || status.equalsIgnoreCase("Approved")) {
                        bookingList.add(new Booking(
                                obj.optString("id", ""),
                                obj.optString("stationId", ""),
                                obj.optString("ownerNic", ""),
                                obj.optString("start", ""),
                                obj.optString("end", ""),
                                status
                        ));
                    }
                }

                runOnUiThread(() -> {
                    if (bookingList.isEmpty()) {
                        Toast.makeText(this, "No active bookings found", Toast.LENGTH_SHORT).show();
                    }
                    adapter.notifyDataSetChanged();
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error fetching bookings: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }
}
