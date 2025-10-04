/*
 * File: BookingApi.java
 * Purpose: Provides API methods for managing EV charging bookings.
 *           Supports creating new bookings, retrieving bookings
 *           by owner, and updating booking details.
 */

package com.example.evchargingapp.api;

import org.json.JSONArray;
import org.json.JSONObject;

public class BookingApi {

    public static JSONObject createBooking(String stationId, String ownerNic,
                                           String start, String end, String token) throws Exception {
        JSONObject body = new JSONObject();
        body.put("stationId", stationId);
        body.put("ownerNic", ownerNic);
        body.put("startTimeUtc", start);
        body.put("endTimeUtc", end);

        String response = ApiClient.post("booking", body.toString(), token).get();
        return new JSONObject(response);
    }

    public static JSONArray getBookingsByOwner(String nic, String token) throws Exception {
        String response = ApiClient.get("booking/owner/" + nic, token).get();
        return new JSONArray(response);
    }

    public static JSONObject updateBooking(String id, JSONObject update, String token) throws Exception {
        String response = ApiClient.put("booking/" + id, update.toString(), token);
        return new JSONObject(response);
    }
}
