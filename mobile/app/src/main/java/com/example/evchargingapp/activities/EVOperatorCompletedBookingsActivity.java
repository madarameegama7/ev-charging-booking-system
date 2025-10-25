/**
 * Activity: EVOperatorCompletedBookingsActivity
 * Description: Displays all completed bookings (status = 3) for the operator's assigned station.
 */

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
import com.example.evchargingapp.adapters.BookingAdapter;
import com.example.evchargingapp.api.BookingApi;
import com.example.evchargingapp.models.Booking;
import com.example.evchargingapp.utils.SharedPrefsHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EVOperatorCompletedBookingsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private BookingAdapter adapter;
    private List<Booking> completedList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evoperator_completed_bookings);

        recyclerView = findViewById(R.id.recyclerCompletedBookings);
        progressBar = findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BookingAdapter(this, completedList, new BookingAdapter.BookingListener() {
            @Override public void onModify(Booking booking) { /* Not allowed */ }
            @Override public void onCancel(Booking booking) { /* Not allowed */ }
            @Override public void onShowQR(Booking booking) { /* Not needed */ }
        });

        recyclerView.setAdapter(adapter);

        // Load completed bookings from backend
        loadCompletedBookings();
    }

    private void loadCompletedBookings() {
        progressBar.setVisibility(View.VISIBLE);

        new AsyncTask<Void, Void, List<Booking>>() {
            @Override
            protected List<Booking> doInBackground(Void... voids) {
                List<Booking> list = new ArrayList<>();
                try {
                    String token = SharedPrefsHelper.getToken(EVOperatorCompletedBookingsActivity.this);
                    String stationId = SharedPrefsHelper.getStationId(EVOperatorCompletedBookingsActivity.this);

                    JSONArray arr = BookingApi.getBookingsByStation("ST-20251023-A0B76", token);

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject o = arr.getJSONObject(i);
                        int status = o.optInt("status");
                        if (status == 3) { // Completed
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
                    Log.e("CompletedBookings", "Error loading bookings", e);
                }
                return list;
            }

            @Override
            protected void onPostExecute(List<Booking> bookings) {
                progressBar.setVisibility(View.GONE);
                completedList.clear();
                completedList.addAll(bookings);
                adapter.notifyDataSetChanged();

                if (bookings.isEmpty()) {
                    Toast.makeText(EVOperatorCompletedBookingsActivity.this,
                            "No completed bookings found", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
}
