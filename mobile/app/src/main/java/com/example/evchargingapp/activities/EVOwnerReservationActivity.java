package com.example.evchargingapp.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;



import com.example.evchargingapp.R;
import com.example.evchargingapp.adapters.BookingAdapter;
import com.example.evchargingapp.api.BookingApi;
import com.example.evchargingapp.api.StationApi;
import com.example.evchargingapp.database.BookingDAO;
import com.example.evchargingapp.models.Booking;
import com.example.evchargingapp.utils.SharedPrefsHelper;
import com.google.android.material.tabs.TabLayout;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EVOwnerReservationActivity extends AppCompatActivity {

    private Button btnAddBooking;
    private RecyclerView rvBookings;
    private ProgressBar progressBar;
    private TabLayout tabLayout;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private List<Booking> bookingList = new ArrayList<>();
    private List<Booking> displayedList = new ArrayList<>();
    private BookingAdapter adapter;
    private Booking editingBooking = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_evowner_reservation);

            btnAddBooking = findViewById(R.id.btnAddBooking);
            rvBookings = findViewById(R.id.rvBookings);
            progressBar = findViewById(R.id.progressBar);
            tabLayout = findViewById(R.id.tabLayout);

            rvBookings.setLayoutManager(new LinearLayoutManager(this));
            adapter = new BookingAdapter(this, displayedList, new BookingAdapter.BookingListener() {
                @Override
                public void onModify(Booking booking) {
                    showBookingForm(booking);
                }

                @Override
                public void onCancel(Booking booking) {
                    showCancelConfirmation(booking);
                }

                @Override
                public void onShowQR(Booking booking) {
                    showQrDialog(booking);
                }
            });
            rvBookings.setAdapter(adapter);

            btnAddBooking.setOnClickListener(v -> showBookingForm(null));

            tabLayout.addTab(tabLayout.newTab().setText("Upcoming"));
            tabLayout.addTab(tabLayout.newTab().setText("Past"));

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    filterBookings(tab.getPosition() == 0);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            });

            fetchBookings();

        } catch (Exception e) {
            e.printStackTrace(); // This will show the crash reason in Logcat
            Toast.makeText(this, "Error in onCreate: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void filterBookings(boolean upcoming) {
        displayedList.clear();
        Date now = new Date();
        for (Booking b : bookingList) {
            try {
                Date start = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).parse(b.getStartTimeUtc());
                if (upcoming && start.after(now)) {
                    displayedList.add(b);
                } else if (!upcoming && start.before(now)) {
                    displayedList.add(b);
                }
            } catch (Exception ignored) {
            }
        }
        adapter.notifyDataSetChanged();
        if (displayedList.isEmpty()) {
            Toast.makeText(this, "No bookings found", Toast.LENGTH_SHORT).show();
        }

    }

    // Booking form, create / modify
    private void showBookingForm(Booking booking) {
        editingBooking = booking;
        View formView = getLayoutInflater().inflate(R.layout.dialog_add_booking, null);
        Spinner spinnerStation = formView.findViewById(R.id.spinnerStation);
        EditText etStartTime = formView.findViewById(R.id.etStartTime);
        EditText etEndTime = formView.findViewById(R.id.etEndTime);
        Button btnSubmitBooking = formView.findViewById(R.id.btnSubmitBooking);

        final Map<String, String> nameToId = new HashMap<>(); // keep only this

        executor.execute(() -> {
            try {
                String token = SharedPrefsHelper.getToken(this);
                JSONArray stations = StationApi.getAllStations(token);
                List<String> stationNames = new ArrayList<>();

                for (int i = 0; i < stations.length(); i++) {
                    JSONObject s = stations.getJSONObject(i);
                    String id = s.getString("stationId"); // server ID
                    String name = s.getString("name");

                    stationNames.add(name);
                    nameToId.put(name, id); // fill the outer map
                }

                runOnUiThread(() -> {
                    ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_item, stationNames);
                    adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerStation.setAdapter(adapterSpinner);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        etStartTime.setOnClickListener(v -> pickDateTime(etStartTime));
        etEndTime.setOnClickListener(v -> pickDateTime(etEndTime));

        AlertDialog dialog = new AlertDialog.Builder(this).setView(formView).create();
        dialog.show();
        btnSubmitBooking.setOnClickListener(v -> {
            String stationName = (String) spinnerStation.getSelectedItem();
            if (stationName == null || !nameToId.containsKey(stationName)) {
                Toast.makeText(this, "Please select a station", Toast.LENGTH_SHORT).show();
                return;
            }

            String stationId = nameToId.get(stationName);
            String start = etStartTime.getText().toString().trim();
            String end = etEndTime.getText().toString().trim();

            if (start.isEmpty() || end.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                // Parse local dates
                SimpleDateFormat sdfLocal = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                Date startDate = sdfLocal.parse(start);
                Date endDate = sdfLocal.parse(end);

                // Convert to UTC
                SimpleDateFormat sdfUtc = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
                sdfUtc.setTimeZone(TimeZone.getTimeZone("UTC"));
                String startUtc = sdfUtc.format(startDate);
                String endUtc = sdfUtc.format(endDate);

                // Log for debugging
                Log.d("BookingDebug", "Selected Start (local): " + start);
                Log.d("BookingDebug", "Selected End   (local): " + end);
                Log.d("BookingDebug", "Start UTC sent : " + startUtc);
                Log.d("BookingDebug", "End UTC sent   : " + endUtc);
                Log.d("BookingDebug", "Current UTC time: " + sdfUtc.format(new Date()));

                // Validate 1-hour rule before sending
                Date nowUtc = new Date();
                Calendar oneHourLater = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                oneHourLater.setTime(nowUtc);
                oneHourLater.add(Calendar.HOUR_OF_DAY, 1);

                if (startDate.before(oneHourLater.getTime())) {
                    Toast.makeText(this, "Start time must be at least 1 hour from now", Toast.LENGTH_LONG).show();
                    return;
                }

                // Optional: validate within 7 days
                Calendar limit = Calendar.getInstance();
                limit.add(Calendar.DAY_OF_YEAR, 7);
                if (startDate.after(limit.getTime()) || endDate.after(limit.getTime())) {
                    Toast.makeText(this, "Both start and end times must be within 7 days from now", Toast.LENGTH_LONG)
                            .show();
                    return;
                }

                // Show confirmation dialog
                String summary = "📍 Station: " + stationName
                        + "\n\n🕒 Start: " + startUtc
                        + "\n⏰ End: " + endUtc
                        + "\n\nPlease confirm your reservation details.";

                new AlertDialog.Builder(this)
                        .setTitle("Review Reservation")
                        .setMessage(summary)
                        .setPositiveButton("Confirm", (d, w) -> {
                            dialog.dismiss();
                            if (editingBooking == null) {
                                createBooking(stationId, startUtc, endUtc);
                            } else {
                                modifyBooking(editingBooking.getBookingId(), stationId, startUtc, endUtc);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();

            } catch (Exception ex) {
                Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // Pick date and time, store in local format
    private void pickDateTime(EditText target) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            calendar.set(year, month, day);
            new TimePickerDialog(this, (v, hour, min) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, min);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                // Format in local time
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                sdf.setTimeZone(TimeZone.getDefault()); // ensures local timezone
                target.setText(sdf.format(calendar.getTime()));

                Log.d("BookingDebug", "Selected (local): " + target.getText().toString());

            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void toggleLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        // Optionally disable user interaction while loading
        getWindow().getDecorView().setEnabled(!show);
    }

    private void fetchBookings() {
        String nic = SharedPrefsHelper.getNic(this);
        String token = SharedPrefsHelper.getToken(this);
        Log.d("EVOwnerReservation", "NIC=" + nic + ", Token=" + token);

        toggleLoading(true);
        executor.execute(() -> {
            BookingDAO bookingDAO = new BookingDAO(this);
            bookingDAO.open();
            // First read from local DB
            bookingList.clear();
            bookingList.addAll(bookingDAO.getBookingsByOwner(nic));

            runOnUiThread(() -> {
                filterBookings(tabLayout.getSelectedTabPosition() == 0);
                toggleLoading(false);
            });

            try {
                // Then try to fetch from backend and update local DB
                JSONArray resp = BookingApi.getBookingsByOwner(nic, token);
                bookingDAO.deleteAllBookings(); // clear old cache
                for (int i = 0; i < resp.length(); i++) {
                    JSONObject obj = resp.getJSONObject(i);
                    Booking b = new Booking();
                    b.setBookingId(obj.optString("id"));
                    b.setBookingId(obj.optString("bookingId", obj.optString("BookingId", "")));
                    b.setStationId(obj.optString("stationId", obj.optString("StationId", "")));
                    b.setOwnerNic(nic);
                    b.setStartTimeUtc(obj.optString("startTimeUtc"));
                    b.setEndTimeUtc(obj.optString("endTimeUtc"));
                    b.setStatus(obj.optInt("status"));
                    bookingList.add(b);
                    bookingDAO.insertOrUpdateBooking(b); // cache locally
                }

                runOnUiThread(() -> filterBookings(tabLayout.getSelectedTabPosition() == 0));

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error fetching bookings", Toast.LENGTH_SHORT).show());
            } finally {
                bookingDAO.close();
            }
        });
    }

  private void createBooking(String stationId, String start, String end) {
    toggleLoading(true);
    executor.execute(() -> {
        try {
            String ownerNic = SharedPrefsHelper.getNic(this);
            String token = SharedPrefsHelper.getToken(this);

            // Build JSON object
            JSONObject json = new JSONObject();
            json.put("stationId", stationId);
            json.put("ownerNic", ownerNic);
            json.put("startTimeUtc", start);
            json.put("endTimeUtc", end);

            // Call API
            JSONObject response = BookingApi.createBooking(json, token);

            runOnUiThread(() -> {
                toggleLoading(false);
                fetchBookings();
                Toast.makeText(this, "Booking created", Toast.LENGTH_SHORT).show();
            });
        } catch (Exception e) {
            Log.e("CreateBookingError", "Error creating booking", e);
            runOnUiThread(() -> {
                toggleLoading(false);
                Toast.makeText(this, "Failed to create booking: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
        }
    });
}

 
private void modifyBooking(String bookingId, String stationId, String start, String end) {
    toggleLoading(true);
    executor.execute(() -> {
        try {
            String token = SharedPrefsHelper.getToken(this);

            // Build JSON object
            JSONObject update = new JSONObject();
            update.put("stationId", stationId);
            update.put("startTimeUtc", start);
            update.put("endTimeUtc", end);

            // Call API
            JSONObject response = BookingApi.updateBooking(bookingId, update, token);

            runOnUiThread(() -> {
                toggleLoading(false);
                fetchBookings();
                Toast.makeText(this, "Booking modified", Toast.LENGTH_SHORT).show();
            });
        } catch (Exception e) {
            Log.e("ModifyBookingError", "Error modifying booking", e);
            runOnUiThread(() -> {
                toggleLoading(false);
                Toast.makeText(this, "Failed to modify booking", Toast.LENGTH_SHORT).show();
            });
        }
    });
}


    private void showCancelConfirmation(Booking booking) {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Reservation")
                .setMessage("Are you sure you want to cancel this reservation?")
                .setPositiveButton("Yes", (d, w) -> cancelBooking(booking.getBookingId()))
                .setNegativeButton("No", null)
                .show();
    }

    private void cancelBooking(String bookingId) {
        toggleLoading(true);
        executor.execute(() -> {
            boolean success = BookingApi.cancelBooking(bookingId);
            runOnUiThread(() -> {
                toggleLoading(false);
                if (success) {
                    fetchBookings();
                    Toast.makeText(this, "Booking cancelled", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to cancel booking", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

 private void showQrDialog(Booking booking) {
    try {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_booking_qr, null);
        builder.setView(dialogView);

        ImageView ivQr = dialogView.findViewById(R.id.ivQrCode);
        TextView tvBookingId = dialogView.findViewById(R.id.tvBookingId);
        TextView tvStationName = dialogView.findViewById(R.id.tvStationName);
        TextView tvTimeRange = dialogView.findViewById(R.id.tvTimeRange);
        TextView tvStatus = dialogView.findViewById(R.id.tvStatus);
        Button btnClose = dialogView.findViewById(R.id.btnCloseQr);

        // Generate QR code
        Bitmap qrBitmap = new com.journeyapps.barcodescanner.BarcodeEncoder()
                .encodeBitmap(booking.getBookingId(),
                        com.google.zxing.BarcodeFormat.QR_CODE, 400, 400);
        ivQr.setImageBitmap(qrBitmap);

        // Fill details
        tvBookingId.setText("Booking ID: " + booking.getBookingId());
        tvStationName.setText("Station: " + booking.getStationId());
        tvTimeRange.setText(
                "Start: " + formatUtcToLocal(booking.getStartTimeUtc()) +
                        "\nEnd: " + formatUtcToLocal(booking.getEndTimeUtc())
        );

        tvStatus.setText("Status: " + booking.getStatusText());

        AlertDialog dialog = builder.create();

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();

    } catch (Exception e) {
        Toast.makeText(this, "Failed to generate QR", Toast.LENGTH_SHORT).show();
        e.printStackTrace();
    }
}


    private String formatUtcToLocal(String utcDateStr) {
        try {
            // 1. Parse the UTC date string
            SimpleDateFormat sdfUtc = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
            sdfUtc.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = sdfUtc.parse(utcDateStr);

            // 2. Format it to local, user-friendly string
            SimpleDateFormat sdfLocal = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.US);
            sdfLocal.setTimeZone(TimeZone.getDefault()); // converts to local timezone
            return sdfLocal.format(date);

        } catch (Exception e) {
            e.printStackTrace();
            return utcDateStr; // fallback
        }
    }




}
