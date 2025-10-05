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

    public static JSONArray getAllStations() throws Exception {
        String response = ApiClient.get("station", null).get();
        return new JSONArray(response);
    }

    public static JSONObject getStationById(String id) throws Exception {
        String response = ApiClient.get("station/" + id, null).get();
        return new JSONObject(response);
    }
}
