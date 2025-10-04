package com.example.evchargingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.evchargingapp.activities.LoginActivity;
import com.example.evchargingapp.activities.DashboardActivity;
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
            Intent intent;
            if (token != null && !token.isEmpty()) {
                // User already logged in, go to Dashboard
                intent = new Intent(MainActivity.this, DashboardActivity.class);
            } else {
                // Not logged in, go to Login screen
                intent = new Intent(MainActivity.this, LoginActivity.class);
            }
            startActivity(intent);
            finish(); // close splash so user can't go back to it
        }, SPLASH_DURATION);
    }
}
