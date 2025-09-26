/*
 * File: ReservationAdapter.java
 * Author: Janudi Adhikari
 * Date: 2025-09-26
 * Purpose: Custom adapter to show reservations in a ListView
 */

package com.example.evchargingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.evchargingapp.models.Reservation;

import java.util.List;

public class ReservationAdapter extends ArrayAdapter<Reservation> {

    public ReservationAdapter(@NonNull Context context, @NonNull List<Reservation> reservations) {
        super(context, 0, reservations);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_reservation, parent, false);
        }

        Reservation reservation = getItem(position);

        TextView tvStation = convertView.findViewById(R.id.tvStation);
        TextView tvDateTime = convertView.findViewById(R.id.tvDateTime);
        TextView tvStatus = convertView.findViewById(R.id.tvStatus);

        tvStation.setText("Station: " + reservation.getStationId());
        tvDateTime.setText("Date & Time: " + reservation.getDateTime());
        tvStatus.setText("Status: " + reservation.getStatus());

        return convertView;
    }
}
