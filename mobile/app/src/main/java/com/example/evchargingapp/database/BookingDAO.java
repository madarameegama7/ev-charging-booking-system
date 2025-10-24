package com.example.evchargingapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.evchargingapp.models.Booking;

import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    private SQLiteDatabase database;
    private BookingDatabaseHelper dbHelper;

    public BookingDAO(Context context) {
        dbHelper = new BookingDatabaseHelper(context);
    }

    public void open() { database = dbHelper.getWritableDatabase(); }
    public void close() { dbHelper.close(); }

    public long insertOrUpdateBooking(Booking booking) {
        // Ensure DB is open
        if (database == null || !database.isOpen()) {
            open();
        }

        ContentValues values = new ContentValues();
        values.put(BookingDatabaseHelper.COLUMN_BOOKING_ID, booking.getBookingId());
        values.put(BookingDatabaseHelper.COLUMN_STATION_ID, booking.getStationId());
        values.put(BookingDatabaseHelper.COLUMN_OWNER_NIC, booking.getOwnerNic());
        values.put(BookingDatabaseHelper.COLUMN_START, booking.getStartTimeUtc());
        values.put(BookingDatabaseHelper.COLUMN_END, booking.getEndTimeUtc());
        values.put(BookingDatabaseHelper.COLUMN_STATUS, booking.getStatus());

        return database.insertWithOnConflict(
                BookingDatabaseHelper.TABLE_BOOKING,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
        );
    }

    public List<Booking> getBookingsByOwner(String nic) {
        // Ensure DB is open
        if (database == null || !database.isOpen()) {
            open();
        }

        List<Booking> list = new ArrayList<>();
        Cursor cursor = database.query(
                BookingDatabaseHelper.TABLE_BOOKING,
                null,
                BookingDatabaseHelper.COLUMN_OWNER_NIC + "=?",
                new String[]{nic},
                null, null, BookingDatabaseHelper.COLUMN_START + " ASC"
        );

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        Booking b = new Booking();
                        b.setBookingId(cursor.getString(cursor.getColumnIndexOrThrow(BookingDatabaseHelper.COLUMN_BOOKING_ID)));
                        b.setStationId(cursor.getString(cursor.getColumnIndexOrThrow(BookingDatabaseHelper.COLUMN_STATION_ID)));
                        b.setOwnerNic(cursor.getString(cursor.getColumnIndexOrThrow(BookingDatabaseHelper.COLUMN_OWNER_NIC)));
                        b.setStartTimeUtc(cursor.getString(cursor.getColumnIndexOrThrow(BookingDatabaseHelper.COLUMN_START)));
                        b.setEndTimeUtc(cursor.getString(cursor.getColumnIndexOrThrow(BookingDatabaseHelper.COLUMN_END)));
                        b.setStatus(cursor.getInt(cursor.getColumnIndexOrThrow(BookingDatabaseHelper.COLUMN_STATUS)));
                        list.add(b);
                    } while (cursor.moveToNext());
                }
            } finally {
                cursor.close();
            }
        }
        return list;
    }

    public void deleteAllBookings() {
        // Ensure DB is open
        if (database == null || !database.isOpen()) {
            open();
        }

        database.delete(BookingDatabaseHelper.TABLE_BOOKING, null, null);
    }
}
