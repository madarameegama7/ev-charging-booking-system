package com.example.evchargingapp.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.evchargingapp.R;
import com.example.evchargingapp.api.BookingApi;
import com.example.evchargingapp.models.Booking;
import com.example.evchargingapp.utils.SharedPrefsHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EVOperatorReservationActivity extends AppCompatActivity {

    private ListView lvBookings;
    private ProgressBar progressBar;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private List<Booking> bookingList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evoperator_reservation);

        lvBookings = findViewById(R.id.lvBookings);
        progressBar = findViewById(R.id.progressBar);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        lvBookings.setAdapter(adapter);

        lvBookings.setOnItemClickListener((parent, view, position, id) -> {
            Booking selected = bookingList.get(position);
            showActionDialog(selected);
        });

        fetchBookings();
    }

    private void fetchBookings() {
        String stationId = SharedPrefsHelper.getNic(this); // Assuming operator is tied to a station ID
        String token = SharedPrefsHelper.getToken(this);

        toggleLoading(true);
        executor.execute(() -> {
            try {
                JSONArray resp = BookingApi.getBookingsByStation(stationId, token);
                bookingList.clear();
                for (int i = 0; i < resp.length(); i++) {
                    JSONObject obj = resp.getJSONObject(i);
                    Booking b = new Booking();
                    b.setBookingId(obj.optString("id"));
                    b.setStationId(obj.optString("stationId"));
                    b.setOwnerNic(obj.optString("ownerNic"));
                    b.setStartTimeUtc(obj.optString("startTimeUtc"));
                    b.setEndTimeUtc(obj.optString("endTimeUtc"));
                    b.setStatus(obj.optInt("status"));
                    bookingList.add(b);
                }

                runOnUiThread(() -> {
                    toggleLoading(false);
                    updateListView();
                });

            } catch (final Exception ex) {
                ex.printStackTrace();
                runOnUiThread(() -> {
                    toggleLoading(false);
                    Toast.makeText(this, "Failed to fetch bookings: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void updateListView() {
        List<String> display = new ArrayList<>();
        for (Booking b : bookingList) {
            display.add("Owner: " + b.getOwnerNic() + "\nStart: " + b.getStartTimeUtc() +
                    "\nEnd: " + b.getEndTimeUtc() + "\nStatus: " + b.getStatus());
        }
        adapter.clear();
        adapter.addAll(display);
        adapter.notifyDataSetChanged();

        // Color-code by status
        for (int i = 0; i < lvBookings.getChildCount(); i++) {
            View item = lvBookings.getChildAt(i);
            Booking b = bookingList.get(i);
            int color;
            int status = b.getStatus();
            if (status == 1) { // Approved
                color = ContextCompat.getColor(this, android.R.color.holo_green_light);
            } else if (status == 2) { // Cancelled
                color = ContextCompat.getColor(this, android.R.color.holo_red_light);
            } else { // Pending / others
                color = ContextCompat.getColor(this, android.R.color.holo_orange_light);
            }
            item.setBackgroundColor(color);
        }
    }


    private void showActionDialog(Booking booking) {
        String[] options = {"Approve", "Cancel", "Close"};
        new AlertDialog.Builder(this)
                .setTitle("Select Action")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Approve
                            updateBookingStatus(booking, "Approved");
                            break;
                        case 1: // Cancel
                            updateBookingStatus(booking, "Cancelled");
                            break;
                        default:
                            dialog.dismiss();
                    }
                })
                .show();
    }

    private void updateBookingStatus(Booking booking, String newStatus) {
        String token = SharedPrefsHelper.getToken(this);
        JSONObject update = new JSONObject();
        try { update.put("status", newStatus); } catch (Exception ignored) {}

        toggleLoading(true);
        executor.execute(() -> {
            try {
                BookingApi.updateBooking(booking.getBookingId(), update, token);
                runOnUiThread(() -> {
                    toggleLoading(false);
                    Toast.makeText(this, "Booking " + newStatus, Toast.LENGTH_SHORT).show();
                    fetchBookings();
                });
            } catch (final Exception ex) {
                ex.printStackTrace();
                runOnUiThread(() -> {
                    toggleLoading(false);
                    Toast.makeText(this, "Failed to update: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void toggleLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        lvBookings.setEnabled(!isLoading);
    }
}
