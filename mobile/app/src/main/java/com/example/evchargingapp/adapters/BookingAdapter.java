package com.example.evchargingapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
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
        holder.tvStationId.setText("Station: " + b.getStationId());
        holder.tvTimeRange.setText("Start: " + b.getStartTimeUtc() + " | End: " + b.getEndTimeUtc());
        holder.tvStatus.setText(b.getStatus());

        int color;
        switch (b.getStatus()) {
            case "Approved": color = ContextCompat.getColor(context, android.R.color.holo_green_light); break;
            case "Cancelled": color = ContextCompat.getColor(context, android.R.color.holo_red_light); break;
            default: color = ContextCompat.getColor(context, android.R.color.holo_orange_light);
        }
        holder.tvStatus.setTextColor(color);

        holder.btnModify.setEnabled("Pending".equals(b.getStatus()));
        holder.btnModify.setOnClickListener(v -> listener.onModify(b));
        holder.btnCancel.setEnabled(!"Cancelled".equals(b.getStatus()));
        holder.btnCancel.setOnClickListener(v -> listener.onCancel(b));
        holder.btnQR.setEnabled("Approved".equals(b.getStatus()));
        holder.btnQR.setOnClickListener(v -> listener.onShowQR(b));
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStationId, tvTimeRange, tvStatus;
        Button btnModify, btnCancel, btnQR;

        ViewHolder(View itemView) {
            super(itemView);
            tvStationId = itemView.findViewById(R.id.tvStationId);
            tvTimeRange = itemView.findViewById(R.id.tvTimeRange);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnModify = itemView.findViewById(R.id.btnModify);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            btnQR = itemView.findViewById(R.id.btnQR);
        }
    }
}
