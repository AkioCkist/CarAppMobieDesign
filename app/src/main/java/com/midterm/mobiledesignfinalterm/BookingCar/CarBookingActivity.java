package com.midterm.mobiledesignfinalterm.BookingCar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import com.midterm.mobiledesignfinalterm.R;
public class CarBookingActivity extends AppCompatActivity {

    private EditText etPickupLocation, etDropoffLocation;
    private TextView tvPickupDate, tvPickupTime, tvDropoffDate, tvDropoffTime;
    private Button btnNextStep;
    private ImageView ivBack;

    private String selectedPickupDate = "";
    private String selectedPickupTime = "";
    private String selectedDropoffDate = "";
    private String selectedDropoffTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_booking);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        // Ensure these IDs exist in activity_car_booking.xml
        etPickupLocation = findViewById(R.id.et_pickup_location); // EditText for pickup location
        etDropoffLocation = findViewById(R.id.et_dropoff_location); // EditText for dropoff location
        tvPickupDate = findViewById(R.id.tv_pickup_date); // TextView for pickup date
        tvPickupTime = findViewById(R.id.tv_pickup_time); // TextView for pickup time
        tvDropoffDate = findViewById(R.id.tv_dropoff_date); // TextView for dropoff date
        tvDropoffTime = findViewById(R.id.tv_dropoff_time); // TextView for dropoff time
        btnNextStep = findViewById(R.id.btn_next_step); // Button for next step
        ivBack = findViewById(R.id.iv_back); // ImageView for back button
    }

    private void setupClickListeners() {
        ivBack.setOnClickListener(v -> finish());

        tvPickupDate.setOnClickListener(v -> showDatePicker(true));
        tvDropoffDate.setOnClickListener(v -> showDatePicker(false));
        tvPickupTime.setOnClickListener(v -> showTimePicker(true));
        tvDropoffTime.setOnClickListener(v -> showTimePicker(false));

        btnNextStep.setOnClickListener(v -> {
            if (validateInputs()) {
                Intent intent = new Intent(CarBookingActivity.this, UserInfoActivity.class);
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

    private void showDatePicker(boolean isPickup) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String date = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year);
                    if (isPickup) {
                        selectedPickupDate = date;
                        tvPickupDate.setText(date);
                    } else {
                        selectedDropoffDate = date;
                        tvDropoffDate.setText(date);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePicker(boolean isPickup) {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    String time = String.format("%02d:%02d", hourOfDay, minute);
                    if (isPickup) {
                        selectedPickupTime = time;
                        tvPickupTime.setText(time);
                    } else {
                        selectedDropoffTime = time;
                        tvDropoffTime.setText(time);
                    }
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private boolean validateInputs() {
        if (etPickupLocation.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter pickup location", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etDropoffLocation.getText().toString().trim().isEmpty()) {
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
}