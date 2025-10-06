package com.example.evchargingapp.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.evchargingapp.R;
import com.example.evchargingapp.api.ApiClient;
import com.example.evchargingapp.utils.SharedPrefsHelper;

import org.json.JSONObject;

public class EVOwnerProfileActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPhone, etPassword;
    private Button btnUpdate, btnDeactivate;
    private TextView tvInitials, tvProfileName, tvProfileNic;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evowner_profile);

        // Initialize views
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDeactivate = findViewById(R.id.btnDeactivate);
        tvInitials = findViewById(R.id.tvInitials);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileNic = findViewById(R.id.tvProfileNic);
        btnBack = findViewById(R.id.btnBack);

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Load and display user info
        loadUserInfo();

        btnUpdate.setOnClickListener(v -> updateProfile());
        btnDeactivate.setOnClickListener(v -> confirmDeactivation());
    }

    private void loadUserInfo() {
        String nic = SharedPrefsHelper.getNic(this);

        // Display NIC
        tvProfileNic.setText("NIC: " + nic);

        // You can prefill from SharedPrefs or fetch from API
        // For now, showing placeholder
        String userName = "EV Owner"; // Get from SharedPrefs if stored
        tvProfileName.setText(userName);

        // Generate initials (first letters of first and last name)
        String initials = getInitials(userName);
        tvInitials.setText(initials);
    }

    private String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "EV";
        }

        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        } else {
            return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
        }
    }

    private void updateProfile() {
        String nic = SharedPrefsHelper.getNic(this);
        String token = SharedPrefsHelper.getToken(this);

        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Name, email, and phone are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Email validation
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Invalid email format");
            etEmail.requestFocus();
            return;
        }

        // Phone validation
        if (!android.util.Patterns.PHONE.matcher(phone).matches()) {
            etPhone.setError("Invalid phone number");
            etPhone.requestFocus();
            return;
        }

        // Disable button during update
        btnUpdate.setEnabled(false);
        btnUpdate.setText("Updating...");

        new UpdateProfileTask(nic, name, email, phone, password, token).execute();
    }

    private void confirmDeactivation() {
        new AlertDialog.Builder(this)
                .setTitle("⚠️ Deactivate Account")
                .setMessage("Are you sure you want to deactivate your account?\n\nThis will:\n• Disable your login access\n• Cancel all future bookings\n• Require back-office approval to reactivate\n\nThis action cannot be undone by yourself.")
                .setPositiveButton("Yes, Deactivate", (dialog, which) -> {
                    String nic = SharedPrefsHelper.getNic(this);
                    String token = SharedPrefsHelper.getToken(this);

                    // Disable button during deactivation
                    btnDeactivate.setEnabled(false);
                    btnDeactivate.setText("Deactivating...");

                    new DeactivateTask(nic, token).execute();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // AsyncTask for updating profile
    private class UpdateProfileTask extends AsyncTask<Void, Void, Boolean> {
        private final String nic, name, email, phone, password, token;

        UpdateProfileTask(String nic, String name, String email, String phone, String password, String token) {
            this.nic = nic;
            this.name = name;
            this.email = email;
            this.phone = phone;
            this.password = password;
            this.token = token;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                JSONObject json = new JSONObject();
                json.put("nic", nic);
                json.put("name", name);
                json.put("email", email);
                json.put("phone", phone);
                if (!password.isEmpty()) json.put("passwordHash", password);

                String response = ApiClient.put("api/user/profile/" + nic, json.toString(), token);
                return response.contains("Profile updated successfully");

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            btnUpdate.setEnabled(true);
            btnUpdate.setText("Update Profile");

            if (success) {
                Toast.makeText(EVOwnerProfileActivity.this, "✓ Profile updated successfully!", Toast.LENGTH_SHORT).show();

                // Update display
                String name = etName.getText().toString().trim();
                tvProfileName.setText(name);
                tvInitials.setText(getInitials(name));

                // Clear password field
                etPassword.setText("");
            } else {
                Toast.makeText(EVOwnerProfileActivity.this, "✗ Update failed! Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // AsyncTask for deactivating account
    private class DeactivateTask extends AsyncTask<Void, Void, Boolean> {
        private final String nic, token;

        DeactivateTask(String nic, String token) {
            this.nic = nic;
            this.token = token;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                String response = ApiClient.put("api/user/profile/" + nic + "/deactivate", "{}", token);
                return response.contains("Account deactivated successfully");

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            btnDeactivate.setEnabled(true);
            btnDeactivate.setText("Deactivate Account");

            if (success) {
                Toast.makeText(EVOwnerProfileActivity.this, "Account deactivated successfully.", Toast.LENGTH_LONG).show();

                // Clear all saved data
                SharedPrefsHelper.clear(EVOwnerProfileActivity.this);

                // Navigate to login and clear backstack
                Intent i = new Intent(EVOwnerProfileActivity.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(EVOwnerProfileActivity.this, "✗ Failed to deactivate account. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}