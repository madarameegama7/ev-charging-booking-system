/*
 * File: SharedPrefsHelper.java
 * Purpose: Provides helper methods for managing user session data
 *           such as JWT token, NIC, and role using Android SharedPreferences.
 *           Used across activities to maintain login state and authentication.
 */

package com.example.evchargingapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefsHelper {

    private static final String PREFS_NAME = Constants.PREFS_NAME;

    // Save JWT token
    public static void saveToken(Context context, String token) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(Constants.PREFS_KEY_JWT, token).apply();
    }

    public static String getToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(Constants.PREFS_KEY_JWT, null);
    }

    // ✅ Save User Name
    public static void saveName(Context context, String name) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(Constants.PREFS_KEY_NAME, name).apply();
    }

    // ✅ Get User Name
    public static String getName(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(Constants.PREFS_KEY_NAME, null);
    }

    // Save Owner NIC
    public static void saveNic(Context context, String nic) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(Constants.PREFS_KEY_NIC, nic).apply();
    }

    public static String getNic(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(Constants.PREFS_KEY_NIC, null);
    }

    // Save Role
    public static void saveRole(Context context, String role) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(Constants.PREFS_KEY_ROLE, role).apply();
    }

    public static String getRole(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(Constants.PREFS_KEY_ROLE, null);
    }

    // Clear all data (logout)
    public static void clear(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }

    // Save Station ID
    public static void saveStationId(Context context, String stationId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(Constants.PREFS_KEY_STATION_ID, stationId).apply();
    }

    // Get Station ID
    public static String getStationId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(Constants.PREFS_KEY_STATION_ID, null);
    }

}
