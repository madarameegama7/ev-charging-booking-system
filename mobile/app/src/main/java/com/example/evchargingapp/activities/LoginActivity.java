package com.example.evchargingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.evchargingapp.R;
import com.example.evchargingapp.api.AuthApi;
import com.example.evchargingapp.utils.SharedPrefsHelper;

import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private EditText etNic, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private ProgressBar progressBar;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etNic = findViewById(R.id.etNic);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
        tvRegister = findViewById(R.id.tvRegister);

        btnLogin.setOnClickListener(v -> attemptLogin());

        // Register link
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void attemptLogin() {
        final String nic = etNic.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();

        if (nic.isEmpty()) {
            etNic.setError("NIC required");
            etNic.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password required");
            etPassword.requestFocus();
            return;
        }

        toggleLoading(true);

        executor.execute(() -> {
            try {
                JSONObject resp = AuthApi.login(nic, password); // Updated to send password

                String token = resp.optString("token", null);
                String role = resp.optString("role", null);
                boolean isActive = resp.optBoolean("isActive", true);

                String firstName = resp.optString("firstName", "");
                String lastName = resp.optString("lastName", "");
                String fullName = (firstName + " " + lastName).trim();

                if (!isActive) {
                    throw new Exception("AccountDeactivated");
                }

                if (token == null || token.isEmpty()) {
                    throw new Exception("InvalidCredentials");
                }

                // Save user data
                SharedPrefsHelper.saveToken(getApplicationContext(), token);
                SharedPrefsHelper.saveNic(getApplicationContext(), nic);
                SharedPrefsHelper.saveRole(getApplicationContext(), role);
                SharedPrefsHelper.saveName(getApplicationContext(), fullName);

                // Navigate to dashboard based on role
                runOnUiThread(() -> {
                    toggleLoading(false);
                    Intent intent;
                    if ("Owner".equalsIgnoreCase(role)) {
                        intent = new Intent(LoginActivity.this, EVOwnerDashboardActivity.class);
                    } else if ("Operator".equalsIgnoreCase(role)) {
                        intent = new Intent(LoginActivity.this, EVOperatorDashboardActivity.class);
                    } else {
                        intent = new Intent(LoginActivity.this, LoginActivity.class);
                        Toast.makeText(LoginActivity.this, "Unknown role", Toast.LENGTH_SHORT).show();
                    }
                    startActivity(intent);
                    finish();
                });

            } catch (final Exception ex) {
                ex.printStackTrace();
                runOnUiThread(() -> {
                    toggleLoading(false);

                    String message;
                    if (ex.getMessage() != null && ex.getMessage().contains("AccountDeactivated")) {
                        message = "Your account has been deactivated. Please contact support.";
                    } else if (ex.getMessage() != null && ex.getMessage().contains("InvalidCredentials")) {
                        message = "Incorrect NIC or password. Please try again.";
                    } else {
                        message = "Unable to log in. Please check your details or try again later.";
                    }

                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                    etPassword.setText("");
                });
            }
        });
    }

    private void toggleLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!isLoading);
    }
}
