package com.midterm.mobiledesignfinalterm.BookingCar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

import com.midterm.mobiledesignfinalterm.MainActivity;
import com.midterm.mobiledesignfinalterm.R;
public class CheckoutActivity extends AppCompatActivity {

    private TextView tvPickupDetails, tvDropoffDetails, tvUserDetails, tvTotalAmount;
    private RadioGroup rgPaymentMethod;
    private Button btnConfirmBooking;
    private ImageView ivBack;

    private String pickupLocation, dropoffLocation, pickupDate, pickupTime, dropoffDate, dropoffTime;
    private String fullName, email, phone, aadharNumber, panNumber;
    private String userId, userName, userPhone;
    private double totalAmount = 80.00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        getIntentData();
        initViews();
        setupClickListeners();
        displayBookingDetails();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        pickupLocation = intent.getStringExtra("pickup_location");
        dropoffLocation = intent.getStringExtra("dropoff_location");
        pickupDate = intent.getStringExtra("pickup_date");
        pickupTime = intent.getStringExtra("pickup_time");
        dropoffDate = intent.getStringExtra("dropoff_date");
        dropoffTime = intent.getStringExtra("dropoff_time");
        fullName = intent.getStringExtra("full_name");
        email = intent.getStringExtra("email");
        phone = intent.getStringExtra("phone");
        aadharNumber = intent.getStringExtra("citizen_id");
        panNumber = intent.getStringExtra("tax_id");
        userId = intent.getStringExtra("user_id");
        userName = intent.getStringExtra("user_name");
        userPhone = intent.getStringExtra("user_phone");
    }

    private void initViews() {
        tvPickupDetails = findViewById(R.id.tv_pickup_details);
        tvDropoffDetails = findViewById(R.id.tv_dropoff_details);
        tvUserDetails = findViewById(R.id.tv_user_details);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        rgPaymentMethod = findViewById(R.id.rg_payment_method);
        btnConfirmBooking = findViewById(R.id.btn_confirm_booking);
        ivBack = findViewById(R.id.iv_back);
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());

        btnConfirmBooking.setOnClickListener(v -> {
            int selectedPaymentId = rgPaymentMethod.getCheckedRadioButtonId();
            if (selectedPaymentId == -1) {
                Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedPayment = findViewById(selectedPaymentId);
            String paymentMethod = selectedPayment.getText().toString();

            // Generate booking ID
            String bookingId = generateBookingId();

            Intent intent = new Intent(CheckoutActivity.this, ThankYouActivity.class);
            intent.putExtra("booking_id", bookingId);
            intent.putExtra("pickup_location", pickupLocation);
            intent.putExtra("dropoff_location", dropoffLocation);
            intent.putExtra("pickup_date", pickupDate);
            intent.putExtra("pickup_time", pickupTime);
            intent.putExtra("dropoff_date", dropoffDate);
            intent.putExtra("dropoff_time", dropoffTime);
            intent.putExtra("full_name", fullName);
            intent.putExtra("email", email);
            intent.putExtra("phone", phone);
            intent.putExtra("citizen_id", aadharNumber);
            intent.putExtra("tax_id", panNumber);
            intent.putExtra("payment_method", paymentMethod);
            intent.putExtra("total_amount", String.valueOf(totalAmount));
            // Pass user data to ThankYouActivity
            intent.putExtra("user_id", userId);
            intent.putExtra("user_name", userName);
            intent.putExtra("user_phone", userPhone);
            startActivity(intent);
            finish();
        });
    }

    private void displayBookingDetails() {
        tvPickupDetails.setText(String.format("Pickup: %s\n%s at %s",
                pickupLocation, pickupDate, pickupTime));

        tvDropoffDetails.setText(String.format("Drop-off: %s\n%s at %s",
                dropoffLocation, dropoffDate, dropoffTime));

        tvUserDetails.setText(String.format("Name: %s\nEmail: %s\nPhone: %s",
                fullName, email, phone));

        tvTotalAmount.setText(String.format("$%.2f", totalAmount));
    }

    private String generateBookingId() {
        Random random = new Random();
        return "BK" + String.format("%06d", random.nextInt(1000000));
    }
}