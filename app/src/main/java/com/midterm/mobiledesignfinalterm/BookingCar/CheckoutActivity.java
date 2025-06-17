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
    private String carPriceStr;
    private double carPriceRaw = 0;

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
        carPriceStr = intent.getStringExtra("car_price");
        // Ưu tiên lấy car_price_raw từ intent, nếu không có thì mới lấy carPriceStr
        if (intent.hasExtra("car_price_raw")) {
            carPriceRaw = intent.getDoubleExtra("car_price_raw", -1);
        } else {
            carPriceRaw = -1;
        }
        calculateTotalAmount();
    }

    private void calculateTotalAmount() {
        double carPrice = 0;
        if (carPriceRaw > 0) {
            carPrice = carPriceRaw;
        } else if (carPriceStr != null) {
            try {
                String cleanPrice = carPriceStr.replaceAll("[^\\d.]", "");
                carPrice = Double.parseDouble(cleanPrice);
            } catch (Exception e) {
                carPrice = 0;
            }
        }
        int rentalDays = getRentalDays(pickupDate, dropoffDate);
        if (rentalDays <= 0) rentalDays = 1;
        totalAmount = carPrice * rentalDays;
    }

    private int getRentalDays(String start, String end) {
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            java.util.Date d1 = sdf.parse(start);
            java.util.Date d2 = sdf.parse(end);
            long diff = d2.getTime() - d1.getTime();
            return (int) Math.ceil(diff / (1000.0 * 60 * 60 * 24));
        } catch (Exception e) {
            return 1;
        }
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

        tvTotalAmount.setText(formatCurrencyVND(totalAmount));
    }

    private String formatCurrencyVND(double amount) {
        java.text.NumberFormat formatter = java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN"));
        return formatter.format(amount) + " VND";
    }

    private String generateBookingId() {
        Random random = new Random();
        return "BK" + String.format("%06d", random.nextInt(1000000));
    }
}