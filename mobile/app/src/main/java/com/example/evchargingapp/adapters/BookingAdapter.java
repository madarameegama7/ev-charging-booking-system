package com.example.evchargingapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.evchargingapp.R;
import com.example.evchargingapp.models.Booking;
import com.example.evchargingapp.api.StationApi;
import com.example.evchargingapp.utils.SharedPrefsHelper;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {

    public interface BookingListener {
        void onModify(Booking booking);
        void onCancel(Booking booking);
        void onShowQR(Booking booking);
    }

    private final List<Booking> bookings;
    private final BookingListener listener;
    private final Context context;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Map<String, String> stationNameCache = new HashMap<>();

    public BookingAdapter(Context context, List<Booking> bookings, BookingListener listener) {
        this.bookings = bookings;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_reservation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Booking b = bookings.get(position);

        // Display booking details
        holder.tvBookingId.setText("Booking ID: " + b.getBookingId());
        
        // Format time range to local time
        holder.tvTimeRange.setText("Start: " + formatUtcToLocal(b.getStartTimeUtc()) + 
                                 "\nEnd: " + formatUtcToLocal(b.getEndTimeUtc()));
        
        holder.tvStatus.setText(b.getStatusText());

        // Set station name - first check cache, then fetch if needed
        String stationId = b.getStationId();
        if (stationNameCache.containsKey(stationId)) {
            holder.tvStationId.setText("Station: " + stationNameCache.get(stationId));
        } else {
            holder.tvStationId.setText("Station: Loading...");
            fetchStationName(stationId, holder.tvStationId, position);
        }

        // Modify button: enabled only for pending bookings
        holder.btnModify.setEnabled("Pending".equals(b.getStatusText()));
        holder.btnModify.setOnClickListener(v -> {
            Log.d("BookingAdapter", "Modify clicked for bookingId: " + b.getBookingId());
            listener.onModify(b);
        });

        // Cancel button: enabled unless already cancelled
        holder.btnCancel.setEnabled(!"Cancelled".equals(b.getStatusText()));
        holder.btnCancel.setOnClickListener(v -> {
            Log.d("BookingAdapter", "Cancel clicked for bookingId: " + b.getBookingId());
            listener.onCancel(b);
        });

        // QR button: enabled for approved bookings
        holder.btnQR.setEnabled("Approved".equals(b.getStatusText()));
        holder.btnQR.setOnClickListener(v -> {
            Log.d("BookingAdapter", "QR clicked for bookingId: " + b.getBookingId());
            listener.onShowQR(b);
        });
    }

    private void fetchStationName(String stationId, TextView textView, int position) {
        executor.execute(() -> {
            try {
                String token = SharedPrefsHelper.getToken(context);
                JSONObject station = StationApi.getStationById(stationId, token);
                String stationName = station.getString("name");
                
                // Cache the station name
                stationNameCache.put(stationId, stationName);
                
                // Update UI on main thread
                ((android.app.Activity) context).runOnUiThread(() -> {
                    // Check if this holder is still displaying the same station
                    if (position < bookings.size() && bookings.get(position).getStationId().equals(stationId)) {
                        textView.setText("Station: " + stationName);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                // Fallback to station ID if name fetch fails
                stationNameCache.put(stationId, stationId);
                ((android.app.Activity) context).runOnUiThread(() -> {
                    if (position < bookings.size() && bookings.get(position).getStationId().equals(stationId)) {
                        textView.setText("Station: " + stationId);
                    }
                });
            }
        });
    }

    private String formatUtcToLocal(String utcDateStr) {
        try {
            SimpleDateFormat sdfUtc = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
            sdfUtc.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = sdfUtc.parse(utcDateStr);

            SimpleDateFormat sdfLocal = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.US);
            sdfLocal.setTimeZone(TimeZone.getDefault());
            return sdfLocal.format(date);

        } catch (Exception e) {
            e.printStackTrace();
            return utcDateStr; // fallback
        }
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    // Method to update station name cache (useful to pre-fetch station names)
    public void updateStationNameCache(Map<String, String> stationNames) {
        stationNameCache.putAll(stationNames);
        notifyDataSetChanged();
    }

    // Clear cache when adapter is no longer needed
    public void clearCache() {
        stationNameCache.clear();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStationId, tvBookingId, tvTimeRange, tvStatus;
        Button btnModify, btnCancel, btnQR;

        ViewHolder(View itemView) {
            super(itemView);
            tvStationId = itemView.findViewById(R.id.tvStationId);
            tvBookingId = itemView.findViewById(R.id.tvBookingId);
            tvTimeRange = itemView.findViewById(R.id.tvTimeRange);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnModify = itemView.findViewById(R.id.btnModify);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            btnQR = itemView.findViewById(R.id.btnQR);
        }
    }
}