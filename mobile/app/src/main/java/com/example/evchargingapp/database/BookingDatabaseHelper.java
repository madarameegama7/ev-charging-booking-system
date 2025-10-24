package com.example.evchargingapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BookingDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "evchargingapp.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_BOOKING = "Booking";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_BOOKING_ID = "bookingId";
    public static final String COLUMN_STATION_ID = "stationId";
    public static final String COLUMN_OWNER_NIC = "ownerNic";
    public static final String COLUMN_START = "startTimeUtc";
    public static final String COLUMN_END = "endTimeUtc";
    public static final String COLUMN_STATUS = "status";

    private static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_BOOKING + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_BOOKING_ID + " TEXT UNIQUE, " +
                    COLUMN_STATION_ID + " TEXT, " +
                    COLUMN_OWNER_NIC + " TEXT, " +
                    COLUMN_START + " TEXT, " +
                    COLUMN_END + " TEXT, " +
                    COLUMN_STATUS + " TEXT" +
                    ");";

    public BookingDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKING);
        onCreate(db);
    }
}
