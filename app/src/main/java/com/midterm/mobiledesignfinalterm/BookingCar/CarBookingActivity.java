package com.midterm.mobiledesignfinalterm.BookingCar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import java.util.Calendar;
import com.midterm.mobiledesignfinalterm.R;

public class CarBookingActivity extends AppCompatActivity {

    private EditText etPickupLocation, etDropoffLocation;
    private TextView tvPickupDate, tvPickupTime, tvDropoffDate, tvDropoffTime, tvCarName;
    private Button btnNextStep;
    private ImageView ivBack;

    private String selectedPickupDate = "";
    private String selectedPickupTime = "";
    private String selectedDropoffDate = "";
    private String selectedDropoffTime = "";
    private String carName = "";
    private BookingData bookingData;

    // Calendars to hold pickup and dropoff times for validation
    private Calendar pickupCalendar = Calendar.getInstance();
    private Calendar dropoffCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_booking);

        initViews();
        setupClickListeners();
        getCarDataFromIntent();
        initializeBookingData();
    }

    private void initViews() {
        etPickupLocation = findViewById(R.id.et_pickup_location);
        etDropoffLocation = findViewById(R.id.et_dropoff_location);
        tvPickupDate = findViewById(R.id.tv_pickup_date);
        tvPickupTime = findViewById(R.id.tv_pickup_time);
        tvDropoffDate = findViewById(R.id.tv_dropoff_date);
        tvDropoffTime = findViewById(R.id.tv_dropoff_time);
        btnNextStep = findViewById(R.id.btn_next_step);
        ivBack = findViewById(R.id.iv_back);

        // Optional: if you have a TextView to display car name
       // tvCarName = findViewById(R.id.tv_car_name); // Add this to your layout if needed
    }

    private void getCarDataFromIntent() {
        // Get car data from intent
        if (getIntent().hasExtra("car_name")) {
            carName = getIntent().getStringExtra("car_name");
        }

        // Get pickup and drop-off data from intent
        if (getIntent().hasExtra("pickup_date")) {
            selectedPickupDate = getIntent().getStringExtra("pickup_date");
            tvPickupDate.setText(selectedPickupDate);
        }

        if (getIntent().hasExtra("pickup_time")) {
            selectedPickupTime = getIntent().getStringExtra("pickup_time");
            tvPickupTime.setText(selectedPickupTime);
        }

        if (getIntent().hasExtra("dropoff_date")) {
            selectedDropoffDate = getIntent().getStringExtra("dropoff_date");
            tvDropoffDate.setText(selectedDropoffDate);
        }

        if (getIntent().hasExtra("dropoff_time")) {
            selectedDropoffTime = getIntent().getStringExtra("dropoff_time");
            tvDropoffTime.setText(selectedDropoffTime);
        }
    }

    private void initializeBookingData() {
        // Initialize BookingData with real-time data
        bookingData = new BookingData(carName);

        // Log booking initialization
        System.out.println("Booking initialized: " + bookingData.getBookingId());
        System.out.println("Timestamp: " + bookingData.getFormattedBookingDate());
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());

        tvPickupDate.setOnClickListener(v -> showDatePicker(true));
        tvDropoffDate.setOnClickListener(v -> showDatePicker(false));
        tvPickupTime.setOnClickListener(v -> showTimePicker(tvPickupTime, pickupCalendar));
        tvDropoffTime.setOnClickListener(v -> showTimePicker(tvDropoffTime, dropoffCalendar));

        btnNextStep.setOnClickListener(v -> {
            if (validateInputs()) {
                // Update booking data with current form inputs
                updateBookingDataFromForm();

                // Create intent for UserInfoActivity
                Intent intent = new Intent(CarBookingActivity.this, UserInfoActivity.class);

                // Pass booking data as serializable object
                intent.putExtra("booking_data", bookingData);

                // Also pass individual values for backward compatibility
                intent.putExtra("car_name", carName);
                intent.putExtra("pickup_location", etPickupLocation.getText().toString());
                intent.putExtra("dropoff_location", etDropoffLocation.getText().toString());
                intent.putExtra("pickup_date", selectedPickupDate);
                intent.putExtra("pickup_time", selectedPickupTime);
                intent.putExtra("dropoff_date", selectedDropoffDate);
                intent.putExtra("dropoff_time", selectedDropoffTime);

                startActivity(intent);
            }
        });
    }

    private void updateBookingDataFromForm() {
        // Update booking data with current timestamp
        bookingData.updateTimestamp();

        // Set form data to booking object
        bookingData.setPickupLocation(etPickupLocation.getText().toString().trim());
        bookingData.setDropoffLocation(etDropoffLocation.getText().toString().trim());
        bookingData.setPickupDate(selectedPickupDate);
        bookingData.setPickupTime(selectedPickupTime);
        bookingData.setDropoffDate(selectedDropoffDate);
        bookingData.setDropoffTime(selectedDropoffTime);

        // Log updated booking data
        System.out.println("Booking updated: " + bookingData.toString());
    }

    private void showDatePicker(boolean isPickup) {
        Calendar calendar = Calendar.getInstance();

        // Use custom dialog theme for better background and text color
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                new android.view.ContextThemeWrapper(this, R.style.CustomMaterialCalendar),
                (view, year, month, dayOfMonth) -> {
                    String date = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year);
                    if (isPickup) {
                        selectedPickupDate = date;
                        tvPickupDate.setText(date);
                        tvPickupDate.setTextColor(getResources().getColor(android.R.color.black));
                    } else {
                        selectedDropoffDate = date;
                        tvDropoffDate.setText(date);
                        tvDropoffDate.setTextColor(getResources().getColor(android.R.color.black));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private void showTimePicker(final TextView timeTextView, final Calendar calendar) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create the Material TimePicker
        MaterialTimePicker.Builder builder = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(hour)
                .setMinute(minute)
                .setTitleText("Select Time")
                .setTheme(R.style.CustomMaterialTimePicker); // Apply custom dark theme

        final MaterialTimePicker timePicker = builder.build();

        // Set up listeners for the time picker
        timePicker.addOnPositiveButtonClickListener(view -> {
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
            calendar.set(Calendar.MINUTE, timePicker.getMinute());
            updateTimeTextView(timeTextView, calendar);

            // If pickup time is selected and dropoff is on the same day, validate the times
            if (timeTextView == tvPickupTime &&
                isSameDay(pickupCalendar, dropoffCalendar) &&
                pickupCalendar.after(dropoffCalendar)) {

                // Set dropoff time to be 1 hour after pickup if on the same day
                dropoffCalendar.setTimeInMillis(pickupCalendar.getTimeInMillis());
                dropoffCalendar.add(Calendar.HOUR_OF_DAY, 1);
                updateTimeTextView(tvDropoffTime, dropoffCalendar);
                Toast.makeText(CarBookingActivity.this,
                    "Return time adjusted to be after pickup time",
                    Toast.LENGTH_SHORT).show();
            }
        });

        // Show the time picker
        timePicker.show(getSupportFragmentManager(), "TIME_PICKER");
    }

    private void updateTimeTextView(TextView timeTextView, Calendar calendar) {
        String time = String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        timeTextView.setText(time);
    }

    private boolean isSameDay(Calendar calendar1, Calendar calendar2) {
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
               calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR);
    }

    private boolean validateInputs() {
        String pickupLocation = etPickupLocation.getText().toString().trim();
        String dropoffLocation = etDropoffLocation.getText().toString().trim();

        if (pickupLocation.isEmpty()) {
            etPickupLocation.setError("Please enter pickup location");
            etPickupLocation.requestFocus();
            Toast.makeText(this, "Please enter pickup location", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (dropoffLocation.isEmpty()) {
            etDropoffLocation.setError("Please enter drop-off location");
            etDropoffLocation.requestFocus();
            Toast.makeText(this, "Please enter drop-off location", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedPickupDate.isEmpty()) {
            Toast.makeText(this, "Please select pickup date", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedPickupTime.isEmpty()) {
            Toast.makeText(this, "Please select pickup time", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedDropoffDate.isEmpty()) {
            Toast.makeText(this, "Please select drop-off date", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selectedDropoffTime.isEmpty()) {
            Toast.makeText(this, "Please select drop-off time", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update timestamp when activity resumes
        if (bookingData != null) {
            bookingData.updateTimestamp();
        }
    }
}
