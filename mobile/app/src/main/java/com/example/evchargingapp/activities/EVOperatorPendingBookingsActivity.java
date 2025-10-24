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

import com.example.evchargingapp.adapters.BookingAdapter;
import com.example.evchargingapp.adapters.PendingBookingAdapter;
import com.example.evchargingapp.api.BookingApi;
import com.example.evchargingapp.models.Booking;
import com.example.evchargingapp.utils.SharedPrefsHelper;
import com.example.evchargingapp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EVOperatorPendingBookingsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private List<Booking> pendingList = new ArrayList<>();
    private PendingBookingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evoperator_pending_bookings);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PendingBookingAdapter(this, pendingList, new PendingBookingAdapter.PendingBookingListener() {
            @Override
            public void onApprove(Booking booking) {
                updateBookingStatus(booking, 1, "Approved successfully");
            }

            @Override
            public void onCancel(Booking booking) {
                updateBookingStatus(booking, 2, "Cancelled successfully");
            }
        });
        recyclerView.setAdapter(adapter);

        loadPendingBookings();
    }

    private void loadPendingBookings() {
        progressBar.setVisibility(View.VISIBLE);

        new AsyncTask<Void, Void, List<Booking>>() {
            @Override
            protected List<Booking> doInBackground(Void... voids) {
                List<Booking> list = new ArrayList<>();
                try {
                    String token = SharedPrefsHelper.getToken(EVOperatorPendingBookingsActivity.this);
                    String stationId = SharedPrefsHelper.getStationId(EVOperatorPendingBookingsActivity.this);

                    JSONArray arr = BookingApi.getBookingsByStation("ST-20251023-A0B76", token);

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject o = arr.getJSONObject(i);
                        int status = o.optInt("status");
                        if (status == 0) { // Pending
                            Booking b = new Booking(
                                    o.optString("bookingId"),
                                    o.optString("stationId"),
                                    o.optString("ownerNic"),
                                    o.optString("start"),
                                    o.optString("end"),
                                    status
                            );
                            list.add(b);
                            Log.d("BookingStatusCheck", "Booking " + b.getBookingId() + " status=" + b.getStatus());
                        }
                    }
                } catch (Exception e) {
                    Log.e("PendingBookings", "Error loading bookings", e);
                }
                return list;
            }

            @Override
            protected void onPostExecute(List<Booking> bookings) {
                progressBar.setVisibility(View.GONE);
                pendingList.clear();
                pendingList.addAll(bookings);
                adapter.notifyDataSetChanged();

                if (bookings.isEmpty()) {
                    Toast.makeText(EVOperatorPendingBookingsActivity.this,
                            "No pending bookings found", Toast.LENGTH_SHORT).show();
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
                    String token = SharedPrefsHelper.getToken(EVOperatorPendingBookingsActivity.this);
                    JSONObject update = new JSONObject();
                    update.put("status", newStatus);
                    BookingApi.updateBooking(booking.getBookingId(), update, token);
                    return true;
                } catch (Exception e) {
                    Log.e("PendingBookings", "Failed to update booking", e);
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                progressBar.setVisibility(View.GONE);
                if (success) {
                    Toast.makeText(EVOperatorPendingBookingsActivity.this, successMessage, Toast.LENGTH_SHORT).show();
                    loadPendingBookings(); // Refresh list
                } else {
                    Toast.makeText(EVOperatorPendingBookingsActivity.this, "Failed to update booking", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
}
