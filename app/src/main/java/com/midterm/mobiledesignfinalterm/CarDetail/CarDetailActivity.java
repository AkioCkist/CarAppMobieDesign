package com.midterm.mobiledesignfinalterm.CarDetail;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.midterm.mobiledesignfinalterm.R;
import com.midterm.mobiledesignfinalterm.BookingCar.CarBookingActivity;

public class CarDetailActivity extends AppCompatActivity {
    private ImageView mainCarImageView;
    private ImageView thumbnailImage1, thumbnailImage2, thumbnailImage3;
    private Button buttonRentNow;
    private String carName = ""; // Store car name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_car_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupImageGallery();

        // Apply rounded corners programmatically
        applyRoundedCorners();

        // Get car data from intent if available
        getCarDataFromIntent();
    }

    private void getCarDataFromIntent() {
        if (getIntent().hasExtra("car_name")) {
            carName = getIntent().getStringExtra("car_name");
            Toast.makeText(this, "Viewing details for " + carName, Toast.LENGTH_SHORT).show();
        } else {
            // Default car name if not provided
            carName = "Selected Car";
        }
    }

    private void initializeViews() {
        mainCarImageView = findViewById(R.id.imageViewCarMain);
        thumbnailImage1 = findViewById(R.id.thumbnail1);
        thumbnailImage2 = findViewById(R.id.thumbnail2);
        thumbnailImage3 = findViewById(R.id.thumbnail3);
        buttonRentNow = findViewById(R.id.buttonRentNow);

        // Set up the Rent Now button click listener
        buttonRentNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show loading feedback
                buttonRentNow.setText("Loading...");
                buttonRentNow.setEnabled(false);

                try {
                    // Navigate to CarBookingActivity
                    Intent intent = new Intent(CarDetailActivity.this, CarBookingActivity.class);

                    // Pass car name to booking activity
                    intent.putExtra("car_name", carName);

                    // Add any additional car details you want to pass
                    intent.putExtra("car_type", "Sedan"); // Example - replace with actual data
                    intent.putExtra("car_price", 50.0); // Example - replace with actual price

                    // Start the booking activity
                    startActivity(intent);

                    // Show success message
                    Toast.makeText(CarDetailActivity.this, "Opening booking for " + carName, Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    // Handle any errors
                    Toast.makeText(CarDetailActivity.this, "Error opening booking page: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                } finally {
                    // Reset button state
                    buttonRentNow.setText("Rent Now");
                    buttonRentNow.setEnabled(true);
                }
            }
        });
    }

    private void applyRoundedCorners() {
        // Create rounded corners for all image views programmatically
        float cornerRadius = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                16f, // 16dp corner radius - adjust as needed
                getResources().getDisplayMetrics());

        // Apply to main image
        GradientDrawable mainImageShape = new GradientDrawable();
        mainImageShape.setCornerRadius(cornerRadius);
        mainImageShape.setColor(getResources().getColor(android.R.color.white));
        mainCarImageView.setBackground(mainImageShape);
        mainCarImageView.setClipToOutline(true);

        // Apply to thumbnails
        GradientDrawable thumbnail1Shape = new GradientDrawable();
        thumbnail1Shape.setCornerRadius(cornerRadius / 2);
        thumbnail1Shape.setColor(getResources().getColor(android.R.color.white));
        thumbnailImage1.setBackground(thumbnail1Shape);
        thumbnailImage1.setClipToOutline(true);

        GradientDrawable thumbnail2Shape = new GradientDrawable();
        thumbnail2Shape.setCornerRadius(cornerRadius / 2);
        thumbnail2Shape.setColor(getResources().getColor(android.R.color.white));
        thumbnailImage2.setBackground(thumbnail2Shape);
        thumbnailImage2.setClipToOutline(true);

        GradientDrawable thumbnail3Shape = new GradientDrawable();
        thumbnail3Shape.setCornerRadius(cornerRadius / 2);
        thumbnail3Shape.setColor(getResources().getColor(android.R.color.white));
        thumbnailImage3.setBackground(thumbnail3Shape);
        thumbnailImage3.setClipToOutline(true);
    }

    private void setupImageGallery() {
        // Set click listeners for thumbnail images
        thumbnailImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMainImage(R.drawable.intro_bg_1);
                updateThumbnailSelection(thumbnailImage1);
            }
        });

        thumbnailImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMainImage(R.drawable.intro_bg_2);
                updateThumbnailSelection(thumbnailImage2);
            }
        });

        thumbnailImage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMainImage(R.drawable.intro_bg_3);
                updateThumbnailSelection(thumbnailImage3);
            }
        });

        // Set initial selection
        updateThumbnailSelection(thumbnailImage1);
    }

    private void changeMainImage(int imageResourceId) {
        // Add fade animation when changing image
        mainCarImageView.setAlpha(0.3f);
        mainCarImageView.setImageResource(imageResourceId);
        mainCarImageView.animate()
                .alpha(1.0f)
                .setDuration(200)
                .start();
    }

    private void updateThumbnailSelection(ImageView selectedThumbnail) {
        // Reset all thumbnails to normal state
        resetThumbnailSelection(thumbnailImage1);
        resetThumbnailSelection(thumbnailImage2);
        resetThumbnailSelection(thumbnailImage3);

        // Highlight selected thumbnail
        selectedThumbnail.setAlpha(1.0f);
        selectedThumbnail.setScaleX(1.1f);
        selectedThumbnail.setScaleY(1.1f);
    }

    private void resetThumbnailSelection(ImageView thumbnail) {
        thumbnail.setAlpha(0.7f);
        thumbnail.setScaleX(1.0f);
        thumbnail.setScaleY(1.0f);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reset button state when returning to this activity
        if (buttonRentNow != null) {
            buttonRentNow.setText("Rent Now");
            buttonRentNow.setEnabled(true);
        }
    }
}