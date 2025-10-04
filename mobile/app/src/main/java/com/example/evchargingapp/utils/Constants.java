package com.example.evchargingapp.utils;

public class Constants {
    // Base URL of your backend service
    public static final String BASE_URL = "http://10.0.2.2:5000/api/";

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

}
