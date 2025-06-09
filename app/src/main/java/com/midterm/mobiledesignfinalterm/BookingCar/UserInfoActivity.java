package com.midterm.mobiledesignfinalterm.BookingCar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.midterm.mobiledesignfinalterm.R;

public class UserInfoActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPhone, etAadharNumber, etPanNumber;
    private CheckBox cbTermsAccepted;
    private Button btnNextStep;
    private ImageView ivBack;

    private String pickupLocation, dropoffLocation, pickupDate, pickupTime, dropoffDate, dropoffTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        getIntentData();
        initViews();
        setupClickListeners();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        pickupLocation = intent.getStringExtra("pickup_location");
        dropoffLocation = intent.getStringExtra("dropoff_location");
        pickupDate = intent.getStringExtra("pickup_date");
        pickupTime = intent.getStringExtra("pickup_time");
        dropoffDate = intent.getStringExtra("dropoff_date");
        dropoffTime = intent.getStringExtra("dropoff_time");
    }

    private void initViews() {
        etFullName = findViewById(R.id.et_full_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        etAadharNumber = findViewById(R.id.et_aadhar_number);
        etPanNumber = findViewById(R.id.et_pan_number);
        cbTermsAccepted = findViewById(R.id.cb_terms_accepted);
        btnNextStep = findViewById(R.id.btn_next_step);
        ivBack = findViewById(R.id.iv_back);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());

        btnNextStep.setOnClickListener(v -> {
            if (validateInputs()) {
                // Show loading state
                btnNextStep.setEnabled(false);
                btnNextStep.setText("Verifying...");

                Intent intent = new Intent(UserInfoActivity.this, CheckoutActivity.class);
                // Pass booking details
                intent.putExtra("pickup_location", pickupLocation);
                intent.putExtra("dropoff_location", dropoffLocation);
                intent.putExtra("pickup_date", pickupDate);
                intent.putExtra("pickup_time", pickupTime);
                intent.putExtra("dropoff_date", dropoffDate);
                intent.putExtra("dropoff_time", dropoffTime);
                // Pass user info
                intent.putExtra("full_name", etFullName.getText().toString().trim());
                intent.putExtra("email", etEmail.getText().toString().trim());
                intent.putExtra("phone", etPhone.getText().toString().trim());
                intent.putExtra("aadhar_number", etAadharNumber.getText().toString().trim());
                intent.putExtra("pan_number", etPanNumber.getText().toString().trim());

                startActivity(intent);

                // Reset button state
                btnNextStep.setEnabled(true);
                btnNextStep.setText("Verify");
            }
        });
    }

    private boolean validateInputs() {
        // Full Name validation
        String fullName = etFullName.getText().toString().trim();
        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Please enter your full name");
            etFullName.requestFocus();
            return false;
        }
        if (fullName.length() < 2) {
            etFullName.setError("Name must be at least 2 characters");
            etFullName.requestFocus();
            return false;
        }

        // Email validation
        String email = etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Please enter your email");
            etEmail.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email address");
            etEmail.requestFocus();
            return false;
        }

        // Phone validation
        String phone = etPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Please enter your phone number");
            etPhone.requestFocus();
            return false;
        }
        if (!Patterns.PHONE.matcher(phone).matches() || phone.length() < 10) {
            etPhone.setError("Please enter a valid phone number");
            etPhone.requestFocus();
            return false;
        }

        // Aadhar validation
        String aadhar = etAadharNumber.getText().toString().trim();
        if (TextUtils.isEmpty(aadhar)) {
            etAadharNumber.setError("Please enter your Aadhar number");
            etAadharNumber.requestFocus();
            return false;
        }
        if (aadhar.length() != 12 || !aadhar.matches("\\d+")) {
            etAadharNumber.setError("Aadhar number must be 12 digits");
            etAadharNumber.requestFocus();
            return false;
        }

        // PAN validation
        String pan = etPanNumber.getText().toString().trim().toUpperCase();
        if (TextUtils.isEmpty(pan)) {
            etPanNumber.setError("Please enter your PAN number");
            etPanNumber.requestFocus();
            return false;
        }

        // Terms acceptance
        if (!cbTermsAccepted.isChecked()) {
            Toast.makeText(this, "Please accept the Terms of Service", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reset button state when returning to this activity
        btnNextStep.setEnabled(true);
        btnNextStep.setText("Verify");
    }
}