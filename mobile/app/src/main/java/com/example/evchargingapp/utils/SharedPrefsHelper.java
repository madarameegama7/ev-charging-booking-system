package com.example.evchargingapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefsHelper {

    private SharedPreferences sharedPreferences;

    public SharedPrefsHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveToken(String token) {
        sharedPreferences.edit().putString(Constants.PREFS_KEY_JWT, token).apply();
    }

    public String getToken() {
        return sharedPreferences.getString(Constants.PREFS_KEY_JWT, null);
    }

    public void saveNic(String nic) {
        sharedPreferences.edit().putString(Constants.PREFS_KEY_NIC, nic).apply();
    }

    public String getNic() {
        return sharedPreferences.getString(Constants.PREFS_KEY_NIC, null);
    }

    public void clear() {
        sharedPreferences.edit().clear().apply();
    }
}
