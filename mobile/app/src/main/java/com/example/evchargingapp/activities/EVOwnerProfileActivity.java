package com.example.evchargingapp.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.evchargingapp.R;
import com.example.evchargingapp.api.ApiClient;
import com.example.evchargingapp.api.UserApi;
import com.example.evchargingapp.database.EVOwnerDAO;
import com.example.evchargingapp.models.EVOwner;
import com.example.evchargingapp.utils.SharedPrefsHelper;

import org.json.JSONObject;

import java.util.concurrent.Future;

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

        // Load and display user info (hybrid)
        loadUserInfoHybrid();

        btnUpdate.setOnClickListener(v -> updateProfile());
        btnDeactivate.setOnClickListener(v -> confirmDeactivation());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    v.clearFocus();

                    // Hide the keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    // -------------------- Hybrid Load --------------------
    private void loadUserInfoHybrid() {
        // 1️⃣ Load from SQLite first
        EVOwnerDAO dao = new EVOwnerDAO(this);
        dao.open();
        EVOwner owner = dao.getEVOwnerByNic(SharedPrefsHelper.getNic(this));
        dao.close();

        if (owner != null) {
            prefillFields(owner);
        } else {
            // Placeholder if no local data
            tvProfileName.setText("EV Owner");
            tvInitials.setText(getInitials("EV Owner"));
        }

        // 2️⃣ Fetch latest from server
        fetchFromServerAndUpdateSQLite();
    }

    private void prefillFields(EVOwner owner) {
        etName.setText(owner.getName());
        etEmail.setText(owner.getEmail());
        etPhone.setText(owner.getPhone());
        tvProfileName.setText(owner.getName());
        tvInitials.setText(getInitials(owner.getName()));
        tvProfileNic.setText("NIC: " + owner.getNic());
    }

    private void fetchFromServerAndUpdateSQLite() {
        new AsyncTask<Void, Void, EVOwner>() {
            @Override
            protected EVOwner doInBackground(Void... voids) {
                try {
                    String nic = SharedPrefsHelper.getNic(EVOwnerProfileActivity.this);
                    String token = SharedPrefsHelper.getToken(EVOwnerProfileActivity.this);
                    JSONObject json = UserApi.getByNic(nic, token);
                    EVOwner serverOwner = new EVOwner();
                    serverOwner.setNic(json.getString("nic"));
                    serverOwner.setName(json.getString("name"));
                    serverOwner.setEmail(json.getString("email"));
                    serverOwner.setPhone(json.getString("phone"));
                    serverOwner.setActive(json.getBoolean("isActive"));
                    return serverOwner;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(EVOwner serverOwner) {
                if (serverOwner != null) {
                    // Update UI
                    prefillFields(serverOwner);

                    // Update local SQLite
                    EVOwnerDAO dao = new EVOwnerDAO(EVOwnerProfileActivity.this);
                    dao.open();
                    dao.insertEVOwner(serverOwner); // Insert or replace
                    dao.close();
                }
            }
        }.execute();
    }

    // -------------------- Utilities --------------------
    private String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) return "EV";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }

    // -------------------- Update Profile --------------------
    private void updateProfile() {
        String nic = SharedPrefsHelper.getNic(this);
        String token = SharedPrefsHelper.getToken(this);

        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Invalid email format");
            etEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.PHONE.matcher(phone).matches()) {
            etPhone.setError("Invalid phone number");
            etPhone.requestFocus();
            return;
        }

        btnUpdate.setEnabled(false);
        btnUpdate.setText("Updating...");

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    JSONObject json = new JSONObject();
                    json.put("name", name);
                    json.put("email", email);
                    json.put("phone", phone);

                    String response = ApiClient.put("user/profile/" + nic, json.toString(), token);
                    System.out.println("Update profile response: " + response);
                    JSONObject res = new JSONObject(response);
                    boolean success = res.optString("message").equalsIgnoreCase("Profile updated successfully");

                    if (success) {
                        // Update local SQLite
                        EVOwnerDAO dao = new EVOwnerDAO(EVOwnerProfileActivity.this);
                        dao.open();
                        EVOwner updatedOwner = new EVOwner(nic, name, phone, email, true);
                        dao.insertEVOwner(updatedOwner);
                        dao.close();
                    }

                    return success;

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
                    Toast.makeText(EVOwnerProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    tvProfileName.setText(name);
                    tvInitials.setText(getInitials(name));
                    etPassword.setText("");

                    // Update password if a new one is entered
                    if (!password.isEmpty()) {
                        updatePassword(nic, token, password);
                    }

                    // Navigate back to EVOwnerDashboardActivity after success
                    Intent intent = new Intent(EVOwnerProfileActivity.this, EVOwnerDashboardActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish(); // close profile activity

                } else {
                    Toast.makeText(EVOwnerProfileActivity.this, "Update failed! Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

        }.execute();
    }

    private void updatePassword(String nic, String token, String newPassword) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    JSONObject json = new JSONObject();
                    json.put("NewPassword", newPassword);

                    Future<String> future = ApiClient.post("user/" + nic + "/password", json.toString(), token);
                    String response = future.get(); // safely call get() in background thread
                    return response.contains("Password updated successfully");
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    Toast.makeText(EVOwnerProfileActivity.this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                    etPassword.setText(""); // clear password field
                } else {
                    Toast.makeText(EVOwnerProfileActivity.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    // -------------------- Deactivate Account --------------------
    private void confirmDeactivation() {
        new AlertDialog.Builder(this)
                .setTitle("⚠️ Deactivate Account")
                .setMessage("Are you sure you want to deactivate your account?\n\nThis will:\n• Disable your login access\n• Cancel all future bookings\n• Require back-office approval to reactivate\n\nThis action cannot be undone by yourself.")
                .setPositiveButton("Yes, Deactivate", (dialog, which) -> {
                    String nic = SharedPrefsHelper.getNic(this);
                    String token = SharedPrefsHelper.getToken(this);

                    btnDeactivate.setEnabled(false);
                    btnDeactivate.setText("Deactivating...");

                    new AsyncTask<Void, Void, Boolean>() {
                        @Override
                        protected Boolean doInBackground(Void... voids) {
                            try {
                                String response = ApiClient.put("user/profile/" + nic + "/deactivate", "{}", token);
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
                                SharedPrefsHelper.clear(EVOwnerProfileActivity.this);

                                Intent i = new Intent(EVOwnerProfileActivity.this, LoginActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(EVOwnerProfileActivity.this, "✗ Failed to deactivate account. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }.execute();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
