package com.example.evchargingapp.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.evchargingapp.R;
import com.example.evchargingapp.adapters.BookingAdapter;
import com.example.evchargingapp.api.BookingApi;
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
                    showQrDialog(booking.getBookingId());
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
                }else if (!upcoming && start.before(now)) {
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
        EditText etStationId = formView.findViewById(R.id.etStationId);
        EditText etStartTime = formView.findViewById(R.id.etStartTime);
        EditText etEndTime = formView.findViewById(R.id.etEndTime);
        Button btnSubmitBooking = formView.findViewById(R.id.btnSubmitBooking);

        if (booking != null) {
            etStationId.setText(booking.getStationId());
            etStartTime.setText(booking.getStartTimeUtc());
            etEndTime.setText(booking.getEndTimeUtc());
        }

        etStartTime.setOnClickListener(v -> pickDateTime(etStartTime));
        etEndTime.setOnClickListener(v -> pickDateTime(etEndTime));

        AlertDialog dialog = new AlertDialog.Builder(this).setView(formView).create();
        dialog.show();

        btnSubmitBooking.setOnClickListener(v -> {
            String stationId = etStationId.getText().toString().trim();
            String start = etStartTime.getText().toString().trim();
            String end = etEndTime.getText().toString().trim();

            if (stationId.isEmpty() || start.isEmpty() || end.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                Date startDate = sdf.parse(start);
                Date endDate = sdf.parse(end);
                Date now = new Date();
                Calendar limit = Calendar.getInstance();
                limit.add(Calendar.DAY_OF_YEAR, 7);

                if (startDate.after(limit.getTime()) || endDate.after(limit.getTime())) {
                    Toast.makeText(this, "Both start and end times must be within 7 days from now", Toast.LENGTH_LONG).show();
                    return;
                }

                // Show summary dialog before final confirmation
                String summary = "📍 Station ID: " + stationId
                        + "\n\n🕒 Start: " + start
                        + "\n⏰ End: " + end
                        + "\n\nPlease confirm your reservation details.";

                new AlertDialog.Builder(this)
                        .setTitle("Review Reservation")
                        .setMessage(summary)
                        .setPositiveButton("Confirm", (d, w) -> {
                            dialog.dismiss();
                            if (editingBooking == null) {
                                createBooking(stationId, start, end); 
                            }else {
                                modifyBooking(editingBooking.getBookingId(), stationId, start, end);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();

            } catch (Exception ex) {
                Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void pickDateTime(EditText target) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            calendar.set(year, month, day);
            new TimePickerDialog(this, (v, hour, min) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, min);
                target.setText(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).format(calendar.getTime()));
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void toggleLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnAddBooking.setEnabled(!isLoading);
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
                    b.setStationId(obj.optString("stationId"));
                    b.setOwnerNic(nic);
                    b.setStartTimeUtc(obj.optString("start"));
                    b.setEndTimeUtc(obj.optString("end"));
                    b.setStatus(obj.optString("status"));
                    bookingList.add(b);
                    bookingDAO.insertOrUpdateBooking(b); // cache locally
                }

                runOnUiThread(() -> filterBookings(tabLayout.getSelectedTabPosition() == 0));

            } catch (Exception e) {
                runOnUiThread(()
                        -> Toast.makeText(this, "Error fetching bookings", Toast.LENGTH_SHORT).show()
                );
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
                JSONObject response = BookingApi.createBooking(stationId, ownerNic, start, end, token);

                runOnUiThread(() -> {
                    toggleLoading(false);
                    fetchBookings();
                    Toast.makeText(this, "Booking created", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    toggleLoading(false);
                    Toast.makeText(this, "Failed to create booking", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void modifyBooking(String bookingId, String stationId, String start, String end) {
        toggleLoading(true);
        executor.execute(() -> {
            try {
                String token = SharedPrefsHelper.getToken(this);
                JSONObject update = new JSONObject();
                update.put("stationId", stationId);
                update.put("startTimeUtc", start);
                update.put("endTimeUtc", end);

                BookingApi.updateBooking(bookingId, update, token);

                runOnUiThread(() -> {
                    toggleLoading(false);
                    fetchBookings();
                    Toast.makeText(this, "Booking modified", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
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

    private void showQrDialog(String bookingId) {
        try {
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.encodeBitmap(bookingId, com.google.zxing.BarcodeFormat.QR_CODE, 400, 400);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Booking QR Code")
                    .setPositiveButton("Close", null)
                    .setView(new androidx.appcompat.widget.AppCompatImageView(this) {
                        {
                            setImageBitmap(bitmap);
                            setPadding(32, 32, 32, 32);
                        }
                    })
                    .show();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to generate QR", Toast.LENGTH_SHORT).show();
        }
    }
}
