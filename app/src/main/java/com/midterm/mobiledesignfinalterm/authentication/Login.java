package com.midterm.mobiledesignfinalterm.authentication;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.midterm.mobiledesignfinalterm.MainActivity;
import com.midterm.mobiledesignfinalterm.R;
import com.midterm.mobiledesignfinalterm.admin.AdminLoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Login extends AppCompatActivity {

    private EditText editTextPhoneNumber;
    private EditText editTextPassword;
    private ImageButton buttonTogglePassword;
    private CheckBox checkBoxRememberMe;
    private Button buttonSignIn;
    private TextView textViewForgotPassword;
    private TextView textViewSignUp;
    private TextView textViewAdminAccess;

    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Make status bar transparent and content edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.setAppearanceLightStatusBars(false);

        initializeViews();
        setupClickListeners();
        setupFocusListeners();
        setupPasswordToggle();

    }

    private void setupPasswordToggle() {
        buttonTogglePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide password
            editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            buttonTogglePassword.setImageResource(R.drawable.ic_eye_off); // You'll need this drawable
            isPasswordVisible = false;
        } else {
            // Show password
            editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            buttonTogglePassword.setImageResource(R.drawable.ic_eye_on); // You'll need this drawable
            isPasswordVisible = true;
        }

        // Move cursor to end of text
        editTextPassword.setSelection(editTextPassword.getText().length());

        // Animate the toggle button
        animatePasswordToggle(buttonTogglePassword);
    }

    private void animatePasswordToggle(View view) {
        // Rotate and scale animation for password toggle
        ObjectAnimator rotation = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.2f, 1f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(rotation, scaleX, scaleY);
        animatorSet.setDuration(300);
        animatorSet.setInterpolator(new OvershootInterpolator(1.1f));
        animatorSet.start();
    }

    private void initializeViews() {
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonTogglePassword = findViewById(R.id.buttonTogglePassword);
        checkBoxRememberMe = findViewById(R.id.checkBoxRememberMe);
        buttonSignIn = findViewById(R.id.buttonSignIn);
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);
        textViewSignUp = findViewById(R.id.textViewSignUp);
        textViewAdminAccess = findViewById(R.id.textViewAdminAccess);
    }

    private void setupClickListeners() {
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(v, () -> handleSignIn());
            }
        });

        textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateTextClick(v);
                handleForgotPassword();
            }
        });

        textViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateTextClick(v);
                handleSignUp();
            }
        });

        checkBoxRememberMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateCheckboxClick(v);
            }
        });

        textViewAdminAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateTextClick(v);
                handleAdminAccess();
            }
        });
    }

    private void setupFocusListeners() {
        // Phone number field focus animation
        editTextPhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    animateEditTextFocus(v, true);
                } else {
                    animateEditTextFocus(v, false);
                }
            }
        });

        // Password field focus animation
        editTextPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    animateEditTextFocus(v, true);
                } else {
                    animateEditTextFocus(v, false);
                }
            }
        });
    }

    // Animation Methods
    private void animateInitialEntrance() {
        View[] views = {editTextPhoneNumber, editTextPassword, buttonTogglePassword,
                checkBoxRememberMe, buttonSignIn,
                textViewForgotPassword, textViewSignUp, textViewAdminAccess};

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

    private void animateEditTextFocus(View view, boolean hasFocus) {
        if (hasFocus) {
            // Focus gained - popup effect
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.05f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.05f, 1f);
            ObjectAnimator elevation = ObjectAnimator.ofFloat(view, "elevation", 0f, 8f);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(scaleX, scaleY, elevation);
            animatorSet.setDuration(300);
            animatorSet.setInterpolator(new OvershootInterpolator(1.1f));
            animatorSet.start();
        } else {
            // Focus lost - return to normal
            ObjectAnimator elevation = ObjectAnimator.ofFloat(view, "elevation", 8f, 0f);
            elevation.setDuration(200);
            elevation.start();
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

    private void animateTextClick(View view) {
        // Text click popup effect
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.2f, 1f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 1f, 0.7f, 1f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, alpha);
        animatorSet.setDuration(250);
        animatorSet.setInterpolator(new OvershootInterpolator(1.3f));
        animatorSet.start();
    }

    private void animateCheckboxClick(View view) {
        // Checkbox click popup effect
        ObjectAnimator rotation = ObjectAnimator.ofFloat(view, "rotation", 0f, 15f, -15f, 0f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.3f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.3f, 1f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(rotation, scaleX, scaleY);
        animatorSet.setDuration(350);
        animatorSet.setInterpolator(new BounceInterpolator());
        animatorSet.start();
    }

    private void animateErrorShake(View view) {
        // Error shake animation
        ObjectAnimator shake = ObjectAnimator.ofFloat(view, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f);
        shake.setDuration(600);
        shake.start();
    }

    public void handleSignIn() {
        String phoneNumber = editTextPhoneNumber.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (phoneNumber.equals("0123456789") && password.equals("123456")) {
            Intent intent = new Intent(Login.this, MainActivity.class);
            intent.putExtra("user_name", "Tài khoản ảo");
            intent.putExtra("user_phone", phoneNumber);
            intent.putExtra("user_id", "virtual_user");
            intent.putExtra("user_data", "{}");
            startActivity(intent);
            finish();
            return;
        }

        if (phoneNumber.isEmpty()) {
            editTextPhoneNumber.setError("Phone number is required");
            editTextPhoneNumber.requestFocus();
            animateErrorShake(editTextPhoneNumber);
            return;
        }

        String phoneRegex = "^[+]?[0-9]{10,13}$";
        if (!phoneNumber.matches(phoneRegex)) {
            editTextPhoneNumber.setError("Please enter a valid phone number");
            editTextPhoneNumber.requestFocus();
            animateErrorShake(editTextPhoneNumber);
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            animateErrorShake(editTextPassword);
            return;
        }

        // ✅ Send JSON POST request
        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2/myapi/login.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);

                // Prepare JSON payload
                String jsonInputString = "{\"phone_number\":\"" + phoneNumber + "\", \"password\":\"" + password + "\"}";

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

                // ✅ Debug: Print the full response to see what we received
                System.out.println("Full API Response: " + response.toString());

                runOnUiThread(() -> {
                    try {
                        if (result.getBoolean("success")) {
                            // ✅ Extract user object from API response
                            JSONObject userObject = result.getJSONObject("user");

                            // ✅ Get user data from the user object
                            // Try different possible field names for username
                            String userName = "";
                            if (userObject.has("name")) {
                                userName = userObject.getString("name");
                            } else if (userObject.has("username")) {
                                userName = userObject.getString("username");
                            } else if (userObject.has("full_name")) {
                                userName = userObject.getString("full_name");
                            } else if (userObject.has("first_name")) {
                                userName = userObject.getString("first_name");
                            }

                            String userPhone = userObject.optString("phone_number", phoneNumber);

                            // Get the user ID from account_id field
                            String userId = String.valueOf(userObject.optInt("account_id", 0));
                            if (userId.equals("0")) {
                                userId = userObject.optString("account_id", "");
                            }

                            // Create a user roles list - even though API might not provide roles,
                            // we'll set a default "User" role
                            ArrayList<String> userRoles = new ArrayList<>();
                            userRoles.add("User");

                            // ✅ Debug: Print what we extracted
                            System.out.println("Extracted user data:");
                            System.out.println("Username: '" + userName + "'");
                            System.out.println("Phone: '" + userPhone + "'");
                            System.out.println("User ID: '" + userId + "'");
                            System.out.println("Full user object: " + userObject.toString());

                            // ✅ Pass user data to MainActivity
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            intent.putExtra("user_name", userName);
                            intent.putExtra("user_phone", userPhone);
                            intent.putExtra("user_id", userId);
                            intent.putStringArrayListExtra("user_roles", userRoles);

                            // ✅ Pass the entire user object as JSON string for future use
                            intent.putExtra("user_data", userObject.toString());

                            startActivity(intent);
                            finish(); // Close login activity so user can't go back

                        } else {
                            String errorMessage = result.optString("error", "Phone number or password is incorrect");
                            Toast.makeText(Login.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                        // ✅ Enhanced error handling with debug info
                        String errorMsg = "Invalid server response: " + e.getMessage();
                        Toast.makeText(Login.this, errorMsg, Toast.LENGTH_LONG).show();

                        // Debug: Print the full response and error details
                        System.out.println("JSON Parsing Error: " + e.getMessage());
                        System.out.println("Raw response that caused error: " + response.toString());

                        // Try to show what keys are actually available
                        try {
                            System.out.println("Available keys in response: " + result.keys().toString());
                            if (result.has("user")) {
                                JSONObject userObj = result.getJSONObject("user");
                                System.out.println("Available keys in user object: " + userObj.keys().toString());
                            }
                        } catch (Exception debugError) {
                            System.out.println("Could not debug response structure: " + debugError.getMessage());
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    String networkError = "Network error: " + e.getMessage();
                    Toast.makeText(Login.this, networkError, Toast.LENGTH_LONG).show();
                    System.out.println("Network Error Details: " + e.getMessage());
                });
            }
        }).start();
    }

    private void handleForgotPassword() {
        // Handle forgot password logic
        Toast.makeText(this, "Forgot password clicked", Toast.LENGTH_SHORT).show();
    }

    private void handleSignUp() {
        // Handle sign up logic
        Intent intent = new Intent(Login.this, Register.class);
        startActivity(intent);
    }

    private void handleAdminAccess() {
        // Handle admin access logic
        Intent intent = new Intent(Login.this, AdminLoginActivity.class);
        startActivity(intent);
    }
}
