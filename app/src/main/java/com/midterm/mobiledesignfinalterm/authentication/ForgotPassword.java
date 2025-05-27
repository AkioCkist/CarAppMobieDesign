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

public class ForgotPassword extends AppCompatActivity {




    private EditText editTextEmail;
    private Button buttonSend;
    private Button buttonResend;
    private ImageView imageViewBack;
    private CountDownTimer countDownTimer;
    private boolean isEmailSent = false;

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

    private void playPopupAnimation(View view) {
        Animation popupAnimation = AnimationUtils.loadAnimation(this, R.anim.popup_animation);
        view.startAnimation(popupAnimation);
    }
    private void initializeViews() {
        editTextEmail = findViewById(R.id.editTextEmail);
        buttonSend = findViewById(R.id.buttonSend);
        buttonResend = findViewById(R.id.buttonResend);
        imageViewBack = findViewById(R.id.imageViewBack);
    }

    private void setupClickListeners() {
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSendResetLink();
            }
        });

        buttonResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleResendResetLink();
            }
        });

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleBackPress();
            }
        });
    }

    private void handleSendResetLink() {
        String email = editTextEmail.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email address");
            editTextEmail.requestFocus();
            return;
        }

        // Add your forgot password logic here
        Toast.makeText(this, "Password reset link sent to " + email, Toast.LENGTH_LONG).show();

        // Show resend button and start countdown
        isEmailSent = true;
        buttonResend.setVisibility(View.VISIBLE);
        startCountdown();
    }

    private void handleResendResetLink() {
        String email = editTextEmail.getText().toString().trim();

        // Add your resend logic here
        Toast.makeText(this, "Password reset link resent to " + email, Toast.LENGTH_LONG).show();

        // Restart countdown
        startCountdown();
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