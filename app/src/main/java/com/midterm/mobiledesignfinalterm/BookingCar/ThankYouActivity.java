package com.midterm.mobiledesignfinalterm.BookingCar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.midterm.mobiledesignfinalterm.MainActivity;
import com.midterm.mobiledesignfinalterm.R;
import com.midterm.mobiledesignfinalterm.homepage.Homepage;
public class ThankYouActivity extends AppCompatActivity {

    private TextView tvBookingId, tvPickupDetails, tvDropoffDetails, tvUserDetails, tvPaymentDetails, tvTotalAmount;
    private Button btnPrintTicket, btnBackToHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);

        initViews();
        displayBookingDetails();
        setupClickListeners();
    }

    private void initViews() {
        tvBookingId = findViewById(R.id.tv_booking_id);
        tvPickupDetails = findViewById(R.id.tv_pickup_details);
        tvDropoffDetails = findViewById(R.id.tv_dropoff_details);
        tvUserDetails = findViewById(R.id.tv_user_details);
        tvPaymentDetails = findViewById(R.id.tv_payment_details);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        btnPrintTicket = findViewById(R.id.btn_print_ticket);
        btnBackToHome = findViewById(R.id.btn_back_to_home);
    }

    private void displayBookingDetails() {
        Intent intent = getIntent();

        String bookingId = intent.getStringExtra("booking_id");
        String pickupLocation = intent.getStringExtra("pickup_location");
        String dropoffLocation = intent.getStringExtra("dropoff_location");
        String pickupDate = intent.getStringExtra("pickup_date");
        String pickupTime = intent.getStringExtra("pickup_time");
        String dropoffDate = intent.getStringExtra("dropoff_date");
        String dropoffTime = intent.getStringExtra("dropoff_time");
        String fullName = intent.getStringExtra("full_name");
        String email = intent.getStringExtra("email");
        String phone = intent.getStringExtra("phone");
        String paymentMethod = intent.getStringExtra("payment_method");
        String totalAmount = intent.getStringExtra("total_amount");

        tvBookingId.setText("Booking ID: " + (bookingId != null ? bookingId : ""));
        tvPickupDetails.setText(String.format("Pickup: %s\n%s at %s",
                pickupLocation, pickupDate, pickupTime));
        tvDropoffDetails.setText(String.format("Drop-off: %s\n%s at %s",
                dropoffLocation, dropoffDate, dropoffTime));
        tvUserDetails.setText(String.format("Name: %s\nEmail: %s\nPhone: %s",
                fullName, email, phone));
        tvPaymentDetails.setText("Payment: " + (paymentMethod != null ? paymentMethod : ""));
        tvTotalAmount.setText(totalAmount != null ? "$" + totalAmount : "$0.00");
    }

    private void setupClickListeners() {
        btnPrintTicket.setOnClickListener(v -> {
            // Implement print logic or show a message
        });

        btnBackToHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, Homepage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
