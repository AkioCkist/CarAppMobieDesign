package com.midterm.mobiledesignfinalterm.authentication;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import com.midterm.mobiledesignfinalterm.R;

public class Login extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextPincode;
    private CheckBox checkBoxRememberMe;
    private Button buttonSignIn;
    private Button buttonSignInWithMobile;
    private TextView textViewForgotPassword;
    private TextView textViewSignUp;

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

        // Initial entrance animation for all elements
        animateInitialEntrance();
    }

    private void initializeViews() {
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPincode = findViewById(R.id.editTextPincode);
        checkBoxRememberMe = findViewById(R.id.checkBoxRememberMe);
        buttonSignIn = findViewById(R.id.buttonSignIn);
        buttonSignInWithMobile = findViewById(R.id.buttonSignInWithMobile);
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

        buttonSignInWithMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(v);
                handleSignInWithMobile();
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
        // Email field focus animation
        editTextEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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

        // Pincode field focus animation
        editTextPincode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
        View[] views = {editTextEmail, editTextPassword, editTextPincode,
                checkBoxRememberMe, buttonSignIn, buttonSignInWithMobile,
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

    private void handleSignIn() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String pincode = editTextPincode.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            animateErrorShake(editTextEmail);
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            animateErrorShake(editTextPassword);
            return;
        }

        if (pincode.isEmpty()) {
            editTextPincode.setError("Pincode is required");
            editTextPincode.requestFocus();
            animateErrorShake(editTextPincode);
            return;
        }

        // Success animation for sign-in button
        ObjectAnimator pulse = ObjectAnimator.ofFloat(buttonSignIn, "scaleX", 1f, 1.1f, 1f);
        ObjectAnimator pulseY = ObjectAnimator.ofFloat(buttonSignIn, "scaleY", 1f, 1.1f, 1f);
        AnimatorSet pulseSet = new AnimatorSet();
        pulseSet.playTogether(pulse, pulseY);
        pulseSet.setDuration(200);
        pulseSet.start();

        Toast.makeText(this, "Signing in...", Toast.LENGTH_SHORT).show();
    }

    private void handleSignInWithMobile() {
        // Animate button before showing toast
        ObjectAnimator wave = ObjectAnimator.ofFloat(buttonSignInWithMobile, "rotation", 0f, 5f, -5f, 0f);
        wave.setDuration(300);
        wave.start();

        Toast.makeText(this, "Sign in with mobile number", Toast.LENGTH_SHORT).show();
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