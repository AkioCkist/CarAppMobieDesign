package com.midterm.mobiledesignfinalterm.BookingCar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.midterm.mobiledesignfinalterm.MainActivity;
import com.midterm.mobiledesignfinalterm.R;

public class CheckoutActivity extends AppCompatActivity {

    private TextView tvPickupDetails, tvDropoffDetails, tvUserDetails, tvTotalAmount;
    private TextView tvBookingDuration, tvBaseFare, tvTaxes, tvDiscount;
    private RadioGroup rgPaymentMethod;
    private Button btnConfirmBooking;
    private ImageView ivBack;

    private String pickupLocation, dropoffLocation, pickupDate, pickupTime, dropoffDate, dropoffTime;
    private String fullName, email, phone, aadharNumber, panNumber;

    // Enhanced pricing structure
    private double baseFare = 50.00;
    private double perDayRate = 15.00;
    private double taxes = 0.0;
    private double discount = 0.0;
    private double totalAmount = 0.0;
    private int bookingDurationDays = 1;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        getIntentData();
        initViews();
        calculatePricing();
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
        aadharNumber = intent.getStringExtra("aadhar_number");
        panNumber = intent.getStringExtra("pan_number");
    }

    private void initViews() {
        tvPickupDetails = findViewById(R.id.tv_pickup_details);
        tvDropoffDetails = findViewById(R.id.tv_dropoff_details);
        tvUserDetails = findViewById(R.id.tv_user_details);
        tvTotalAmount = findViewById(R.id.tv_total_amount);

        // Additional pricing breakdown views (add these to your layout if needed)
        tvBookingDuration = findViewById(R.id.tv_booking_duration);
        tvBaseFare = findViewById(R.id.tv_base_fare);
        tvTaxes = findViewById(R.id.tv_taxes);
        tvDiscount = findViewById(R.id.tv_discount);

        rgPaymentMethod = findViewById(R.id.rg_payment_method);
        btnConfirmBooking = findViewById(R.id.btn_confirm_booking);
        ivBack = findViewById(R.id.iv_back);
    }

    private void calculatePricing() {
        try {
            // Calculate booking duration
            bookingDurationDays = calculateBookingDuration();

            // Calculate base fare (minimum 1 day)
            if (bookingDurationDays < 1) bookingDurationDays = 1;

            double fareBeforeTax = baseFare + (perDayRate * bookingDurationDays);

            // Apply discount for longer bookings
            if (bookingDurationDays >= 7) {
                discount = fareBeforeTax * 0.15; // 15% discount for weekly bookings
            } else if (bookingDurationDays >= 3) {
                discount = fareBeforeTax * 0.05; // 5% discount for 3+ day bookings
            }

            // Calculate taxes (10% of fare after discount)
            double fareAfterDiscount = fareBeforeTax - discount;
            taxes = fareAfterDiscount * 0.10;

            // Calculate total
            totalAmount = fareAfterDiscount + taxes;

        } catch (Exception e) {
            // Fallback to default pricing
            bookingDurationDays = 1;
            totalAmount = 80.00;
            taxes = 7.27;
            discount = 0.0;
        }
    }

    private int calculateBookingDuration() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date pickup = sdf.parse(pickupDate);
            Date dropoff = sdf.parse(dropoffDate);

            if (pickup != null && dropoff != null) {
                long diffInMillis = dropoff.getTime() - pickup.getTime();
                int days = (int) TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
                return Math.max(1, days); // Minimum 1 day
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1; // Default to 1 day
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> {
            showBackConfirmationDialog();
        });

        btnConfirmBooking.setOnClickListener(v -> {
            if (validatePaymentSelection()) {
                showBookingConfirmationDialog();
            }
        });
    }

    private void showBackConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Go Back?")
                .setMessage("Your booking details will be lost. Are you sure you want to go back?")
                .setPositiveButton("Yes", (dialog, which) -> finish())
                .setNegativeButton("No", null)
                .show();
    }

    private boolean validatePaymentSelection() {
        int selectedPaymentId = rgPaymentMethod.getCheckedRadioButtonId();
        if (selectedPaymentId == -1) {
            Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void showBookingConfirmationDialog() {
        RadioButton selectedPayment = findViewById(rgPaymentMethod.getCheckedRadioButtonId());
        String paymentMethod = selectedPayment.getText().toString();

        String message = String.format(
                "Confirm your booking:\n\n" +
                        "From: %s\nTo: %s\n" +
                        "Duration: %d day(s)\n" +
                        "Total Amount: $%.2f\n" +
                        "Payment Method: %s",
                pickupLocation, dropoffLocation, bookingDurationDays, totalAmount, paymentMethod
        );

        new AlertDialog.Builder(this)
                .setTitle("Confirm Booking")
                .setMessage(message)
                .setPositiveButton("Confirm", (dialog, which) -> processBooking())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void processBooking() {
        // Show progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing your booking...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Simulate processing time
        new Handler().postDelayed(() -> {
            progressDialog.dismiss();
            completeBooking();
        }, 2000); // 2 second delay
    }

    private void completeBooking() {
        RadioButton selectedPayment = findViewById(rgPaymentMethod.getCheckedRadioButtonId());
        String paymentMethod = selectedPayment.getText().toString();
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
        intent.putExtra("payment_method", paymentMethod);
        intent.putExtra("total_amount", String.format("%.2f", totalAmount));
        intent.putExtra("booking_duration", String.valueOf(bookingDurationDays));
        intent.putExtra("base_fare", String.format("%.2f", baseFare));
        intent.putExtra("taxes", String.format("%.2f", taxes));
        intent.putExtra("discount", String.format("%.2f", discount));

        startActivity(intent);

        // Clear activity stack and show success message
        Toast.makeText(this, "Booking confirmed successfully!", Toast.LENGTH_SHORT).show();

        Intent mainIntent = new Intent(CheckoutActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void displayBookingDetails() {
        // Pickup details
        tvPickupDetails.setText(String.format("Pickup Location: %s\nDate & Time: %s at %s",
                pickupLocation != null ? pickupLocation : "Not specified",
                pickupDate != null ? pickupDate : "Not specified",
                pickupTime != null ? pickupTime : "Not specified"));

        // Drop-off details
        tvDropoffDetails.setText(String.format("Drop-off Location: %s\nDate & Time: %s at %s",
                dropoffLocation != null ? dropoffLocation : "Not specified",
                dropoffDate != null ? dropoffDate : "Not specified",
                dropoffTime != null ? dropoffTime : "Not specified"));

        // User details
        tvUserDetails.setText(String.format("Name: %s\nEmail: %s\nPhone: %s",
                fullName != null ? fullName : "Not provided",
                email != null ? email : "Not provided",
                phone != null ? phone : "Not provided"));

        // Pricing breakdown (if views exist in layout)
        if (tvBookingDuration != null) {
            tvBookingDuration.setText(String.format("Duration: %d day(s)", bookingDurationDays));
        }
        if (tvBaseFare != null) {
            tvBaseFare.setText(String.format("Base Fare: $%.2f", baseFare + (perDayRate * bookingDurationDays)));
        }
        if (tvTaxes != null) {
            tvTaxes.setText(String.format("Taxes: $%.2f", taxes));
        }
        if (tvDiscount != null && discount > 0) {
            tvDiscount.setText(String.format("Discount: -$%.2f", discount));
        }

        // Total amount
        tvTotalAmount.setText(String.format("Total: $%.2f", totalAmount));
    }

    private String generateBookingId() {
        Random random = new Random();
        String timestamp = new SimpleDateFormat("yyMMdd", Locale.getDefault()).format(new Date());
        return "BK" + timestamp + String.format("%04d", random.nextInt(10000));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        showBackConfirmationDialog();
    }
}