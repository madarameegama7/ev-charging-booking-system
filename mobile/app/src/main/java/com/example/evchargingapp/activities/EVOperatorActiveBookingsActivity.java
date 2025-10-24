package com.example.evchargingapp.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.evchargingapp.R;
import com.example.evchargingapp.adapters.ActiveBookingAdapter;
import com.example.evchargingapp.api.BookingApi;
import com.example.evchargingapp.models.Booking;
import com.example.evchargingapp.utils.SharedPrefsHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EVOperatorActiveBookingsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private List<Booking> activeList = new ArrayList<>();
    private ActiveBookingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evoperator_active_bookings);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ActiveBookingAdapter(this, activeList, booking -> updateBookingStatus(booking, 3, "Marked as Completed"));
        recyclerView.setAdapter(adapter);

        loadActiveBookings();
    }

    private void loadActiveBookings() {
        progressBar.setVisibility(View.VISIBLE);

        new AsyncTask<Void, Void, List<Booking>>() {
            @Override
            protected List<Booking> doInBackground(Void... voids) {
                List<Booking> list = new ArrayList<>();
                try {
                    String token = SharedPrefsHelper.getToken(EVOperatorActiveBookingsActivity.this);
                    String stationId = SharedPrefsHelper.getStationId(EVOperatorActiveBookingsActivity.this);

                    JSONArray arr = BookingApi.getBookingsByStation("ST-20251023-A0B76", token);

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject o = arr.getJSONObject(i);
                        int status = o.optInt("status");
                        if (status == 4) { // Active bookings only
                            Booking b = new Booking(
                                    o.optString("bookingId"),
                                    o.optString("stationId"),
                                    o.optString("ownerNic"),
                                    o.optString("start"),
                                    o.optString("end"),
                                    status
                            );
                            list.add(b);
                        }
                    }
                } catch (Exception e) {
                    Log.e("ActiveBookings", "Error loading bookings", e);
                }
                return list;
            }

            @Override
            protected void onPostExecute(List<Booking> bookings) {
                progressBar.setVisibility(View.GONE);
                activeList.clear();
                activeList.addAll(bookings);
                adapter.notifyDataSetChanged();

                if (bookings.isEmpty()) {
                    Toast.makeText(EVOperatorActiveBookingsActivity.this,
                            "No active bookings found", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void updateBookingStatus(Booking booking, int newStatus, String successMessage) {
        progressBar.setVisibility(View.VISIBLE);
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    String token = SharedPrefsHelper.getToken(EVOperatorActiveBookingsActivity.this);
                    JSONObject update = new JSONObject();
                    update.put("status", newStatus);
                    BookingApi.updateBooking(booking.getBookingId(), update, token);
                    return true;
                } catch (Exception e) {
                    Log.e("ActiveBookings", "Failed to update booking", e);
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                progressBar.setVisibility(View.GONE);
                if (success) {
                    Toast.makeText(EVOperatorActiveBookingsActivity.this, successMessage, Toast.LENGTH_SHORT).show();
                    loadActiveBookings(); // Refresh list
                } else {
                    Toast.makeText(EVOperatorActiveBookingsActivity.this, "Failed to update booking", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
}
