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
        holder.tvStationId.setText("Station: " + b.getStationId());
        holder.tvBookingId.setText("Booking ID: " + b.getBookingId());
        holder.tvTimeRange.setText("Start: " + b.getStartTimeUtc() + "\nEnd: " + b.getEndTimeUtc());
        holder.tvStatus.setText(b.getStatusText());

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

    @Override
    public int getItemCount() {
        return bookings.size();
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
