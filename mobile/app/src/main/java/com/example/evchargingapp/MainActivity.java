package com.example.evchargingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

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

        // Start rotation animation
        ImageView imgLogo = findViewById(R.id.imgLogo);
        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.rotate_logo);
        imgLogo.startAnimation(rotate);

        // Delay then navigate
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            String token = SharedPrefsHelper.getToken(getApplicationContext());
            String role = SharedPrefsHelper.getRole(getApplicationContext());

            Intent intent;
            if (token != null && !token.isEmpty()) {
                if ("Owner".equalsIgnoreCase(role)) {
                    intent = new Intent(MainActivity.this, EVOwnerDashboardActivity.class);
                } else if ("Operator".equalsIgnoreCase(role)) {
                    intent = new Intent(MainActivity.this, EVOperatorDashboardActivity.class);
                } else {
                    intent = new Intent(MainActivity.this, LoginActivity.class);
                }
            } else {
                intent = new Intent(MainActivity.this, LoginActivity.class);
            }

            startActivity(intent);
            finish();
        }, SPLASH_DURATION);
    }
}
