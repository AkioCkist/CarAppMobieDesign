package com.midterm.mobiledesignfinalterm.admin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.midterm.mobiledesignfinalterm.R;
import com.midterm.mobiledesignfinalterm.authentication.Login;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AdminDashboardActivity extends AppCompatActivity {

    // Admin information
    private String adminId;
    private String adminName;
    private String adminRole;
    private String adminData;

    // UI Components
    private TextView textViewAdminName;
    private TextView textViewAdminRole;
    private TextView textViewTotalUsers;
    private TextView textViewTotalCars;
    private TextView textViewTotalBookings;
    private TextView textViewActiveBookings;
    private CardView cardUsers;
    private CardView cardCars;
    private CardView cardBookings;
    private CardView cardReports;
    private ImageView imageViewLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Make status bar transparent and content edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.setAppearanceLightStatusBars(false);

        getAdminInfoFromIntent();
        initializeViews();
        setupClickListeners();
        loadAdminData();
        animateScreenEntry();
        
        // Load dashboard statistics
        loadDashboardStats();
    }

    private void getAdminInfoFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            adminId = intent.getStringExtra("admin_id");
            adminName = intent.getStringExtra("admin_name");
            adminRole = intent.getStringExtra("admin_role");
            adminData = intent.getStringExtra("admin_data");
        }
    }

    private void initializeViews() {
        textViewAdminName = findViewById(R.id.textViewAdminName);
        textViewAdminRole = findViewById(R.id.textViewAdminRole);
        textViewTotalUsers = findViewById(R.id.textViewTotalUsers);
        textViewTotalCars = findViewById(R.id.textViewTotalCars);
        textViewTotalBookings = findViewById(R.id.textViewTotalBookings);
        textViewActiveBookings = findViewById(R.id.textViewActiveBookings);
        
        cardUsers = findViewById(R.id.cardUsers);
        cardCars = findViewById(R.id.cardCars);
        cardBookings = findViewById(R.id.cardBookings);
        cardReports = findViewById(R.id.cardReports);
        
        imageViewLogout = findViewById(R.id.imageViewLogout);
    }

    private void setupClickListeners() {
        cardUsers.setOnClickListener(v -> animateCardClick(v, this::openUserManagement));
        cardCars.setOnClickListener(v -> animateCardClick(v, this::openCarManagement));
        cardBookings.setOnClickListener(v -> animateCardClick(v, this::openBookingManagement));
        cardReports.setOnClickListener(v -> animateCardClick(v, this::openReports));
        
        imageViewLogout.setOnClickListener(v -> handleLogout());
    }

    private void loadAdminData() {
        if (adminName != null && !adminName.isEmpty()) {
            textViewAdminName.setText(adminName);
        } else {
            textViewAdminName.setText("Administrator");
        }
        
        if (adminRole != null && !adminRole.isEmpty()) {
            textViewAdminRole.setText(adminRole);
        } else {
            textViewAdminRole.setText("System Admin");
        }
    }

    private void animateScreenEntry() {
        // Hide views initially to prepare for animation
        View[] views = {textViewAdminName, textViewAdminRole, cardUsers, cardCars, cardBookings, cardReports};
        
        for (View view : views) {
            view.setVisibility(View.INVISIBLE);
        }

        // Animate views with staggered delay
        for (int i = 0; i < views.length; i++) {
            final View view = views[i];
            new Handler().postDelayed(() -> {
                Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_left);
                view.setVisibility(View.VISIBLE);
                view.startAnimation(slideIn);
            }, i * 100);
        }
    }

    private void animateCardClick(View card, Runnable action) {
        card.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction(() -> {
                    card.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(100)
                            .withEndAction(action)
                            .start();
                })
                .start();
    }

    private void loadDashboardStats() {
        // Set placeholder statistics
        textViewTotalUsers.setText("Loading...");
        textViewTotalCars.setText("Loading...");
        textViewTotalBookings.setText("Loading...");
        textViewActiveBookings.setText("Loading...");
        
        // Load stats from API
        loadStatsFromAPI();
    }

    private void loadStatsFromAPI() {
        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2/myapi/dashboard_stats.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                JSONObject result = new JSONObject(response.toString());

                runOnUiThread(() -> {
                    try {
                        if (result.getBoolean("success")) {
                            JSONObject stats = result.getJSONObject("stats");
                            
                            textViewTotalUsers.setText(String.valueOf(stats.optInt("total_users", 0)));
                            textViewTotalCars.setText(String.valueOf(stats.optInt("total_cars", 0)));
                            textViewTotalBookings.setText(String.valueOf(stats.optInt("total_bookings", 0)));
                            textViewActiveBookings.setText(String.valueOf(stats.optInt("active_bookings", 0)));
                        } else {
                            // Set default values if API fails
                            setDefaultStats();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        setDefaultStats();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(this::setDefaultStats);
            }
        }).start();
    }

    private void setDefaultStats() {
        // Set demo statistics
        textViewTotalUsers.setText("25");
        textViewTotalCars.setText("12");
        textViewTotalBookings.setText("48");
        textViewActiveBookings.setText("8");
    }

    private void openUserManagement() {
        Toast.makeText(this, "User Management - Coming Soon", Toast.LENGTH_SHORT).show();
        
        // TODO: Implement user management screen
        // Intent intent = new Intent(AdminDashboardActivity.this, AdminUserManagementActivity.class);
        // startActivity(intent);
    }

    private void openCarManagement() {
        Toast.makeText(this, "Car Management - Coming Soon", Toast.LENGTH_SHORT).show();
        
        // TODO: Implement car management screen
        // Intent intent = new Intent(AdminDashboardActivity.this, AdminCarManagementActivity.class);
        // startActivity(intent);
    }

    private void openBookingManagement() {
        Toast.makeText(this, "Booking Management - Coming Soon", Toast.LENGTH_SHORT).show();
        
        // TODO: Implement booking management screen
        // Intent intent = new Intent(AdminDashboardActivity.this, AdminBookingManagementActivity.class);
        // startActivity(intent);
    }

    private void openReports() {
        Toast.makeText(this, "Reports - Coming Soon", Toast.LENGTH_SHORT).show();
        
        // TODO: Implement reports screen
        // Intent intent = new Intent(AdminDashboardActivity.this, AdminReportsActivity.class);
        // startActivity(intent);
    }

    private void handleLogout() {
        // Clear admin session and navigate back to login
        Intent intent = new Intent(AdminDashboardActivity.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Override back press to prevent going back to login without proper logout
        // Show a confirmation dialog or handle logout
        handleLogout();
    }
}
