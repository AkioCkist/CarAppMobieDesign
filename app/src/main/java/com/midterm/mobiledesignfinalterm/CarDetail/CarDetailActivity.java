package com.midterm.mobiledesignfinalterm.CarDetail;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.midterm.mobiledesignfinalterm.R;
import com.midterm.mobiledesignfinalterm.BookingCar.CarBookingActivity;
import com.midterm.mobiledesignfinalterm.api.CarApiService;
import com.midterm.mobiledesignfinalterm.api.RetrofitClient;
import com.midterm.mobiledesignfinalterm.models.Car;
import com.midterm.mobiledesignfinalterm.models.CarImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CarDetailActivity extends AppCompatActivity {
    // UI Elements
    private ImageView mainCarImageView;
    private ImageView thumbnail1, thumbnail2, thumbnail3;
    private TextView tvCarName, tvRating, tvTrips, tvLocation;
    private TextView tvTransmission, tvFuelType, tvSeatCount, tvConsumption;
    private TextView tvDescription, tvPrice, tvInsurancePrice, tvTotalPrice;
    private Button buttonRentNow;
    private FrameLayout loadingView;
    private RecyclerView recyclerViewAmenities;
    private AmenityAdapter amenityAdapter;
    private int carId;
    private List<Amenity> amenityList;

    // Car details
    private String carName, carType, carFuel, carTransmission, carSeats, carConsumption;
    private String carPrice, carImage;

    // API service
    private CarApiService carApiService;

    private int currentImageIndex = 0;
    private int MAX_IMAGES = 3;
    private ImageView[] thumbnailViews;

    // Booking details
    private String pickupTime;
    private String pickupDate;
    private String dropoffDate;
    private String dropoffTime;
    private String pickupLocation;
    private String dropoffLocation;
    private String userName, userPhone;

    // List to hold image URLs
    private List<String> imageUrls = new ArrayList<>();

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
        carApiService = RetrofitClient.getClient().create(CarApiService.class);
        initializeViews();

        // Get data from intent
        getDataFromIntent();

        // Set up amenities recyclerview
        setupAmenitiesRecyclerView();

        // Fetch detailed car data from API
        fetchCarDetails();

        // Setup listeners
        setupListeners();
    }

    private void initializeViews() {
        mainCarImageView = findViewById(R.id.iv_primaryCarImage);
        thumbnail1 = findViewById(R.id.thumbnail1);
        thumbnail2 = findViewById(R.id.thumbnail2);
        thumbnail3 = findViewById(R.id.thumbnail3);
        buttonRentNow = findViewById(R.id.buttonRentNow);
        // Car info text views
        tvCarName = findViewById(R.id.tv_CarName);
        tvRating = findViewById(R.id.tv_rating);
        tvTrips = findViewById(R.id.tv_trips);
        tvLocation = findViewById(R.id.tv_location);
        tvTransmission = findViewById(R.id.tv_transmission);
        tvFuelType = findViewById(R.id.tv_fuelType);
        tvSeatCount = findViewById(R.id.tv_seatCount);
        tvConsumption = findViewById(R.id.tv_consumption);
        tvDescription = findViewById(R.id.tv_description);

        // Price text views
        tvPrice = findViewById(R.id.tv_price);
        tvInsurancePrice = findViewById(R.id.tv_insurance_price);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        loadingView = findViewById(R.id.loadingView);
        recyclerViewAmenities = findViewById(R.id.recyclerViewAmenities);
        // Amenities recyclerview
        amenityList = new ArrayList<>();
        amenityAdapter = new AmenityAdapter(this, amenityList);
        recyclerViewAmenities.setAdapter(amenityAdapter);
        recyclerViewAmenities.setLayoutManager(new GridLayoutManager(this, 2));
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
    private void setupListeners() {
        // Handle back arrow click
        ImageView backArrow = findViewById(R.id.iv_back);
        backArrow.setOnClickListener(v -> {
            finish(); // Return to previous screen
        });

        // Handle rent now button click
        buttonRentNow.setOnClickListener(v -> {
            Toast.makeText(this, "Rental request submitted", Toast.LENGTH_SHORT).show();
             Intent intent = new Intent(this, CarBookingActivity.class);
             startActivity(intent);
        });

        setupImageGallery();
        // Image navigation buttons
        ImageButton buttonPrevious = findViewById(R.id.buttonPrevious);
        ImageButton buttonNext = findViewById(R.id.buttonNext);
    }

    private void showLoading(boolean show) {
        if (loadingView != null) {
            loadingView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            carId = intent.getIntExtra("car_id", -1);

            // User info
            userName = intent.getStringExtra("user_name");
            userPhone = intent.getStringExtra("user_phone");

            // Booking details
            pickupTime = intent.getStringExtra("pickup_time");
            pickupDate = intent.getStringExtra("pickup_date");
            dropoffTime = intent.getStringExtra("dropoff_time");
            dropoffDate = intent.getStringExtra("dropoff_date");
            pickupLocation = intent.getStringExtra("pickup_location");
            dropoffLocation = intent.getStringExtra("dropoff_location");
        }
    }
    private void setupAmenitiesRecyclerView() {
        amenityAdapter = new AmenityAdapter(this, new ArrayList<>());
        recyclerViewAmenities.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewAmenities.setAdapter(amenityAdapter);
    }
    private void fetchCarDetails() {
        showLoading(true);

        if (carId <= 0) {
            Toast.makeText(this, "Invalid car ID", Toast.LENGTH_SHORT).show();
            showLoading(false);
            return;
        }

        carApiService.getCarDetails(carId).enqueue(new retrofit2.Callback<Car>() {
            @Override
            public void onResponse(retrofit2.Call<Car> call, retrofit2.Response<Car> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    Car car = response.body();
                    updateCarDetailsFromCarObject(car);
                } else {
                    Toast.makeText(CarDetailActivity.this,
                            "Failed to get car details", Toast.LENGTH_SHORT).show();
                    Log.e("CarDetailActivity", "API response error: " + response.code());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Car> call, Throwable t) {
                showLoading(false);
                Toast.makeText(CarDetailActivity.this,
                        "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("CarDetailActivity", "API call failed", t);
            }
        });
    }
    private void updateCarDetailsFromCarObject(Car car) {
        // Update car name
        tvCarName.setText(car.getName());

        // Update rating
        tvRating.setText(String.format("%.1f", car.getRating()));

        // Update trips
        tvTrips.setText(car.getTotalTrips() + " Chuyến");

        // Update location
        tvLocation.setText(car.getLocation());

        // Update transmission
        tvTransmission.setText(car.getTransmission());

        // Update fuel type
        tvFuelType.setText(car.getFuelType());

        // Update seats
        tvSeatCount.setText(car.getSeats() + " Person");

        // Update consumption
        tvConsumption.setText(car.getFuel_consumption());

        // Update description
        tvDescription.setText(car.getDescription());

        // Update price
        tvPrice.setText(car.getPriceFormatted());

        // Calculate insurance price (assuming 10% of base price)
        double basePrice = car.getBasePrice();
        double insurancePrice = basePrice * 0.1;
        tvInsurancePrice.setText(String.format("%,.0f VND", insurancePrice));

        // Calculate total price
        double totalPrice = basePrice + insurancePrice;
        tvTotalPrice.setText(String.format("%,.0f VND", totalPrice));

        // Clear existing image URLs
        imageUrls.clear();

        // Add primary image to our imageUrls list
        if (car.getPrimaryImage() != null && !car.getPrimaryImage().isEmpty()) {
            imageUrls.add(car.getPrimaryImage());
            Log.d("CarDetailActivity", "Primary image added: " + car.getPrimaryImage());
        }

        // Process images array if it exists
        if (car.getImages() != null && !car.getImages().isEmpty()) {
            Log.d("CarDetailActivity", "Images found: " + car.getImages().size());

            // Add images from the images array - avoid duplicating the primary image
            for (CarImage image : car.getImages()) {
                if (image != null && image.getUrl() != null && !image.getUrl().isEmpty()) {
                    // Skip if this URL is already in our list
                    if (!imageUrls.contains(image.getUrl())) {
                        imageUrls.add(image.getUrl());
                        Log.d("CarDetailActivity", "Added image URL: " + image.getUrl());
                    }
                }
            }
        }

        // If we have no images, add a default placeholder
        if (imageUrls.isEmpty()) {
            Log.d("CarDetailActivity", "No images found, using placeholder");
            // Add default image if no images are available
            imageUrls.add(""); // Empty string will trigger the placeholder in Glide
        }

        Log.d("CarDetailActivity", "Total image URLs: " + imageUrls.size());

        // Update MAX_IMAGES based on available images
        int maxImages = Math.min(imageUrls.size(), thumbnailViews.length);

        // Load images into views
        loadImagesIntoViews();

        // Reset selection
        currentImageIndex = 0;
        updateThumbnailSelection();

        // Update amenities
        if (car.getAmenities() != null) {
            // Initialize icon resources for each amenity
            for (Amenity amenity : car.getAmenities()) {
                amenity.initializeIconResource(this);
            }
            amenityAdapter.setAmenities(car.getAmenities());
        }
    }

    private void loadImagesIntoViews() {
        // Load main image (first image or default)
        if (!imageUrls.isEmpty()) {
            Log.d("CarDetailActivity", "Loading main image: " + imageUrls.get(0));
            Glide.with(this)
                .load(imageUrls.get(0))
                .placeholder(R.drawable.intro_bg_1)
                .error(R.drawable.intro_bg_1)
                .into(mainCarImageView);
        }

        // Load thumbnails
        for (int i = 0; i < MAX_IMAGES; i++) {
            if (i < imageUrls.size()) {
                Log.d("CarDetailActivity", "Loading thumbnail " + i + ": " + imageUrls.get(i));
                final int index = i;
                Glide.with(this)
                    .load(imageUrls.get(i))
                    .placeholder(R.drawable.intro_bg_1)
                    .error(R.drawable.intro_bg_1)
                    .into(thumbnailViews[i]);

                // Update click listener to use the URL
                thumbnailViews[i].setOnClickListener(v -> {
                    currentImageIndex = index;
                    updateMainImage();
                    updateThumbnailSelection();
                });
            }
        }
    }
    private void setupImageGallery() {
        // Initialize the thumbnail array
        thumbnailViews = new ImageView[]{thumbnail1, thumbnail2, thumbnail3};

        // Set click listeners for thumbnail images
        for (int i = 0; i < thumbnailViews.length; i++) {
            final int index = i;
            thumbnailViews[i].setOnClickListener(v -> {
                currentImageIndex = index;
                updateMainImage();
                updateThumbnailSelection();
            });
        }

        // Set up navigation buttons
        ImageButton buttonPrevious = findViewById(R.id.buttonPrevious);
        ImageButton buttonNext = findViewById(R.id.buttonNext);

        buttonPrevious.setOnClickListener(v -> {
            // Modify for infinite scrolling - go to last image when at the beginning
            if (currentImageIndex > 0) {
                currentImageIndex--;
            } else {
                // Jump to the last image when at the first image
                currentImageIndex = MAX_IMAGES - 1;
            }
            updateMainImage();
            updateThumbnailSelection();
        });

        buttonNext.setOnClickListener(v -> {
            // Modify for infinite scrolling - go back to first image when at the end
            if (currentImageIndex < MAX_IMAGES - 1) {
                currentImageIndex++;
            } else {
                // Jump to the first image when at the last image
                currentImageIndex = 0;
            }
            updateMainImage();
            updateThumbnailSelection();
        });

        // Set initial image and selection
        updateMainImage();
        updateThumbnailSelection();
    }
    private void updateMainImage() {
        // Check if we have valid image URLs
        if (imageUrls.isEmpty() || currentImageIndex >= imageUrls.size()) {
            // Use fallback image if no URLs available
            mainCarImageView.setAlpha(0.3f);
            mainCarImageView.setImageResource(R.drawable.intro_bg_1);
            mainCarImageView.animate()
                    .alpha(1.0f)
                    .setDuration(200)
                    .start();
            return;
        }

        // Change main image with animation
        mainCarImageView.setAlpha(0.3f);

        // Load image from URL using Glide
        Glide.with(this)
            .load(imageUrls.get(currentImageIndex))
            .placeholder(R.drawable.intro_bg_1)
            .error(R.drawable.intro_bg_1)
            .into(mainCarImageView);

        mainCarImageView.animate()
            .alpha(1.0f)
            .setDuration(200)
            .start();

        // Log which image is being displayed
        Log.d("CarDetailActivity", "Showing main image: " + imageUrls.get(currentImageIndex));
    }
    private void updateThumbnailSelection() {
        // Reset all thumbnails to normal state
        for (ImageView thumbnail : thumbnailViews) {
            thumbnail.setAlpha(0.7f);
            thumbnail.setScaleX(1.0f);
            thumbnail.setScaleY(1.0f);
        }

        // Highlight current selected thumbnail
        thumbnailViews[currentImageIndex].setAlpha(1.0f);
        thumbnailViews[currentImageIndex].setScaleX(1.1f);
        thumbnailViews[currentImageIndex].setScaleY(1.1f);
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
