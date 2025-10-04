package com.example.evchargingapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.evchargingapp.models.EVOwner;

public class EVOwnerDAO {
    private EVOwnerDatabaseHelper dbHelper;

    public EVOwnerDAO(Context context) {
        dbHelper = new EVOwnerDatabaseHelper(context);
    }

    public long insertEVOwner(EVOwner owner) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EVOwnerDatabaseHelper.COLUMN_NIC, owner.getNic());
        values.put(EVOwnerDatabaseHelper.COLUMN_NAME, owner.getName());
        values.put(EVOwnerDatabaseHelper.COLUMN_PHONE, owner.getPhone());
        values.put(EVOwnerDatabaseHelper.COLUMN_EMAIL, owner.getEmail());
        values.put(EVOwnerDatabaseHelper.COLUMN_IS_ACTIVE, owner.isActive() ? 1 : 0);

        long id = db.insert(EVOwnerDatabaseHelper.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public EVOwner getEVOwnerByNic(String nic) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                EVOwnerDatabaseHelper.TABLE_NAME,
                null,
                EVOwnerDatabaseHelper.COLUMN_NIC + "=?",
                new String[]{nic},
                null,
                null,
                null
        );

        EVOwner owner = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                owner = new EVOwner(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4) == 1
                );
            }
            cursor.close();
        }
        db.close();
        return owner;
    }

    public void deleteAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(EVOwnerDatabaseHelper.TABLE_NAME, null, null);
        db.close();
    }
}
