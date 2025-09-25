/*
 * File: DBHelper.java
 * Author: Janudi Adhikari
 * Date: 2025-09-25
 * Purpose: SQLite helper for EVChargingApp - Users and Reservations tables
 */

package com.example.evchargingapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "evcharging.db";
    private static final int DB_VERSION = 1;

    // Users table
    public static final String TABLE_USERS = "Users";
    public static final String COL_USER_NIC = "NIC";
    public static final String COL_USER_NAME = "Name";
    public static final String COL_USER_EMAIL = "Email";
    public static final String COL_USER_PASSWORD = "Password";
    public static final String COL_USER_ACTIVE = "IsActive"; // 1 active, 0 inactive

    // Reservations table (starter)
    public static final String TABLE_RES = "Reservations";
    public static final String COL_RES_ID = "ReservationID";
    public static final String COL_RES_NIC = "NIC";
    public static final String COL_RES_STATION = "StationID";
    public static final String COL_RES_DATETIME = "DateTime";
    public static final String COL_RES_STATUS = "Status";

    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsers = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_NIC + " TEXT PRIMARY KEY, " +
                COL_USER_NAME + " TEXT, " +
                COL_USER_EMAIL + " TEXT, " +
                COL_USER_PASSWORD + " TEXT, " +
                COL_USER_ACTIVE + " INTEGER DEFAULT 1" +
                ")";
        db.execSQL(createUsers);

        String createRes = "CREATE TABLE " + TABLE_RES + " (" +
                COL_RES_ID + " TEXT PRIMARY KEY, " +
                COL_RES_NIC + " TEXT, " +
                COL_RES_STATION + " TEXT, " +
                COL_RES_DATETIME + " TEXT, " +
                COL_RES_STATUS + " TEXT" +
                ")";
        db.execSQL(createRes);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Simple strategy for assignment: drop and recreate
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    /* ---------- Users methods ---------- */

    // Add a new user. Returns true if inserted (false if already exists)
    public boolean addUser(String nic, String name, String email, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_USER_NIC, nic);
        cv.put(COL_USER_NAME, name);
        cv.put(COL_USER_EMAIL, email);
        cv.put(COL_USER_PASSWORD, password); // NOTE: consider hashing for production
        cv.put(COL_USER_ACTIVE, 1);
        long res = db.insertWithOnConflict(TABLE_USERS, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
        return res != -1;
    }

    // Return Cursor for user by NIC (caller should close cursor)
    public Cursor getUserByNIC(String nic) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_USERS, null, COL_USER_NIC + "=?", new String[]{nic}, null, null, null);
    }

    // Validate credentials (returns true if credentials match and account active)
    public boolean validateUser(String nic, String password) {
        Cursor c = getUserByNIC(nic);
        if (c == null) return false;
        try {
            if (c.moveToFirst()) {
                String stored = c.getString(c.getColumnIndexOrThrow(COL_USER_PASSWORD));
                int isActive = c.getInt(c.getColumnIndexOrThrow(COL_USER_ACTIVE));
                return isActive == 1 && stored.equals(password);
            }
        } finally {
            c.close();
        }
        return false;
    }

    // Check if user exists
    public boolean userExists(String nic) {
        Cursor c = getUserByNIC(nic);
        if (c == null) return false;
        try {
            return c.moveToFirst();
        } finally {
            c.close();
        }
    }

    // Set active / inactive
    public int setUserActive(String nic, boolean active) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_USER_ACTIVE, active ? 1 : 0);
        return db.update(TABLE_USERS, cv, COL_USER_NIC + "=?", new String[]{nic});
    }

    // Update basic profile fields
    public int updateUser(String nic, String name, String email, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        if (name != null) cv.put(COL_USER_NAME, name);
        if (email != null) cv.put(COL_USER_EMAIL, email);
        if (password != null) cv.put(COL_USER_PASSWORD, password);
        return db.update(TABLE_USERS, cv, COL_USER_NIC + "=?", new String[]{nic});
    }

    // Delete user
    public int deleteUser(String nic) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_USERS, COL_USER_NIC + "=?", new String[]{nic});
    }

    /* ---------- Reservations methods (basic) ---------- */

    public boolean addOrUpdateReservation(String resId, String nic, String stationId, String dateTime, String status) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_RES_ID, resId);
        cv.put(COL_RES_NIC, nic);
        cv.put(COL_RES_STATION, stationId);
        cv.put(COL_RES_DATETIME, dateTime);
        cv.put(COL_RES_STATUS, status);
        long res = db.insertWithOnConflict(TABLE_RES, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        return res != -1;
    }

    // Get reservations Cursor by NIC
    public Cursor getReservationsByNIC(String nic) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_RES, null, COL_RES_NIC + "=?", new String[]{nic}, null, null, COL_RES_DATETIME + " DESC");
    }

    // Delete reservation
    public int deleteReservation(String reservationId) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_RES, COL_RES_ID + "=?", new String[]{reservationId});
    }
}
