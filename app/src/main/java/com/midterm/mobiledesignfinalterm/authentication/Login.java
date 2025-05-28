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
import com.midterm.mobiledesignfinalterm.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity {

    private EditText editTextPhoneNumber;
    private EditText editTextPassword;
    private ImageButton buttonTogglePassword;
    private CheckBox checkBoxRememberMe;
    private Button buttonSignIn;
    private TextView textViewForgotPassword;
    private TextView textViewSignUp;

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

        // Initial entrance animation for all elements
        animateInitialEntrance();
        buttonSignIn.setOnClickListener(v -> handleSignIn());
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

    private void attemptLogin(String phoneNumber, String password) {
        new Thread(() -> {
            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://your-db-url/dbname", "root", "");

                String sql = "SELECT a.account_id, r.role_name FROM accounts a " +
                        "JOIN account_roles ar ON a.account_id = ar.account_id " +
                        "JOIN roles r ON ar.role_id = r.role_id " +
                        "WHERE a.phone_number = ? AND a.password = ?";

                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, phoneNumber);
                stmt.setString(2, password);

                ResultSet rs = stmt.executeQuery();

                List<String> roles = new ArrayList<>();
                while (rs.next()) {
                    roles.add(rs.getString("role_name"));
                }

                conn.close();

                runOnUiThread(() -> {
                    if (roles.isEmpty()) {
                        // ⚠️ Invalid login
                        editTextPhoneNumber.setError("Incorrect phone number or password");
                        editTextPhoneNumber.requestFocus();
                        animateErrorShake(editTextPhoneNumber);
                        animateErrorShake(editTextPassword);
                        Toast.makeText(this, "Login failed: incorrect credentials", Toast.LENGTH_SHORT).show();
                    } else {
                        for (String role : roles) {
                            Toast.makeText(this, "Welcome " + role, Toast.LENGTH_SHORT).show();
                        }
                        // ✅ You can redirect to another screen here
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
                e.printStackTrace();
            }
        }).start();
    }

    private void initializeViews() {
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonTogglePassword = findViewById(R.id.buttonTogglePassword);
        checkBoxRememberMe = findViewById(R.id.checkBoxRememberMe);
        buttonSignIn = findViewById(R.id.buttonSignIn);
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);
        textViewSignUp = findViewById(R.id.textViewSignUp);
    }

    private void setupClickListeners() {
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(v);
                handleSignIn();
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
                textViewForgotPassword, textViewSignUp};

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

    private void animateButtonClick(View view) {
        // Button click popup effect
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.95f, 1.1f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.95f, 1.1f, 1f);
        ObjectAnimator elevation = ObjectAnimator.ofFloat(view, "elevation", 4f, 12f, 4f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, elevation);
        animatorSet.setDuration(400);
        animatorSet.setInterpolator(new BounceInterpolator());
        animatorSet.start();
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

        // ✅ Animate Sign In
        ObjectAnimator pulse = ObjectAnimator.ofFloat(buttonSignIn, "scaleX", 1f, 1.1f, 1f);
        ObjectAnimator pulseY = ObjectAnimator.ofFloat(buttonSignIn, "scaleY", 1f, 1.1f, 1f);
        AnimatorSet pulseSet = new AnimatorSet();
        pulseSet.playTogether(pulse, pulseY);
        pulseSet.setDuration(200);
        pulseSet.start();

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

                runOnUiThread(() -> {
                    try {
                        if (result.getBoolean("success")) {
                            Toast.makeText(Login.this, "Login successful!", Toast.LENGTH_SHORT).show();
                            // TODO: Navigate to main screen or save user session
                        } else {
                            Toast.makeText(Login.this, "Phone number or password is incorrect", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(Login.this, "Invalid server response", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(Login.this, "Network error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void handleForgotPassword() {
        Intent intent = new Intent(Login.this, ForgotPassword.class);
        startActivity(intent);
    }

    private void handleSignUp() {
        Intent intent = new Intent(Login.this, Register.class);
        startActivity(intent);
    }
}