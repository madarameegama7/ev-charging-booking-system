/*
 * File: DBHelper.java
 * Author: Janudi Adhikari
 * Date: 2025-09-25
 * Purpose: SQLite helper for EVChargingApp - Users and Reservations tables with models
 */

package com.example.evchargingapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.evchargingapp.models.User;
import com.example.evchargingapp.models.Reservation;

import java.util.ArrayList;
import java.util.List;

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

    // Reservations table
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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    /* ---------- Users CRUD ---------- */

    public boolean addUser(String nic, String name, String email, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_USER_NIC, nic);
        cv.put(COL_USER_NAME, name);
        cv.put(COL_USER_EMAIL, email);
        cv.put(COL_USER_PASSWORD, password); // for assignment; consider hashing in production
        cv.put(COL_USER_ACTIVE, 1);
        long res = db.insertWithOnConflict(TABLE_USERS, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
        return res != -1;
    }

    public User getUserObjectByNIC(String nic) {
        Cursor c = getReadableDatabase().query(TABLE_USERS, null, COL_USER_NIC + "=?",
                new String[]{nic}, null, null, null);
        if (c != null && c.moveToFirst()) {
            User user = new User(
                    c.getString(c.getColumnIndexOrThrow(COL_USER_NIC)),
                    c.getString(c.getColumnIndexOrThrow(COL_USER_NAME)),
                    c.getString(c.getColumnIndexOrThrow(COL_USER_EMAIL)),
                    c.getString(c.getColumnIndexOrThrow(COL_USER_PASSWORD)),
                    c.getInt(c.getColumnIndexOrThrow(COL_USER_ACTIVE)) == 1
            );
            c.close();
            return user;
        }
        return null;
    }

    public boolean validateUserObject(String nic, String password) {
        User user = getUserObjectByNIC(nic);
        return user != null && user.isActive() && user.getPassword().equals(password);
    }

    public boolean userExists(String nic) {
        User user = getUserObjectByNIC(nic);
        return user != null;
    }

    public int setUserActive(String nic, boolean active) {
        ContentValues cv = new ContentValues();
        cv.put(COL_USER_ACTIVE, active ? 1 : 0);
        return getWritableDatabase().update(TABLE_USERS, cv, COL_USER_NIC + "=?", new String[]{nic});
    }

    public int updateUser(String nic, String name, String email, String password) {
        ContentValues cv = new ContentValues();
        if (name != null) cv.put(COL_USER_NAME, name);
        if (email != null) cv.put(COL_USER_EMAIL, email);
        if (password != null) cv.put(COL_USER_PASSWORD, password);
        return getWritableDatabase().update(TABLE_USERS, cv, COL_USER_NIC + "=?", new String[]{nic});
    }

    public int deleteUser(String nic) {
        return getWritableDatabase().delete(TABLE_USERS, COL_USER_NIC + "=?", new String[]{nic});
    }

    /* ---------- Reservations CRUD ---------- */

    public boolean addOrUpdateReservation(String resId, String nic, String stationId, String dateTime, String status) {
        ContentValues cv = new ContentValues();
        cv.put(COL_RES_ID, resId);
        cv.put(COL_RES_NIC, nic);
        cv.put(COL_RES_STATION, stationId);
        cv.put(COL_RES_DATETIME, dateTime);
        cv.put(COL_RES_STATUS, status);
        long res = getWritableDatabase().insertWithOnConflict(TABLE_RES, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        return res != -1;
    }

    public List<Reservation> getReservationsListByNIC(String nic) {
        List<Reservation> list = new ArrayList<>();
        Cursor c = getReadableDatabase().query(TABLE_RES, null, COL_RES_NIC + "=?",
                new String[]{nic}, null, null, COL_RES_DATETIME + " DESC");
        if (c != null) {
            while (c.moveToNext()) {
                list.add(new Reservation(
                        c.getString(c.getColumnIndexOrThrow(COL_RES_ID)),
                        c.getString(c.getColumnIndexOrThrow(COL_RES_NIC)),
                        c.getString(c.getColumnIndexOrThrow(COL_RES_STATION)),
                        c.getString(c.getColumnIndexOrThrow(COL_RES_DATETIME)),
                        c.getString(c.getColumnIndexOrThrow(COL_RES_STATUS))
                ));
            }
            c.close();
        }
        return list;
    }

    public int deleteReservation(String reservationId) {
        return getWritableDatabase().delete(TABLE_RES, COL_RES_ID + "=?", new String[]{reservationId});
    }
}
