package com.midterm.mobiledesignfinalterm.authentication;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.midterm.mobiledesignfinalterm.homepage.Homepage;
import com.midterm.mobiledesignfinalterm.R;

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
    private Button buttonGoogleSignIn; // ✅ Added Google Sign In Button

    private boolean isPasswordVisible = false;

    // ✅ --- Google Sign-In Declarations ---
    private static final String TAG = "GoogleSignIn";
    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> mGoogleSignInLauncher;
    // ✅ --- End of Google Sign-In Declarations ---

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.setAppearanceLightStatusBars(false);

        initializeViews();
        setupClickListeners();
        setupFocusListeners();
        setupPasswordToggle();

        // ✅ --- Initialize Google Sign-In ---
        configureGoogleSignIn();
        setupGoogleSignInLauncher();
        // ✅ --- End of Google Sign-In Initialization ---
    }

    // ✅ --- Check for existing signed-in user ---
    @Override
    protected void onStart() {
        super.onStart();
        // Automatic Google sign-in disabled. User must manually sign in.
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        if (account != null) {
//            Toast.makeText(this, "Already signed in. Navigating to homepage.", Toast.LENGTH_SHORT).show();
//            navigateToHomepage(account);
//        }
    }
    // ✅ --- End of check ---

    private void initializeViews() {
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonTogglePassword = findViewById(R.id.buttonTogglePassword);
        checkBoxRememberMe = findViewById(R.id.checkBoxRememberMe);
        buttonSignIn = findViewById(R.id.buttonSignIn);
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);
        textViewSignUp = findViewById(R.id.textViewSignUp);
        buttonGoogleSignIn = findViewById(R.id.buttonGoogleSignIn); // ✅ Initialize Google Sign In Button
    }

    private void setupClickListeners() {
        buttonSignIn.setOnClickListener(v -> animateButtonClick(v, this::handleSignIn));
        textViewForgotPassword.setOnClickListener(v -> {
            animateTextClick(v);
            handleForgotPassword();
        });
        textViewSignUp.setOnClickListener(v -> {
            animateTextClick(v);
            handleSignUp();
        });
        checkBoxRememberMe.setOnClickListener(this::animateCheckboxClick);

        // ✅ --- Google Sign-In Button Click Listener ---
        buttonGoogleSignIn.setOnClickListener(v -> {
            animateButtonClick(v, this::signInWithGoogle);
        });
        // ✅ --- End of Google Sign-In Listener ---
    }

    // ✅ --- Google Sign-In Methods ---

    /**
     * Configures the GoogleSignInClient with the required options.
     * Requests user's ID, email, and basic profile.
     * The ID token is requested using the web client ID from your google-services.json.
     */
    private void configureGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Get web client ID from strings
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    /**
     * Initializes the ActivityResultLauncher for the Google Sign-In flow.
     * This is the modern replacement for onActivityResult().
     */
    private void setupGoogleSignInLauncher() {
        mGoogleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        handleGoogleSignInResult(task);
                    } else {
                        Log.w(TAG, "Sign-in flow cancelled by user.");
                        Toast.makeText(Login.this, "Google Sign-In was cancelled.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Initiates the Google Sign-In flow by launching the sign-in intent.
     */
    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        mGoogleSignInLauncher.launch(signInIntent);
    }

    /**
     * Handles the result of the Google Sign-In authentication.
     * On success, navigates to the homepage. On failure, shows an error.
     * @param completedTask The task containing the sign-in result.
     */
    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.d(TAG, "signInResult:success, user: " + account.getEmail());
            Toast.makeText(this, "Google Sign-In successful!", Toast.LENGTH_SHORT).show();
            navigateToHomepage(account);
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(this, "Google Sign-In failed. Please try again.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Navigates to the Homepage activity with the Google user's information.
     * @param account The signed-in Google account.
     */
    private void navigateToHomepage(GoogleSignInAccount account) {
        Intent intent = new Intent(Login.this, Homepage.class);
        intent.putExtra("user_name", account.getDisplayName());
        intent.putExtra("user_email", account.getEmail()); // Pass email as well
        intent.putExtra("user_id", account.getId()); // Pass Google's unique user ID

        // You can also pass the profile picture URI if your homepage can display it
        if (account.getPhotoUrl() != null) {
            intent.putExtra("user_photo_uri", account.getPhotoUrl().toString());
        }

        startActivity(intent);
        finish(); // Close login activity
    }

    // ✅ --- End of Google Sign-In Methods ---


    // --- Existing Methods (No changes below this line, except for slight reformatting) ---

    private void setupPasswordToggle() {
        buttonTogglePassword.setOnClickListener(v -> togglePasswordVisibility());
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            buttonTogglePassword.setImageResource(R.drawable.ic_eye_off);
            isPasswordVisible = false;
        } else {
            editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            buttonTogglePassword.setImageResource(R.drawable.ic_eye_on);
            isPasswordVisible = true;
        }
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

    private void setupFocusListeners() {
        View.OnFocusChangeListener focusListener = (v, hasFocus) -> animateEditTextFocus(v, hasFocus);
        editTextPhoneNumber.setOnFocusChangeListener(focusListener);
        editTextPassword.setOnFocusChangeListener(focusListener);
    }

    private void animateEditTextFocus(View view, boolean hasFocus) {
        if (hasFocus) {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.05f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.05f, 1f);
            ObjectAnimator elevation = ObjectAnimator.ofFloat(view, "elevation", 0f, 8f);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(scaleX, scaleY, elevation);
            animatorSet.setDuration(300);
            animatorSet.setInterpolator(new OvershootInterpolator(1.1f));
            animatorSet.start();
        } else {
            ObjectAnimator.ofFloat(view, "elevation", 8f, 0f).setDuration(200).start();
        }
    }

    private void animateButtonClick(View button, Runnable onComplete) {
        button.animate().scaleX(0.95f).scaleY(0.95f).setDuration(70).withEndAction(() -> button.animate().scaleX(1f).scaleY(1f).setDuration(100).withEndAction(onComplete).start()).start();
    }

    private void animateTextClick(View view) {
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
        ObjectAnimator.ofFloat(view, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f).setDuration(600).start();
    }

    public void handleSignIn() {
        String phoneNumber = editTextPhoneNumber.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (phoneNumber.equals("0123456789") && password.equals("123456")) {
            Intent intent = new Intent(Login.this, Homepage.class);
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

                            // Get user role
                            String userRole = userObject.optString("role", "user");

                            // Create a user roles list
                            ArrayList<String> userRoles = new ArrayList<>();
                            userRoles.add(userRole);

                            // ✅ Debug: Print what we extracted
                            System.out.println("Extracted user data:");
                            System.out.println("Username: '" + userName + "'");
                            System.out.println("Phone: '" + userPhone + "'");
                            System.out.println("User ID: '" + userId + "'");
                            System.out.println("User Role: '" + userRole + "'");
                            System.out.println("Full user object: " + userObject.toString());

                            // ✅ Check if user is admin and redirect accordingly
                            System.out.println("Checking user role: " + userRole);
                            if ("admin".equals(userRole)) {
                                // Redirect to Admin Dashboard
                                System.out.println("Redirecting to Admin Dashboard for user: " + userName);
                                Toast.makeText(Login.this, "Chào mừng Admin " + userName, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Login.this, com.midterm.mobiledesignfinalterm.admin.AdminDashboard.class);
                                intent.putExtra("user_name", userName);
                                intent.putExtra("user_phone", userPhone);
                                intent.putExtra("user_id", userId);
                                intent.putExtra("user_role", userRole);
                                intent.putStringArrayListExtra("user_roles", userRoles);
                                intent.putExtra("user_data", userObject.toString());
                                startActivity(intent);
                                finish();
                            } else {
                                // ✅ Pass user data to Homepage for regular users
                                System.out.println("Redirecting to Homepage for regular user: " + userName);
                                Intent intent = new Intent(Login.this, Homepage.class);
                                intent.putExtra("user_name", userName);
                                intent.putExtra("user_phone", userPhone);
                                intent.putExtra("user_id", userId);
                                intent.putStringArrayListExtra("user_roles", userRoles);
                                intent.putExtra("user_data", userObject.toString());
                                startActivity(intent);
                                finish(); // Close login activity so user can't go back
                            }

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
        Intent intent = new Intent(Login.this, ForgotPassword.class);
        startActivity(intent);
    }

    private void handleSignUp() {
        Intent intent = new Intent(Login.this, Register.class);
        startActivity(intent);
    }
}
