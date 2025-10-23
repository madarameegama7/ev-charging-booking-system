/*
 * File: EVOwnerDatabaseHelper.java
 * Purpose: SQLite Database helper for EV Owner data caching
 */

package com.example.evchargingapp.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class EVOwnerDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "evchargingapp.db";
    public static final int DATABASE_VERSION = 2;

    public static final String TABLE_EVOwner = "EVOwner";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NIC = "nic";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_IS_ACTIVE = "isActive";

    private static final String CREATE_TABLE
            = "CREATE TABLE " + TABLE_EVOwner + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_NIC + " TEXT UNIQUE, "
            + COLUMN_NAME + " TEXT, "
            + COLUMN_PHONE + " TEXT, "
            + COLUMN_EMAIL + " TEXT, "
            + COLUMN_IS_ACTIVE + " INTEGER"
            + ");";

    public EVOwnerDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVOwner);
        onCreate(db);
    }
}
