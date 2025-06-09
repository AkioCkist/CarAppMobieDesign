package com.midterm.mobiledesignfinalterm.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import com.midterm.mobiledesignfinalterm.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPassword extends AppCompatActivity {

    private EditText editTextAccountName;
    private EditText editTextMobileNumber;
    private Button buttonSend;
    private Button buttonResend;
    private ImageView imageViewBack;
    private CountDownTimer countDownTimer;
    private boolean isInfoSent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        View rootView = findViewById(android.R.id.content);
        playPopupAnimation(rootView);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Make status bar transparent and content edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.setAppearanceLightStatusBars(false);

        initializeViews();
        setupClickListeners();
    }

    private void animateButtonClick(View button, Runnable onComplete) {
        // Scale animation for button press feedback
        button.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(70)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        button.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .withEndAction(onComplete)
                                .start();
                    }
                })
                .start();
    }

    private void animateErrorShake(View view) {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        view.startAnimation(shake);
    }

    private void playPopupAnimation(View view) {
        Animation popupAnimation = AnimationUtils.loadAnimation(this, R.anim.popup_animation);
        view.startAnimation(popupAnimation);
    }

    private void initializeViews() {
        editTextAccountName = findViewById(R.id.editTextAccountName);
        editTextMobileNumber = findViewById(R.id.editTextMobileNumber);
        buttonSend = findViewById(R.id.buttonSend);
        buttonResend = findViewById(R.id.buttonResend);
        imageViewBack = findViewById(R.id.imageViewBack);
    }

    private void setupClickListeners() {
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(v, new Runnable() {
                    @Override
                    public void run() {
                        handleVerifyAccount();
                    }
                });
            }
        });

        buttonResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(v, new Runnable() {
                    @Override
                    public void run() {
                        handleResendVerification();
                    }
                });
            }
        });

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleBackPress();
            }
        });
    }

    private void handleVerifyAccount() {
        String accountName = editTextAccountName.getText().toString().trim();
        String mobileNumber = editTextMobileNumber.getText().toString().trim();

        // Validate account name
        if (accountName.isEmpty()) {
            editTextAccountName.setError("Account name is required");
            editTextAccountName.requestFocus();
            animateErrorShake(editTextAccountName);
            return;
        }

        // Validate mobile number
        if (mobileNumber.isEmpty()) {
            editTextMobileNumber.setError("Mobile number is required");
            editTextMobileNumber.requestFocus();
            animateErrorShake(editTextMobileNumber);
            return;
        }

        // Mobile number regex validation (10-13 digits, optional + prefix)
        String phoneRegex = "^[+]?[0-9]{10,13}$";
        if (!mobileNumber.matches(phoneRegex)) {
            editTextMobileNumber.setError("Please enter a valid mobile number");
            editTextMobileNumber.requestFocus();
            animateErrorShake(editTextMobileNumber);
            return;
        }

        // Send verification request to API
        verifyAccountWithAPI(accountName, mobileNumber);
    }

    private void handleResendVerification() {
        String accountName = editTextAccountName.getText().toString().trim();
        String mobileNumber = editTextMobileNumber.getText().toString().trim();

        // Resend verification
        Toast.makeText(this, "Verification resent to " + mobileNumber, Toast.LENGTH_LONG).show();

        // Restart countdown
        startCountdown();
    }

    private void verifyAccountWithAPI(String accountName, String mobileNumber) {
        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2/myapi/forgot_password.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                // Set connection properties
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(10000); // 10 seconds
                conn.setReadTimeout(10000); // 10 seconds

                // Create JSON payload using JSONObject instead of string concatenation
                JSONObject jsonInput = new JSONObject();
                try {
                    // Use the field names that match what your app is actually sending
                    jsonInput.put("username", accountName);
                    jsonInput.put("phone_number", mobileNumber);
                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        Toast.makeText(ForgotPassword.this, "Error creating request data", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                String jsonInputString = jsonInput.toString();

                // Debug: Print what we're sending
                System.out.println("Sending JSON: " + jsonInputString);

                // Send data
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                    os.flush();
                }

                // Check response code
                int responseCode = conn.getResponseCode();
                System.out.println("Response Code: " + responseCode);

                // Read response
                BufferedReader br;
                if (responseCode >= 200 && responseCode < 300) {
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                } else {
                    br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"));
                }

                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                br.close();

                // Debug: Print the full response
                System.out.println("Raw API Response: " + response.toString());

                // Parse JSON result
                try {
                    JSONObject result = new JSONObject(response.toString());

                    runOnUiThread(() -> {
                        try {
                            if (result.getBoolean("success")) {
                                // Account verification successful
                                Toast.makeText(ForgotPassword.this, "Account verified successfully!", Toast.LENGTH_SHORT).show();

                                // Show resend button and start countdown
                                isInfoSent = true;
                                buttonResend.setVisibility(View.VISIBLE);
                                startCountdown();

                                // Navigate to create new password screen
                                Intent intent = new Intent(ForgotPassword.this, CreateNewPassword.class);
                                intent.putExtra("account_name", accountName);
                                intent.putExtra("mobile_number", mobileNumber);

                                // If API returns user ID, pass it too
                                if (result.has("user_id")) {
                                    intent.putExtra("user_id", result.getString("user_id"));
                                }

                                startActivity(intent);

                            } else {
                                String errorMessage = result.optString("error", "Account name or mobile number not found");
                                Toast.makeText(ForgotPassword.this, errorMessage, Toast.LENGTH_LONG).show();

                                // Debug: Print additional error info if available
                                if (result.has("received_fields")) {
                                    System.out.println("Fields received by server: " + result.getString("received_fields"));
                                }
                                if (result.has("received_data")) {
                                    System.out.println("Data received by server: " + result.getString("received_data"));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            String errorMsg = "Invalid server response: " + e.getMessage();
                            Toast.makeText(ForgotPassword.this, errorMsg, Toast.LENGTH_LONG).show();
                            System.out.println("JSON Parsing Error: " + e.getMessage());
                            System.out.println("Raw response that caused error: " + response.toString());
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        Toast.makeText(ForgotPassword.this, "Server returned invalid response", Toast.LENGTH_LONG).show();
                        System.out.println("JSON Parse Error: " + e.getMessage());
                        System.out.println("Raw response: " + response.toString());
                    });
                }

            } catch (java.net.ConnectException e) {
                System.out.println("Connection refused: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(ForgotPassword.this, "Cannot connect to server. Please check if your local server is running.", Toast.LENGTH_LONG).show();
                });
            } catch (java.net.UnknownHostException e) {
                System.out.println("Unknown host: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(ForgotPassword.this, "Cannot resolve server address. Check your network connection.", Toast.LENGTH_LONG).show();
                });
            } catch (java.net.SocketTimeoutException e) {
                System.out.println("Connection timeout: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(ForgotPassword.this, "Connection timeout. Server might be slow or unreachable.", Toast.LENGTH_LONG).show();
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    String networkError = "Network error: " + e.getClass().getSimpleName() + " - " + e.getMessage();
                    Toast.makeText(ForgotPassword.this, networkError, Toast.LENGTH_LONG).show();
                    System.out.println("Network Error Details: " + e.getMessage());
                });
            }
        }).start();
    }

    private void startCountdown() {
        // Cancel any existing timer
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        buttonResend.setEnabled(false);

        countDownTimer = new CountDownTimer(15000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsRemaining = millisUntilFinished / 1000;
                buttonResend.setText("Resend (" + secondsRemaining + "s)");
            }

            @Override
            public void onFinish() {
                buttonResend.setText("Resend");
                buttonResend.setEnabled(true);
            }
        };

        countDownTimer.start();
    }

    private void handleBackPress() {
        // Cancel timer if running
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handleBackPress();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancel timer to prevent memory leaks
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}