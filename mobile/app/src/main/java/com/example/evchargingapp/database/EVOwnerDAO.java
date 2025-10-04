/*
 * File: EVOwnerDAO.java
 * Purpose: Handles CRUD operations for EVOwner table
 */

package com.example.evchargingapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.evchargingapp.models.EVOwner;

public class EVOwnerDAO {

    private SQLiteDatabase database;
    private EVOwnerDatabaseHelper dbHelper;

    public EVOwnerDAO(Context context) {
        dbHelper = new EVOwnerDatabaseHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertEVOwner(EVOwner owner) {
        ContentValues values = new ContentValues();
        values.put(EVOwnerDatabaseHelper.COLUMN_NIC, owner.getNic());
        values.put(EVOwnerDatabaseHelper.COLUMN_NAME, owner.getName());
        values.put(EVOwnerDatabaseHelper.COLUMN_PHONE, owner.getPhone());
        values.put(EVOwnerDatabaseHelper.COLUMN_EMAIL, owner.getEmail());
        values.put(EVOwnerDatabaseHelper.COLUMN_IS_ACTIVE, owner.isActive() ? 1 : 0);

        return database.insertWithOnConflict(
                EVOwnerDatabaseHelper.TABLE_EVOwner,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
        );
    }

    public EVOwner getEVOwnerByNic(String nic) {
        EVOwner owner = null;

        Cursor cursor = database.query(
                EVOwnerDatabaseHelper.TABLE_EVOwner,
                null,
                EVOwnerDatabaseHelper.COLUMN_NIC + "=?",
                new String[]{nic},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            owner = new EVOwner(
                    cursor.getString(cursor.getColumnIndexOrThrow(EVOwnerDatabaseHelper.COLUMN_NIC)),
                    cursor.getString(cursor.getColumnIndexOrThrow(EVOwnerDatabaseHelper.COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(EVOwnerDatabaseHelper.COLUMN_PHONE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(EVOwnerDatabaseHelper.COLUMN_EMAIL)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(EVOwnerDatabaseHelper.COLUMN_IS_ACTIVE)) == 1
            );
            cursor.close();
        }

        return owner;
    }

    public void deleteAllOwners() {
        database.delete(EVOwnerDatabaseHelper.TABLE_EVOwner, null, null);
    }
}
