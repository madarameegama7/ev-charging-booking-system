/*
 * File: Constants.java
 * Purpose: Defines global constant values for API endpoints,
 *           SharedPreferences keys, and configuration values
 *           used throughout the EVChargingApp mobile module.
 */

package com.example.evchargingapp.utils;

public class Constants {
    // Base URL of backend service
//    public static final String BASE_URL = "http://10.0.2.2:5000/api/";
    public static final String BASE_URL = "http://10.142.50.49:5000/api/";

    // API endpoints
    public static final String LOGIN_ENDPOINT = BASE_URL + "auth/login";
    public static final String EVOwner_ENDPOINT = BASE_URL + "user";
    public static final String BOOKING_ENDPOINT = BASE_URL + "booking";
    public static final String STATION_ENDPOINT = BASE_URL + "station";

    // SharedPreferences keys
    public static final String PREFS_NAME = "EVChargingPrefs";
    public static final String PREFS_KEY_JWT = "jwt_token";
    public static final String PREFS_KEY_NIC = "owner_nic";
    public static final String PREFS_KEY_ROLE = "user_role";

    public static final String PREFS_KEY_STATION_ID = "station_id";


}
