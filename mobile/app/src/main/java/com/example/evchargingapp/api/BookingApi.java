package com.example.evchargingapp.api;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.evchargingapp.utils.SharedPrefsHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.Future;

public class BookingApi {

    private static Context context;

    public BookingApi(Context ctx) {
        context = ctx;
    }

    /**
     * Create a new booking by sending JSON payload to backend.
     */
    public static JSONObject createBooking(JSONObject json, String token) throws Exception {
        // ApiClient.post returns Future<String>
        Future<String> futureResponse = ApiClient.post("booking", json.toString(), token);
        String response = futureResponse.get(); // unwrap Future
        Log.d("BookingApi", "Response from backend: " + response);
        
        // Handle empty response
        if (response == null || response.trim().isEmpty()) {
            Log.e("BookingApi", "Empty response from server");
            throw new Exception("Server returned empty response");
        }
        
        try {
            return new JSONObject(response);
        } catch (Exception e) {
            Log.e("BookingApi", "Failed to parse JSON response: " + response);
            throw new Exception("Invalid response from server: " + response);
        }
    }

    /**
     * Fetch all bookings for a given owner NIC.
     */
    public static JSONArray getBookingsByOwner(String nic, String token) throws Exception {
        Log.d("BookingApi", "Fetching bookings for NIC=" + nic + " | Token=" + token);
        Future<String> futureResponse = ApiClient.get("booking/owner/" + nic, token);
        String response = futureResponse.get(); // unwrap Future
        Log.d("BookingApi", "Response: " + response);
        
        if (response == null || response.trim().isEmpty()) {
            Log.e("BookingApi", "Empty response from server for getBookingsByOwner");
            throw new Exception("Server returned empty response");
        }
        
        try {
            return new JSONArray(response);
        } catch (Exception e) {
            Log.e("BookingApi", "Failed to parse JSON array response: " + response);
            throw new Exception("Invalid response from server: " + response);
        }
    }
    /**
     * Fetch all bookings for a given station ID (for operators).
     */
    public static JSONArray getBookingsByStation(String stationId, String token) throws Exception {
        Log.d("BookingApi", "Fetching bookings for StationId=" + stationId + " | Token=" + token);
        Future<String> futureResponse = ApiClient.get("booking/station/" + stationId, token);
        String response = futureResponse.get();
        Log.d("BookingApi", "Response: " + response);
        return new JSONArray(response);
    }

    /**
     * Update an existing booking with new details.
     */
    public static JSONObject updateBooking(String bookingId, JSONObject update, String token) throws Exception {
        String response = ApiClient.put("booking/" + bookingId, update.toString(), token);
        Log.d("BookingApi", "Updated booking response: " + response);
        return new JSONObject(response);
    }

    /**
     * Cancel a booking by updating its status to "Cancelled".
     */
    public static boolean cancelBooking(String bookingId) {
        try {
            String token = SharedPrefsHelper.getToken(context);
            JSONObject update = new JSONObject();
            update.put("status", "Cancelled");

            updateBooking(bookingId, update, token);
            return true;
        } catch (Exception e) {
            Log.e("BookingApi", "Failed to cancel booking", e);
            return false;
        }
    }
}