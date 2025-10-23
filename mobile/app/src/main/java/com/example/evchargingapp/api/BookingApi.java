/*
 * File: BookingApi.java
 * Purpose: Provides API methods for managing EV charging bookings.
 *           Supports creating new bookings, retrieving bookings
 *           by owner, and updating booking details.
 */

package com.example.evchargingapp.api;

import android.content.Context;
import android.util.Log;

import com.example.evchargingapp.utils.SharedPrefsHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BookingApi {
    private static Context context;

    public BookingApi(Context ctx) {
        context = ctx;
    }

    public static JSONObject createBooking(String stationId, String ownerNic, String startUtc, String endUtc, String token) throws Exception {
        JSONObject body = new JSONObject();
        body.put("stationId", stationId);
        body.put("ownerNic", ownerNic);
        body.put("start", startUtc);
        body.put("end", endUtc);

        String response = ApiClient.post("booking", body.toString(), token).get();
        try {
            return new JSONObject(response); // try parse JSON
        } catch (JSONException e) {
            Log.e("BookingApi", "Server returned non-JSON: " + response);
            throw new Exception("Server error: " + response);
        }
    }

    public static JSONArray getBookingsByOwner(String nic, String token) throws Exception {
        String response = ApiClient.get("booking/owner/" + nic, token).get();
        return new JSONArray(response);
    }

    public static JSONArray getBookingsByStation(String stationId, String token) throws Exception {
        // method operator to fetch bookings for their station
        String response = ApiClient.get("booking/station/" + stationId, token).get();
        return new JSONArray(response);
    }

    public static JSONObject updateBooking(String id, JSONObject update, String token) throws Exception {
        String response = ApiClient.put("booking/" + id, update.toString(), token);
        return new JSONObject(response);
    }

    public static boolean cancelBooking(String bookingId) {
        try {
            String token = SharedPrefsHelper.getToken(context);
            JSONObject update = new JSONObject();
            update.put("status", "Cancelled");
            updateBooking(bookingId, update, token);
            return true;
        } catch (Exception e) { return false; }
    }

}
