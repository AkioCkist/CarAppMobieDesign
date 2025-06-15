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

    // Dummy images for thumbnails (we'll replace these with API data if available)
    private final String[] dummyImages = {
            "https://example.com/car1.jpg",
            "https://example.com/car2.jpg",
            "https://example.com/car3.jpg"
    };

    // Booking details
    private String pickupTime;
    private String pickupDate;
    private String dropoffDate;
    private String dropoffTime;
    private String pickupLocation;
    private String dropoffLocation;
    private String userName, userPhone;

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

        // Set initial data from intent
        setInitialData();

        // Set up amenities recyclerview
        setupAmenitiesRecyclerView();

        // Fetch detailed car data from API
        fetchCarDetails();

        // Setup listeners
        setupListeners();
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
        // Handle rent now button click
        buttonRentNow.setOnClickListener(v -> {
            Toast.makeText(this, "Rental request submitted", Toast.LENGTH_SHORT).show();
             Intent intent = new Intent(this, CarBookingActivity.class);
             startActivity(intent);
        });

        // Thumbnail click listeners will be set up after loading images

        // Image navigation buttons
        ImageButton buttonPrevious = findViewById(R.id.buttonPrevious);
        ImageButton buttonNext = findViewById(R.id.buttonNext);

        buttonPrevious.setOnClickListener(v -> {
            // Handle previous image navigation
            Toast.makeText(this, "Previous image", Toast.LENGTH_SHORT).show();
        });

        buttonNext.setOnClickListener(v -> {
            // Handle next image navigation
            Toast.makeText(this, "Next image", Toast.LENGTH_SHORT).show();
        });
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
    private void setInitialData() {
        // Set user information in top bar
        TextView textViewUserName = findViewById(R.id.textViewUserName);
        TextView textViewUserPhone = findViewById(R.id.textViewUserPhone);
        if (userName != null) textViewUserName.setText(userName);
        if (userPhone != null) textViewUserPhone.setText(userPhone);

        // Set basic car info
        if (carName != null) tvCarName.setText(carName);
        if (carFuel != null) tvFuelType.setText(carFuel);
        if (carTransmission != null) tvTransmission.setText(carTransmission);
        if (carSeats != null) tvSeatCount.setText(carSeats);
        if (carConsumption != null) tvConsumption.setText(carConsumption);
        if (carPrice != null) tvPrice.setText(carPrice);

        tvLocation.setText(pickupLocation != null ? pickupLocation : "Hà Nội, Việt Nam");

        // Load main image
        if (carImage != null && !carImage.isEmpty()) {
            Glide.with(this)
                    .load(carImage)
                    .placeholder(R.drawable.intro_bg_1)
                    .error(R.drawable.intro_bg_1)
                    .into(mainCarImageView);
        }

        // Initialize with dummy images for thumbnails
        Glide.with(this).load(R.drawable.intro_bg_1).into(thumbnail1);
        Glide.with(this).load(R.drawable.intro_bg_2).into(thumbnail2);
        Glide.with(this).load(R.drawable.intro_bg_3).into(thumbnail3);
    }
    private void setupAmenitiesRecyclerView() {
        amenityAdapter = new AmenityAdapter(this, new ArrayList<>());
        recyclerViewAmenities.setLayoutManager(new LinearLayoutManager(this));
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

        // Load main image
        if (car.getPrimaryImage() != null && !car.getPrimaryImage().isEmpty()) {
            Glide.with(CarDetailActivity.this)
                    .load(car.getPrimaryImage())
                    .placeholder(R.drawable.intro_bg_1)
                    .error(R.drawable.intro_bg_1)
                    .into(mainCarImageView);
        }

        // Update amenities
        if (car.getAmenities() != null) {
            amenityAdapter.setAmenities(car.getAmenities());
        }
    }
    private void updateCarDetails(Map<String, Object> carMap) {
        // Update car name
        if (carMap.containsKey("name")) {
            tvCarName.setText((String) carMap.get("name"));
        }

        // Update rating
        if (carMap.containsKey("rating")) {
            double rating = ((Number) carMap.get("rating")).doubleValue();
            tvRating.setText(String.format("%.1f", rating));
        }

        // Update trips
        if (carMap.containsKey("trips")) {
            int trips = ((Number) carMap.get("trips")).intValue();
            tvTrips.setText(trips + " Chuyến");
        }

        // Update location
        if (carMap.containsKey("location")) {
            tvLocation.setText((String) carMap.get("location"));
        }

        // Update transmission
        if (carMap.containsKey("transmission")) {
            tvTransmission.setText((String) carMap.get("transmission"));
        }

        // Update fuel type
        if (carMap.containsKey("fuel")) {
            tvFuelType.setText((String) carMap.get("fuel"));
        }

        // Update seats
        if (carMap.containsKey("seats")) {
            int seats = ((Number) carMap.get("seats")).intValue();
            tvSeatCount.setText(seats + " Person");
        }

        // Consumption is set to default
        tvConsumption.setText("7L/100km");

        // Update description
        if (carMap.containsKey("description")) {
            tvDescription.setText((String) carMap.get("description"));
        }

        // Update prices
        if (carMap.containsKey("base_price_formatted")) {
            tvPrice.setText((String) carMap.get("base_price_formatted") + "/day");
        } else if (carMap.containsKey("price_formatted")) {
            tvPrice.setText((String) carMap.get("price_formatted") + "/day");
        }

        if (carMap.containsKey("insurance_price_formatted")) {
            tvInsurancePrice.setText((String) carMap.get("insurance_price_formatted"));
        }

        if (carMap.containsKey("total_price_formatted")) {
            tvTotalPrice.setText((String) carMap.get("total_price_formatted"));
        }

        // Update amenities
        if (carMap.containsKey("amenities") && carMap.get("amenities") instanceof List) {
            List<Map<String, Object>> amenitiesList = (List<Map<String, Object>>) carMap.get("amenities");
            List<Amenity> amenities = new ArrayList<>();

            for (Map<String, Object> amenityMap : amenitiesList) {
                int id = ((Number) amenityMap.get("id")).intValue();
                String name = (String) amenityMap.get("name");
                String icon = (String) amenityMap.get("icon");
                String description = (String) amenityMap.get("description");

                amenities.add(new Amenity(id, name, icon, description));
            }

            amenityAdapter.setAmenities(amenities);
        }

        // Update images
        if (carMap.containsKey("images") && carMap.get("images") instanceof List) {
            List<Map<String, Object>> imagesList = (List<Map<String, Object>>) carMap.get("images");

            if (imagesList.size() > 0) {
                String mainImageUrl = (String) imagesList.get(0).get("url");
                Glide.with(this)
                        .load(mainImageUrl)
                        .placeholder(R.drawable.intro_bg_1)
                        .error(R.drawable.intro_bg_1)
                        .into(mainCarImageView);

                // Load thumbnails if available
                if (imagesList.size() > 0) {
                    Glide.with(this)
                            .load((String) imagesList.get(0).get("url"))
                            .placeholder(R.drawable.intro_bg_1)
                            .into(thumbnail1);
                }

                if (imagesList.size() > 1) {
                    Glide.with(this)
                            .load((String) imagesList.get(1).get("url"))
                            .placeholder(R.drawable.intro_bg_2)
                            .into(thumbnail2);
                }

                if (imagesList.size() > 2) {
                    Glide.with(this)
                            .load((String) imagesList.get(2).get("url"))
                            .placeholder(R.drawable.intro_bg_3)
                            .into(thumbnail3);
                }

                // Set click listeners for thumbnails
                setupThumbnailListeners(imagesList);
            }
        }
    }
    private void setupThumbnailListeners(List<Map<String, Object>> images) {
        if (images.size() > 0) {
            thumbnail1.setOnClickListener(v -> {
                String url = (String) images.get(0).get("url");
                Glide.with(CarDetailActivity.this).load(url).into(mainCarImageView);
            });
        }

        if (images.size() > 1) {
            thumbnail2.setOnClickListener(v -> {
                String url = (String) images.get(1).get("url");
                Glide.with(CarDetailActivity.this).load(url).into(mainCarImageView);
            });
        }

        if (images.size() > 2) {
            thumbnail3.setOnClickListener(v -> {
                String url = (String) images.get(2).get("url");
                Glide.with(CarDetailActivity.this).load(url).into(mainCarImageView);
            });
        }
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
        thumbnail1.setBackground(thumbnail1Shape);
        thumbnail1.setClipToOutline(true);

        GradientDrawable thumbnail2Shape = new GradientDrawable();
        thumbnail2Shape.setCornerRadius(cornerRadius / 2);
        thumbnail2Shape.setColor(getResources().getColor(android.R.color.white));
        thumbnail2.setBackground(thumbnail2Shape);
        thumbnail2.setClipToOutline(true);

        GradientDrawable thumbnail3Shape = new GradientDrawable();
        thumbnail3Shape.setCornerRadius(cornerRadius / 2);
        thumbnail3Shape.setColor(getResources().getColor(android.R.color.white));
        thumbnail3.setBackground(thumbnail3Shape);
        thumbnail3.setClipToOutline(true);
    }

    private void setupImageGallery() {
        // Set click listeners for thumbnail images
        thumbnail1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMainImage(R.drawable.intro_bg_1);
                updateThumbnailSelection(thumbnail1);
            }
        });

        thumbnail2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMainImage(R.drawable.intro_bg_2);
                updateThumbnailSelection(thumbnail2);
            }
        });

        thumbnail3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMainImage(R.drawable.intro_bg_3);
                updateThumbnailSelection(thumbnail3);
            }
        });

        // Set initial selection
        updateThumbnailSelection(thumbnail1);
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
        resetThumbnailSelection(thumbnail1);
        resetThumbnailSelection(thumbnail2);
        resetThumbnailSelection(thumbnail3);

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