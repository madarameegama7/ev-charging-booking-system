package com.example.evchargingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.evchargingapp.activities.EVOperatorDashboardActivity;
import com.example.evchargingapp.activities.EVOwnerDashboardActivity;
import com.example.evchargingapp.activities.LoginActivity;
import com.example.evchargingapp.utils.SharedPrefsHelper;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Delay then navigate
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            String token = SharedPrefsHelper.getToken(getApplicationContext());
            String role = SharedPrefsHelper.getRole(getApplicationContext());

            Intent intent;
            if (token != null && !token.isEmpty()) {
                // User already logged in, check role
                if ("Owner".equalsIgnoreCase(role)) {
                    intent = new Intent(MainActivity.this, EVOwnerDashboardActivity.class);
                } else if ("Operator".equalsIgnoreCase(role)) {
                    intent = new Intent(MainActivity.this, EVOperatorDashboardActivity.class);
                } else {
                    // Unknown role, fallback to login
                    intent = new Intent(MainActivity.this, LoginActivity.class);
                }
            } else {
                // Not logged in, go to Login screen
                intent = new Intent(MainActivity.this, LoginActivity.class);
            }

            startActivity(intent);
            finish(); // close splash so user can't go back to it
        }, SPLASH_DURATION);
    }
}
