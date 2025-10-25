/*
 * File: StationApi.java
 * Purpose: Handles API requests related to charging stations.
 *           Fetches all available stations and retrieves details
 *           for a specific station by ID.
 */

package com.example.evchargingapp.api;

import org.json.JSONArray;
import org.json.JSONObject;

public class StationApi {

    public static JSONArray getAllStations(String token) throws Exception {
        String response = ApiClient.get("station", token).get();
        Object json = new org.json.JSONTokener(response).nextValue();

        if (json instanceof JSONObject) {
            JSONObject obj = (JSONObject) json;
            // If backend wraps the list in "data"
            return obj.has("data") ? obj.getJSONArray("data") : new JSONArray();
        } else if (json instanceof JSONArray) {
            return (JSONArray) json;
        } else {
            return new JSONArray();
        }
    }

public static JSONObject getStationById(String id, String token) throws Exception {
    String response = ApiClient.get("station/" + id, token).get();
    Object json = new org.json.JSONTokener(response).nextValue();

    if (json instanceof JSONObject) {
        JSONObject obj = (JSONObject) json;
        // If backend wraps the station data in "data"
        return obj.has("data") ? obj.getJSONObject("data") : obj;
    } else {
        return new JSONObject();
    }
}
}
