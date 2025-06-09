package com.midterm.mobiledesignfinalterm.BookingCar;

import android.content.Intent;
import android.os.Bundle;
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
                Intent intent = new Intent(UserInfoActivity.this, CheckoutActivity.class);
                // Pass booking details
                intent.putExtra("pickup_location", pickupLocation);
                intent.putExtra("dropoff_location", dropoffLocation);
                intent.putExtra("pickup_date", pickupDate);
                intent.putExtra("pickup_time", pickupTime);
                intent.putExtra("dropoff_date", dropoffDate);
                intent.putExtra("dropoff_time", dropoffTime);
                // Pass user info
                intent.putExtra("full_name", etFullName.getText().toString());
                intent.putExtra("email", etEmail.getText().toString());
                intent.putExtra("phone", etPhone.getText().toString());
                intent.putExtra("aadhar_number", etAadharNumber.getText().toString());
                intent.putExtra("pan_number", etPanNumber.getText().toString());
                startActivity(intent);
            }
        });
    }

    private boolean validateInputs() {
        if (etFullName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter your full name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etEmail.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etPhone.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter your phone number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etAadharNumber.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter your Aadhar number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etPanNumber.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter your PAN number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!cbTermsAccepted.isChecked()) {
            Toast.makeText(this, "Please accept the Terms of Service", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
