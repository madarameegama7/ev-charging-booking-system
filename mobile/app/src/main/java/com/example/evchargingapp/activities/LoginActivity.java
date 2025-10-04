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

    private EditText etNic;
    private Spinner spRole;
    private Button btnLogin;
    private ProgressBar progressBar;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etNic = findViewById(R.id.etNic);
        spRole = findViewById(R.id.spRole);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);

        setupRoleSpinner();

        btnLogin.setOnClickListener(v -> attemptLogin());
    }

    /** Populate the role spinner */
    private void setupRoleSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Owner", "Operator"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRole.setAdapter(adapter);
    }

    /** Trigger login process */
    private void attemptLogin() {
        final String nic = etNic.getText().toString().trim();
        final String role = spRole.getSelectedItem().toString();

        if (nic.isEmpty()) {
            etNic.setError("NIC required");
            etNic.requestFocus();
            return;
        }

        toggleLoading(true);

        executor.execute(() -> {
            try {
                JSONObject resp = AuthApi.login(nic, role);

                String token = resp.optString("token", null);
                String returnedRole = resp.optString("role", role);

                if (token == null || token.isEmpty()) {
                    throw new Exception("Invalid login credentials");
                }

                // Save user data
                SharedPrefsHelper.saveToken(getApplicationContext(), token);
                SharedPrefsHelper.saveNic(getApplicationContext(), nic);
                SharedPrefsHelper.saveRole(getApplicationContext(), returnedRole);

                // Navigate to dashboard on UI thread
                runOnUiThread(() -> {
                    toggleLoading(false);
                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                });

            } catch (final Exception ex) {
                ex.printStackTrace();
                runOnUiThread(() -> {
                    toggleLoading(false);
                    Toast.makeText(LoginActivity.this,
                            "Login failed: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /** Show/hide loading UI */
    private void toggleLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!isLoading);
    }
}
