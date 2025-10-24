package com.example.evchargingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.LayoutInflater;
import androidx.appcompat.app.AppCompatActivity;

import com.example.evchargingapp.R;
import com.example.evchargingapp.utils.SharedPrefsHelper;

public class EVOperatorDashboardActivity extends AppCompatActivity {

    private TextView tvWelcome, tvPendingCount, tvActiveCount, tvCompletedCount;
    private TextView tvSessionCount, tvNoSessions;
    private Button btnScanQR, btnLogout;
    private LinearLayout cardViewReservations, cardStationStatus;
    private LinearLayout containerSessions, containerTransactions;
    private TextView tvViewAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evoperator_dashboard);

        initializeViews();
        loadOperatorData();
        setupClickListeners();
        loadActiveSessions();
        loadTransactionHistory();
    }

    private void initializeViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        tvPendingCount = findViewById(R.id.tvPendingCount);
        tvActiveCount = findViewById(R.id.tvActiveCount);
        tvCompletedCount = findViewById(R.id.tvCompletedCount);
        tvSessionCount = findViewById(R.id.tvSessionCount);
        tvNoSessions = findViewById(R.id.tvNoSessions);

        btnScanQR = findViewById(R.id.btnScanQR);
        btnLogout = findViewById(R.id.btnLogout);

        cardViewReservations = findViewById(R.id.cardViewReservations);
        cardStationStatus = findViewById(R.id.cardStationStatus);

        containerSessions = findViewById(R.id.containerSessions);
        containerTransactions = findViewById(R.id.containerTransactions);
        tvViewAll = findViewById(R.id.tvViewAll);
    }

    private void loadOperatorData() {
        String nic = SharedPrefsHelper.getNic(this);
//        tvWelcome.setText("Operator " + nic.substring(0, Math.min(5, nic.length())));
        tvWelcome.setText("Janudi");

        // TODO: Load these values from the server or database
        tvPendingCount.setText("12");
        tvActiveCount.setText("5");
        tvCompletedCount.setText("8");
    }

    private void setupClickListeners() {
        btnScanQR.setOnClickListener(v -> {
            // Navigate to QR Scanner Activity
            Intent intent = new Intent(EVOperatorDashboardActivity.this, QRScannerActivity.class);
            startActivity(intent);
        });

        cardViewReservations.setOnClickListener(v -> {
            // Navigate to Reservations Activity
            Intent intent = new Intent(EVOperatorDashboardActivity.this, EVOperatorReservationActivity.class);
            startActivity(intent);
        });

        cardStationStatus.setOnClickListener(v -> {
            // Navigate to Station Status Activity
            Intent intent = new Intent(EVOperatorDashboardActivity.this, StationStatusActivity.class);
            startActivity(intent);
        });

        tvViewAll.setOnClickListener(v -> {
            // Navigate to full transaction history
            Intent intent = new Intent(EVOperatorDashboardActivity.this, TransactionHistoryActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> performLogout());
    }

    private void loadActiveSessions() {
        // TODO: Fetch active sessions from database or server
        // This is a sample implementation

        // Clear previous items
        containerSessions.removeAllViews();

        // Sample data - replace with actual data
        String[][] sessions = {
                {"EV-001", "Tesla Model 3", "45%", "12:30 PM"},
                {"EV-002", "BMW i4", "60%", "1:00 PM"},
                {"EV-003", "Nissan Leaf", "30%", "1:15 PM"}
        };

        if (sessions.length > 0) {
            tvNoSessions.setVisibility(android.view.View.GONE);
            tvSessionCount.setText(sessions.length + " sessions");

            for (String[] session : sessions) {
                addSessionItem(session[0], session[1], session[2], session[3]);
            }
        } else {
            tvNoSessions.setVisibility(android.view.View.VISIBLE);
        }
    }

    private void addSessionItem(String reservationId, String vehicleModel, String chargeLevel, String startTime) {
        LinearLayout sessionView = (LinearLayout) LayoutInflater.from(this)
                .inflate(R.layout.item_session, containerSessions, false);

        TextView tvReservationId = sessionView.findViewById(R.id.tvReservationId);
        TextView tvVehicleModel = sessionView.findViewById(R.id.tvVehicleModel);
        TextView tvChargeLevel = sessionView.findViewById(R.id.tvChargeLevel);
        TextView tvStartTime = sessionView.findViewById(R.id.tvStartTime);
        Button btnCompleteSession = sessionView.findViewById(R.id.btnCompleteSession);

        tvReservationId.setText(reservationId);
        tvVehicleModel.setText(vehicleModel);
        tvChargeLevel.setText(chargeLevel);
        tvStartTime.setText(startTime);

        btnCompleteSession.setOnClickListener(v -> {
            // Complete the session
            completeChargingSession(reservationId);
        });

        containerSessions.addView(sessionView);
    }

    private void loadTransactionHistory() {
        // TODO: Fetch transaction history from database or server

        containerTransactions.removeAllViews();

        // Sample data - replace with actual data
        String[][] transactions = {
                {"TXN-2024-001", "Rs.450.00", "Completed", "10:30 AM"},
                {"TXN-2024-002", "Rs.320.00", "Completed", "11:45 AM"}
        };

        for (String[] transaction : transactions) {
            addTransactionItem(transaction[0], transaction[1], transaction[2], transaction[3]);
        }
    }

    private void addTransactionItem(String transactionId, String amount, String status, String time) {
        LinearLayout transactionView = (LinearLayout) LayoutInflater.from(this)
                .inflate(R.layout.item_transaction, containerTransactions, false);

        TextView tvTxnId = transactionView.findViewById(R.id.tvTxnId);
        TextView tvAmount = transactionView.findViewById(R.id.tvAmount);
        TextView tvStatus = transactionView.findViewById(R.id.tvStatus);
        TextView tvTime = transactionView.findViewById(R.id.tvTime);

        tvTxnId.setText(transactionId);
        tvAmount.setText(amount);
        tvStatus.setText(status);
        tvTime.setText(time);

        containerTransactions.addView(transactionView);
    }

    private void completeChargingSession(String reservationId) {
        // TODO: Implement logic to finalize charging session
        // Update database, send to server, generate receipt, etc.

        // Show confirmation dialog or toast
        android.widget.Toast.makeText(this, "Session " + reservationId + " completed", android.widget.Toast.LENGTH_SHORT).show();

        // Reload active sessions
        loadActiveSessions();
    }

    private void performLogout() {
        SharedPrefsHelper.clear(this);
        Intent intent = new Intent(EVOperatorDashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to this activity
        loadOperatorData();
        loadActiveSessions();
        loadTransactionHistory();
    }
}