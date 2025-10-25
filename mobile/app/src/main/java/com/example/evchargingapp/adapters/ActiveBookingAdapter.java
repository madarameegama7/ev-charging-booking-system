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

public class ActiveBookingAdapter extends RecyclerView.Adapter<ActiveBookingAdapter.ViewHolder> {

    public interface ActiveBookingListener {
        void onMarkCompleted(Booking booking);
    }

    private final List<Booking> bookings;
    private final ActiveBookingListener listener;
    private final Context context;

    public ActiveBookingAdapter(Context context, List<Booking> bookings, ActiveBookingListener listener) {
        this.bookings = bookings;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.booking_reservation_active, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Booking b = bookings.get(position);

        holder.tvStationId.setText("Station: " + b.getStationId());
        holder.tvBookingId.setText("Booking ID: " + b.getBookingId());
        holder.tvTimeRange.setText("Start: " + b.getStartTimeUtc() + "\nEnd: " + b.getEndTimeUtc());
        holder.tvStatus.setText("Active");

        holder.btnComplete.setOnClickListener(v -> listener.onMarkCompleted(b));
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStationId, tvBookingId, tvTimeRange, tvStatus;
        Button btnComplete;

        ViewHolder(View itemView) {
            super(itemView);
            tvStationId = itemView.findViewById(R.id.tvStationId);
            tvBookingId = itemView.findViewById(R.id.tvBookingId);
            tvTimeRange = itemView.findViewById(R.id.tvTimeRange);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnComplete = itemView.findViewById(R.id.btnComplete);
        }
    }
}
