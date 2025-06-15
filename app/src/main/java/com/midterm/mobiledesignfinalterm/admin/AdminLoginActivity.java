package com.midterm.mobiledesignfinalterm.admin;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.midterm.mobiledesignfinalterm.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AdminLoginActivity extends AppCompatActivity {

    private EditText editTextAdminId;
    private EditText editTextPassword;
    private ImageButton buttonTogglePassword;
    private Button buttonAdminSignIn;
    private ImageView imageViewBack;

    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        // Make status bar transparent and content edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.setAppearanceLightStatusBars(false);

        initializeViews();
        setupClickListeners();
        setupPasswordToggle();
        animateInitialEntrance();
    }

    private void initializeViews() {
        editTextAdminId = findViewById(R.id.editTextAdminId);
        editTextPassword = findViewById(R.id.editTextAdminPassword);
        buttonTogglePassword = findViewById(R.id.buttonToggleAdminPassword);
        buttonAdminSignIn = findViewById(R.id.buttonAdminSignIn);
        imageViewBack = findViewById(R.id.imageViewAdminBack);
    }

    private void setupClickListeners() {
        buttonAdminSignIn.setOnClickListener(v -> animateButtonClick(v, this::handleAdminSignIn));

        imageViewBack.setOnClickListener(v -> finish());
    }

    private void setupPasswordToggle() {
        buttonTogglePassword.setOnClickListener(v -> togglePasswordVisibility());
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide password
            editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            buttonTogglePassword.setImageResource(R.drawable.ic_eye_off);
            isPasswordVisible = false;
        } else {
            // Show password
            editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            buttonTogglePassword.setImageResource(R.drawable.ic_eye_on);
            isPasswordVisible = true;
        }

        // Move cursor to end of text
        editTextPassword.setSelection(editTextPassword.getText().length());
        animatePasswordToggle(buttonTogglePassword);
    }

    private void animatePasswordToggle(View view) {
        ObjectAnimator rotation = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.2f, 1f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(rotation, scaleX, scaleY);
        animatorSet.setDuration(300);
        animatorSet.setInterpolator(new OvershootInterpolator(1.1f));
        animatorSet.start();
    }

    private void animateInitialEntrance() {
        View[] views = {editTextAdminId, editTextPassword, buttonTogglePassword, buttonAdminSignIn};

        for (int i = 0; i < views.length; i++) {
            View view = views[i];
            view.setAlpha(0f);
            view.setTranslationY(50f);
            view.setScaleX(0.8f);
            view.setScaleY(0.8f);

            view.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(600)
                    .setStartDelay(i * 100)
                    .setInterpolator(new OvershootInterpolator(1.2f))
                    .start();
        }
    }

    private void animateButtonClick(View button, Runnable onComplete) {
        button.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(70)
                .withEndAction(() -> {
                    button.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(100)
                            .withEndAction(onComplete)
                            .start();
                })
                .start();
    }

    private void animateErrorShake(View view) {
        ObjectAnimator shake = ObjectAnimator.ofFloat(view, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f);
        shake.setDuration(600);
        shake.start();
    }

    private void handleAdminSignIn() {
        String adminId = editTextAdminId.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Demo admin credentials for testing
        if (adminId.equals("admin") && password.equals("admin123")) {
            Intent intent = new Intent(AdminLoginActivity.this, AdminDashboardActivity.class);
            intent.putExtra("admin_id", adminId);
            intent.putExtra("admin_name", "System Administrator");
            startActivity(intent);
            finish();
            return;
        }

        if (adminId.isEmpty()) {
            editTextAdminId.setError("Admin ID is required");
            editTextAdminId.requestFocus();
            animateErrorShake(editTextAdminId);
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            animateErrorShake(editTextPassword);
            return;
        }

        // Authenticate with server
        authenticateAdminWithAPI(adminId, password);
    }

    private void authenticateAdminWithAPI(String adminId, String password) {
        buttonAdminSignIn.setEnabled(false);
        
        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2/myapi/admin_login.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);

                // Prepare JSON payload
                JSONObject jsonInput = new JSONObject();
                jsonInput.put("admin_id", adminId);
                jsonInput.put("password", password);
                String jsonInputString = jsonInput.toString();

                // Send data
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                // Read response
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                // Parse JSON result
                JSONObject result = new JSONObject(response.toString());

                runOnUiThread(() -> {
                    buttonAdminSignIn.setEnabled(true);
                    try {
                        if (result.getBoolean("success")) {
                            JSONObject adminObject = result.getJSONObject("admin");
                            String adminName = adminObject.optString("name", "Administrator");
                            String role = adminObject.optString("role", "Admin");

                            Intent intent = new Intent(AdminLoginActivity.this, AdminDashboardActivity.class);
                            intent.putExtra("admin_id", adminId);
                            intent.putExtra("admin_name", adminName);
                            intent.putExtra("admin_role", role);
                            intent.putExtra("admin_data", adminObject.toString());
                            startActivity(intent);
                            finish();

                        } else {
                            String errorMessage = result.optString("error", "Invalid admin credentials");
                            Toast.makeText(AdminLoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(AdminLoginActivity.this, "Invalid server response", Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    buttonAdminSignIn.setEnabled(true);
                    Toast.makeText(AdminLoginActivity.this, "Network error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }
}
