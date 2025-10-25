package com.example.evchargingapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.evchargingapp.R;
import com.example.evchargingapp.models.Booking;

import java.util.List;

public class PendingBookingAdapter extends RecyclerView.Adapter<PendingBookingAdapter.ViewHolder> {

    public interface PendingBookingListener {
        void onApprove(Booking booking);
        void onCancel(Booking booking);
    }

    private final List<Booking> bookings;
    private final PendingBookingListener listener;
    private final Context context;

    public PendingBookingAdapter(Context context, List<Booking> bookings, PendingBookingListener listener) {
        this.bookings = bookings;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.booking_reservation_pending, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Booking b = bookings.get(position);

        holder.tvStationId.setText("Station: " + b.getStationId());
        holder.tvBookingId.setText("Booking ID: " + b.getBookingId());
        holder.tvTimeRange.setText("Start: " + b.getStartTimeUtc() + "\nEnd: " + b.getEndTimeUtc());
        holder.tvStatus.setText(b.getStatusText());

        holder.btnApprove.setOnClickListener(v -> listener.onApprove(b));
        holder.btnCancel.setOnClickListener(v -> listener.onCancel(b));
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStationId, tvBookingId, tvTimeRange, tvStatus;
        Button btnApprove, btnCancel;

        ViewHolder(View itemView) {
            super(itemView);
            tvStationId = itemView.findViewById(R.id.tvStationId);
            tvBookingId = itemView.findViewById(R.id.tvBookingId);
            tvTimeRange = itemView.findViewById(R.id.tvTimeRange);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }
    }
}
