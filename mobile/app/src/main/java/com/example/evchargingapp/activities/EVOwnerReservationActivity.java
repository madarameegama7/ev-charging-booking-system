package com.example.evchargingapp.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EVOwnerReservationActivity extends AppCompatActivity {

    private Button btnAddBooking;
    private ListView lvBookings;
    private ProgressBar progressBar;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private List<Booking> bookingList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    private EditText etStationId, etStartTime, etEndTime;
    private Button btnSubmitBooking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evowner_reservation);

        btnAddBooking = findViewById(R.id.btnAddBooking);
        lvBookings = findViewById(R.id.lvBookings);
        progressBar = findViewById(R.id.progressBar);

        btnAddBooking.setOnClickListener(v -> showBookingForm());

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        lvBookings.setAdapter(adapter);

        lvBookings.setOnItemClickListener((parent, view, position, id) -> {
            Booking selected = bookingList.get(position);
            showCancelConfirmation(selected);
        });

        fetchBookings();
    }

    private void showBookingForm() {
        View formView = getLayoutInflater().inflate(R.layout.dialog_add_booking, null);
        etStationId = formView.findViewById(R.id.etStationId);
        etStartTime = formView.findViewById(R.id.etStartTime);
        etEndTime = formView.findViewById(R.id.etEndTime);
        btnSubmitBooking = formView.findViewById(R.id.btnSubmitBooking);

        Calendar calendar = Calendar.getInstance();

        // Date + Time picker
        etStartTime.setOnClickListener(v -> pickDateTime(etStartTime));
        etEndTime.setOnClickListener(v -> pickDateTime(etEndTime));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(formView);
        AlertDialog dialog = builder.create();
        dialog.show();

        btnSubmitBooking.setOnClickListener(v -> {
            String stationId = etStationId.getText().toString().trim();
            String start = etStartTime.getText().toString().trim();
            String end = etEndTime.getText().toString().trim();

            if (stationId.isEmpty() || start.isEmpty() || end.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            dialog.dismiss();
            createBooking(stationId, start, end);
        });
    }

    private void pickDateTime(EditText target) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dpd = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            TimePickerDialog tpd = new TimePickerDialog(this, (timeView, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                target.setText(sdf.format(calendar.getTime()));
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            tpd.show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dpd.show();
    }

    private void createBooking(String stationId, String start, String end) {
        String nic = SharedPrefsHelper.getNic(this);
        String token = SharedPrefsHelper.getToken(this);

        toggleLoading(true);
        executor.execute(() -> {
            try {
                JSONObject resp = BookingApi.createBooking(stationId, nic, start, end, token);
                runOnUiThread(() -> {
                    toggleLoading(false);
                    Toast.makeText(this, "Booking created successfully", Toast.LENGTH_SHORT).show();
                    fetchBookings();
                });
            } catch (final Exception ex) {
                ex.printStackTrace();
                runOnUiThread(() -> {
                    toggleLoading(false);
                    Toast.makeText(this, "Failed to create booking: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void fetchBookings() {
        String nic = SharedPrefsHelper.getNic(this);
        String token = SharedPrefsHelper.getToken(this);

        toggleLoading(true);
        executor.execute(() -> {
            try {
                JSONArray resp = BookingApi.getBookingsByOwner(nic, token);
                bookingList.clear();
                for (int i = 0; i < resp.length(); i++) {
                    JSONObject obj = resp.getJSONObject(i);
                    Booking b = new Booking();
                    b.setBookingId(obj.optString("id"));
                    b.setStationId(obj.optString("stationId"));
                    b.setStartTimeUtc(obj.optString("startTimeUtc"));
                    b.setEndTimeUtc(obj.optString("endTimeUtc"));
                    b.setStatus(obj.optString("status"));
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
            display.add("Station: " + b.getStationId() + "\nStart: " + b.getStartTimeUtc() +
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
            switch (b.getStatus()) {
                case "Approved":
                    color = ContextCompat.getColor(this, android.R.color.holo_green_light);
                    break;
                case "Cancelled":
                    color = ContextCompat.getColor(this, android.R.color.holo_red_light);
                    break;
                default:
                    color = ContextCompat.getColor(this, android.R.color.holo_orange_light);
            }
            item.setBackgroundColor(color);
        }
    }

    private void showCancelConfirmation(Booking booking) {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Booking")
                .setMessage("Are you sure you want to cancel this booking?")
                .setPositiveButton("Yes", (dialog, which) -> cancelBooking(booking))
                .setNegativeButton("No", null)
                .show();
    }

    private void cancelBooking(Booking booking) {
        String token = SharedPrefsHelper.getToken(this);
        JSONObject update = new JSONObject();
        try { update.put("status", "Cancelled"); } catch (Exception ignored) {}

        toggleLoading(true);
        executor.execute(() -> {
            try {
                BookingApi.updateBooking(booking.getBookingId(), update, token);
                runOnUiThread(() -> {
                    toggleLoading(false);
                    Toast.makeText(this, "Booking cancelled", Toast.LENGTH_SHORT).show();
                    fetchBookings();
                });
            } catch (final Exception ex) {
                ex.printStackTrace();
                runOnUiThread(() -> {
                    toggleLoading(false);
                    Toast.makeText(this, "Failed to cancel: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void toggleLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnAddBooking.setEnabled(!isLoading);
    }
}
