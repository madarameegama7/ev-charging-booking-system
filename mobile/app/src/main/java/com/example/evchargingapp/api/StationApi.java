package com.example.evchargingapp.api;

import org.json.JSONArray;
import org.json.JSONObject;

public class StationApi {

    public static JSONArray getAllStations() throws Exception {
        String response = ApiClient.get("station", null).get(); // no token needed
        return new JSONArray(response);
    }

    public static JSONObject getStationById(String id) throws Exception {
        String response = ApiClient.get("station/" + id, null).get();
        return new JSONObject(response);
    }
}
