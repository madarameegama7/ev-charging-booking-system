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
import com.example.evchargingapp.api.ApiClient;
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
import java.util.concurrent.Future; 

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
            e.printStackTrace();
            Toast.makeText(this, "Error in onCreate: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void filterBookings(boolean upcoming) {
        displayedList.clear();
        Date now = new Date();
        for (Booking b : bookingList) {
            try {
                // Parse as local time
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                sdf.setTimeZone(TimeZone.getDefault());
                Date start = sdf.parse(b.getStartTime());
                
                if (upcoming && start.after(now)) {
                    displayedList.add(b);
                } else if (!upcoming && start.before(now)) {
                    displayedList.add(b);
                }
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }
        adapter.notifyDataSetChanged();
        if (displayedList.isEmpty()) {
            Toast.makeText(this, "No bookings found", Toast.LENGTH_SHORT).show();
        }
    }

    private void showBookingForm(Booking booking) {
        editingBooking = booking;
        View formView = getLayoutInflater().inflate(R.layout.dialog_add_booking, null);
        Spinner spinnerStation = formView.findViewById(R.id.spinnerStation);
        EditText etStartTime = formView.findViewById(R.id.etStartTime);
        EditText etEndTime = formView.findViewById(R.id.etEndTime);
        Button btnSubmitBooking = formView.findViewById(R.id.btnSubmitBooking);

        final Map<String, String> nameToId = new HashMap<>();

        executor.execute(() -> {
            try {
                String token = SharedPrefsHelper.getToken(this);
                JSONArray stations = StationApi.getAllStations(token);
                List<String> stationNames = new ArrayList<>();

                for (int i = 0; i < stations.length(); i++) {
                    JSONObject s = stations.getJSONObject(i);
                    String id = s.getString("stationId");
                    String name = s.getString("name");

                    stationNames.add(name);
                    nameToId.put(name, id);
                }

                runOnUiThread(() -> {
                    ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_item, stationNames);
                    adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerStation.setAdapter(adapterSpinner);
                    
                    // Pre-fill form if modifying existing booking
                    if (booking != null) {
                        try {
                            SimpleDateFormat sdfDisplay = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                            sdfDisplay.setTimeZone(TimeZone.getDefault());
                            
                            // Parse the stored time and display in local format
                            Date startDate = parseFlexibleDate(booking.getStartTime());
                            Date endDate = parseFlexibleDate(booking.getEndTime());
                            
                            etStartTime.setText(sdfDisplay.format(startDate));
                            etEndTime.setText(sdfDisplay.format(endDate));
                            
                            // Set station spinner
                            for (int i = 0; i < adapterSpinner.getCount(); i++) {
                                if (nameToId.get(adapterSpinner.getItem(i)).equals(booking.getStationId())) {
                                    spinnerStation.setSelection(i);
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
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
                // Parse local dates directly
                SimpleDateFormat sdfLocal = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                sdfLocal.setTimeZone(TimeZone.getDefault());
                Date startDate = sdfLocal.parse(start);
                Date endDate = sdfLocal.parse(end);

                // Log for debugging
                Log.d("BookingDebug", "Selected Start (local): " + start);
                Log.d("BookingDebug", "Selected End (local): " + end);
                Log.d("BookingDebug", "Current local time: " + sdfLocal.format(new Date()));

                // Validate start is before end
                if (startDate.after(endDate)) {
                    Toast.makeText(this, "Start time must be before end time", Toast.LENGTH_LONG).show();
                    return;
                }

                // Validate 1-hour rule using local time
                Date nowLocal = new Date();
                Calendar oneHourLater = Calendar.getInstance();
                oneHourLater.setTime(nowLocal);
                oneHourLater.add(Calendar.HOUR_OF_DAY, 1);

                if (startDate.before(oneHourLater.getTime())) {
                    Toast.makeText(this, "Start time must be at least 1 hour from now", Toast.LENGTH_LONG).show();
                    return;
                }

                // Optional: validate within 7 days using local time
                Calendar limit = Calendar.getInstance();
                limit.add(Calendar.DAY_OF_YEAR, 7);
                if (startDate.after(limit.getTime()) || endDate.after(limit.getTime())) {
                    Toast.makeText(this, "Both start and end times must be within 7 days from now", Toast.LENGTH_LONG).show();
                    return;
                }

                // Show confirmation dialog with local times
                String summary = "📍 Station: " + stationName
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
                            } else {
                                modifyBooking(editingBooking.getBookingId(), stationId, start, end);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();

            } catch (Exception ex) {
                Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
                ex.printStackTrace();
            }
        });
    }

    private void testBackendConnection() {
    executor.execute(() -> {
        try {
            String token = SharedPrefsHelper.getToken(this);
            
            JSONObject testData = new JSONObject();
            testData.put("message", "test");
            testData.put("timestamp", System.currentTimeMillis());
            
            Log.d("BackendTest", "Sending test request...");
            
            Future<String> futureResponse = ApiClient.post("booking/test", testData.toString(), token);
            String response = futureResponse.get();
            
            Log.d("BackendTest", "Backend test response: " + response);
            runOnUiThread(() -> {
                Toast.makeText(this, "Backend test successful", Toast.LENGTH_LONG).show();
            });
            
        } catch (Exception e) {
            Log.e("BackendTest", "Backend test failed", e);
            runOnUiThread(() -> {
                Toast.makeText(this, "Backend test failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
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
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                // Format in local time
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                sdf.setTimeZone(TimeZone.getDefault());
                target.setText(sdf.format(calendar.getTime()));

                Log.d("BookingDebug", "Selected (local): " + target.getText().toString());

            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void toggleLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
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
            
            try {
                // First try to fetch from backend and update local DB
                JSONArray resp = BookingApi.getBookingsByOwner(nic, token);
                
                // Clear both lists before adding new data
                bookingList.clear();
                
                // Fetch all stations to map IDs to names
                JSONArray stations = StationApi.getAllStations(token);
                Map<String, String> stationIdToName = new HashMap<>();
                for (int i = 0; i < stations.length(); i++) {
                    JSONObject s = stations.getJSONObject(i);
                    String id = s.getString("stationId");
                    String name = s.getString("name");
                    stationIdToName.put(id, name);
                }

                // Update local database with fresh data from API
                bookingDAO.deleteAllBookings();
                
                for (int i = 0; i < resp.length(); i++) {
                    JSONObject obj = resp.getJSONObject(i);
                    Booking b = new Booking();
                    b.setBookingId(obj.optString("id"));
                    b.setBookingId(obj.optString("bookingId", obj.optString("BookingId", "")));
                    b.setStationId(obj.optString("stationId", obj.optString("StationId", "")));
                    b.setOwnerNic(nic);
                    
                    // Handle both old UTC and new local time formats
                    String startTime = obj.optString("startTime", obj.optString("startTimeUtc", ""));
                    String endTime = obj.optString("endTime", obj.optString("endTimeUtc", ""));
                    b.setStartTime(startTime);
                    b.setEndTime(endTime);
                    
                    b.setStatus(obj.optInt("status"));
                    
                    // Add to bookingList
                    bookingList.add(b);
                    // Cache locally
                    bookingDAO.insertOrUpdateBooking(b);
                }

                // Update adapter with station names if available
                if (adapter != null) {
                    runOnUiThread(() -> {
                        adapter.updateStationNameCache(stationIdToName);
                    });
                }

                runOnUiThread(() -> {
                    filterBookings(tabLayout.getSelectedTabPosition() == 0);
                    toggleLoading(false);
                });

            } catch (Exception e) {
                // If API fails, fall back to local database
                Log.e("EVOwnerReservation", "API fetch failed, using local data", e);
                bookingList.clear();
                bookingList.addAll(bookingDAO.getBookingsByOwner(nic));
                
                runOnUiThread(() -> {
                    filterBookings(tabLayout.getSelectedTabPosition() == 0);
                    toggleLoading(false);
                    Toast.makeText(this, "Using cached data", Toast.LENGTH_SHORT).show();
                });
            } finally {
                bookingDAO.close();
            }
        });
    }

   private void createBooking(String stationId, String startLocal, String endLocal) {
    toggleLoading(true);
    executor.execute(() -> {
        try {
            String ownerNic = SharedPrefsHelper.getNic(this);
            String token = SharedPrefsHelper.getToken(this);

            // Build JSON object with local times
            JSONObject json = new JSONObject();
            json.put("stationId", stationId);
            json.put("ownerNic", ownerNic);
            json.put("startTime", startLocal);
            json.put("endTime", endLocal);

            // Log the request for debugging
            Log.d("BookingDebug", "Sending booking request: " + json.toString());
            Log.d("BookingDebug", "Token: " + (token != null ? token.substring(0, Math.min(10, token.length())) + "..." : "null"));
            Log.d("BookingDebug", "StationId: " + stationId);
            Log.d("BookingDebug", "StartTime: " + startLocal);
            Log.d("BookingDebug", "EndTime: " + endLocal);

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

    private void modifyBooking(String bookingId, String stationId, String startLocal, String endLocal) {
        toggleLoading(true);
        executor.execute(() -> {
            try {
                String token = SharedPrefsHelper.getToken(this);

                // Build JSON object with local times
                JSONObject update = new JSONObject();
                update.put("stationId", stationId);
                update.put("startTime", startLocal);
                update.put("endTime", endLocal);

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
            Bitmap qrBitmap = new BarcodeEncoder()
                    .encodeBitmap(booking.getBookingId(),
                            com.google.zxing.BarcodeFormat.QR_CODE, 400, 400);
            ivQr.setImageBitmap(qrBitmap);

            // Fill details with local time formatting
            tvBookingId.setText("Booking ID: " + booking.getBookingId());
            tvStationName.setText("Station: Loading...");
            tvTimeRange.setText(
                    "Start: " + formatLocalTime(booking.getStartTime()) +
                    "\nEnd: " + formatLocalTime(booking.getEndTime())
            );

            tvStatus.setText("Status: " + booking.getStatusText());

            AlertDialog dialog = builder.create();
            btnClose.setOnClickListener(v -> dialog.dismiss());
            dialog.show();

            // Fetch station name asynchronously
            fetchStationName(booking.getStationId(), tvStationName);

        } catch (Exception e) {
            Toast.makeText(this, "Failed to generate QR", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void fetchStationName(String stationId, TextView tvStationName) {
        executor.execute(() -> {
            try {
                String token = SharedPrefsHelper.getToken(this);
                JSONObject station = StationApi.getStationById(stationId, token);
                String stationName = station.getString("name");
                
                runOnUiThread(() -> {
                    tvStationName.setText("Station: " + stationName);
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    tvStationName.setText("Station: " + stationId);
                });
            }
        });
    }

    // Helper method to format local time for display
    private String formatLocalForDisplay(String localDateStr) {
        try {
            SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            sdfInput.setTimeZone(TimeZone.getDefault());
            Date date = sdfInput.parse(localDateStr);

            SimpleDateFormat sdfDisplay = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.US);
            sdfDisplay.setTimeZone(TimeZone.getDefault());
            return sdfDisplay.format(date);
        } catch (Exception e) {
            return localDateStr;
        }
    }

    // Flexible date parser that handles both local and UTC formats
    private Date parseFlexibleDate(String dateStr) {
        try {
            // Try local format first
            SimpleDateFormat sdfLocal = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            sdfLocal.setTimeZone(TimeZone.getDefault());
            return sdfLocal.parse(dateStr);
        } catch (Exception e1) {
            try {
                // Try UTC format as fallback
                SimpleDateFormat sdfUtc = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
                sdfUtc.setTimeZone(TimeZone.getTimeZone("UTC"));
                return sdfUtc.parse(dateStr);
            } catch (Exception e2) {
                e2.printStackTrace();
                return new Date(); // Return current date as last resort
            }
        }
    }

    // Format time for display (handles both local and UTC)
    private String formatLocalTime(String dateStr) {
        try {
            Date date = parseFlexibleDate(dateStr);
            
            SimpleDateFormat sdfDisplay = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.US);
            sdfDisplay.setTimeZone(TimeZone.getDefault());
            return sdfDisplay.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return dateStr;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}