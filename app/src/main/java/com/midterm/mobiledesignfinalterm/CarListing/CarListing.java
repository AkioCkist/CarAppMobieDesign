package com.midterm.mobiledesignfinalterm.CarListing;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.midterm.mobiledesignfinalterm.R;
import com.midterm.mobiledesignfinalterm.api.CarApiService;
import com.midterm.mobiledesignfinalterm.api.RetrofitClient;
import com.midterm.mobiledesignfinalterm.authentication.Login;
import com.midterm.mobiledesignfinalterm.models.Car;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CarListing extends AppCompatActivity {

    // UI Components
    private TextView textViewCarCount;
    private EditText editTextSearch;
    private Button buttonDateTime;
    private ImageButton buttonFilter;
    private RecyclerView recyclerViewCars;
    private ImageView imageViewProfile;
    private LinearLayout dropdownMenu;
    private Button btnMyProfile;
    private Button btnMyBooking;
    private Button btnSettings;
    private Button btnSignOut;

    // User information
    private String userPhone;
    private String userName;
    private String userId;
    private List<String> userRoles;
    private boolean isDropdownVisible = false;

    // Car data
    private CarListingAdapter carAdapter;
    private List<Car> carList;
    private List<Car> filteredCarList;

    // Thông tin thời gian và địa điểm
    private String pickupTime;
    private String pickupDate;
    private String dropoffDate;
    private String dropoffTime;
    private String pickupLocation;
    private String dropoffLocation;

    // Filter-related variables
    private String currentBrandFilter = null;
    private String currentVehicleTypeFilter = null;
    private String currentFuelTypeFilter = null;
    private Integer currentSeatsFilter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_car_listing);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get user information from intent
        getUserInfoFromIntent();
        // Initialize views and setup
        initializeViews();
        setupClickListeners();
        setupRecyclerView();
        displayUserInfo();
        updateDateTimeLocationButton();

        // Load car data
        loadCarData();

        // Initial animations
        animateInitialEntrance();

        // Initialize dropdown as hidden
        dropdownMenu.setVisibility(View.GONE);
        dropdownMenu.setAlpha(0f);
        dropdownMenu.setTranslationY(-20f);

        // Check for filters passed in the intent
        checkIntentForFilters();

        // Show welcome message
        if (userName != null && !userName.isEmpty()) {
            Toast.makeText(this, "Welcome to Car Listing, " + userName + "!", Toast.LENGTH_SHORT).show();
        }
    }

    private void getUserInfoFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            userPhone = intent.getStringExtra("user_phone");
            userName = intent.getStringExtra("user_name");
            userRoles = intent.getStringArrayListExtra("user_roles");
            userId = intent.getStringExtra("user_id"); // Changed from account_id to user_id to match Homepage
            pickupLocation = intent.getStringExtra("pickup_location");
            dropoffLocation = intent.getStringExtra("dropoff_location");
            pickupDate = intent.getStringExtra("pickup_date");
            pickupTime = intent.getStringExtra("pickup_time");
            dropoffDate = intent.getStringExtra("dropoff_date");
            dropoffTime = intent.getStringExtra("dropoff_time");

            // Check for filter parameters
            if (intent.hasExtra("selected_brand")) {
                currentBrandFilter = intent.getStringExtra("selected_brand");
                Log.d("CarListing", "Received brand filter: " + currentBrandFilter);
            }
        }
    }

    /**
     * Check for any filters passed in the intent and apply them
     */
    private void checkIntentForFilters() {
        // Apply location filter if provided
        if (pickupLocation != null && !pickupLocation.isEmpty()) {
            handleFilterByLocation();
        }

        // Apply brand filter if provided
        if (currentBrandFilter != null && !currentBrandFilter.isEmpty()) {
            // Wait a short moment to ensure car data is loaded
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                applyFilters();
                Toast.makeText(this, "Showing " + currentBrandFilter + " cars", Toast.LENGTH_SHORT).show();
            }, 500);
        }
    }

    private void initializeViews() {
        // Removed profile-related views as they are no longer in the layout
        textViewCarCount = findViewById(R.id.textViewCarCount);
        editTextSearch = findViewById(R.id.editTextSearch);
        buttonDateTime = findViewById(R.id.buttonDateTime);
        buttonFilter = findViewById(R.id.btn_Filter);
        recyclerViewCars = findViewById(R.id.recyclerViewCars);

        // Profile and dropdown related views might no longer be needed but we'll keep references
        // for now and handle null checks
        dropdownMenu = findViewById(R.id.dropdownMenu);
        btnMyProfile = findViewById(R.id.btn_MyProfile);
        btnMyBooking = findViewById(R.id.btn_MyBooking);
        btnSettings = findViewById(R.id.btn_Settings);
        btnSignOut = findViewById(R.id.btn_SignOut);
    }

    private void updateDateTimeLocationButton() {
        String formattedText = formatDateTimeLocationText(
                pickupLocation, dropoffLocation,
                pickupTime, pickupDate,
                dropoffTime, dropoffDate);
        buttonDateTime.setText(formattedText);
    }

    private String formatDateTimeLocationText(
            String pickupLocation, String dropoffLocation,
            String pickupTime, String pickupDate,
            String dropoffTime, String dropoffDate) {

        StringBuilder sb = new StringBuilder();

        // First line: Pickup Location --- Dropoff Location
        if (pickupLocation != null && dropoffLocation != null) {
            sb.append(pickupLocation)
                    .append(" --- ")
                    .append(dropoffLocation);
        } else {
            sb.append("Location not specified");
        }

        // Add newline
        sb.append("\n");

        // Second line: Pickup Time, Pickup Date --- Dropoff Time, Dropoff Date
        if (pickupTime != null && pickupDate != null && dropoffTime != null && dropoffDate != null) {
            try {
                // Convert from stored format (dd/MM/yyyy) to display format (dd MMMM yyyy)
                java.text.SimpleDateFormat storedFormat = new java.text.SimpleDateFormat("dd/MM/yyyy");
                java.text.SimpleDateFormat displayFormat = new java.text.SimpleDateFormat("dd MMMM yyyy");

                // Parse and format the pickup date
                java.util.Date pickupDateObj = storedFormat.parse(pickupDate);
                String pickupDateFormatted = displayFormat.format(pickupDateObj);

                // Parse and format the dropoff date
                java.util.Date dropoffDateObj = storedFormat.parse(dropoffDate);
                String dropoffDateFormatted = displayFormat.format(dropoffDateObj);

                sb.append(pickupTime)
                        .append(", ")
                        .append(pickupDateFormatted)
                        .append(" --- ")
                        .append(dropoffTime)
                        .append(", ")
                        .append(dropoffDateFormatted);
            } catch (Exception e) {
                // Fallback to original format if parsing fails
                Log.e("CarListing", "Error converting date format: " + e.getMessage());

                sb.append(pickupTime)
                        .append(", ")
                        .append(pickupDate)
                        .append(" --- ")
                        .append(dropoffTime)
                        .append(", ")
                        .append(dropoffDate);
            }
        } else {
            sb.append("Time and date not specified");
        }

        return sb.toString();
    }

    private void setupClickListeners() {
        // Back arrow functionality
        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to previous screen
                finish();
            }
        });

        // Search functionality with debounce handler
        android.os.Handler searchHandler = new android.os.Handler(android.os.Looper.getMainLooper());
        Runnable searchRunnable = new Runnable() {
            @Override
            public void run() {
                String query = editTextSearch.getText().toString().trim();
                if (!query.isEmpty()) {
                    performSearch(query);
                } else {
                    // Reset to original list if search is cleared
                    filteredCarList.clear();
                    filteredCarList.addAll(carList);
                    carAdapter.notifyDataSetChanged();
                    textViewCarCount.setText("Found " + filteredCarList.size() + " cars");
                }
            }
        };

        // Add TextWatcher to handle text changes with debounce
        editTextSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not used
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                // Remove any pending searches
                searchHandler.removeCallbacks(searchRunnable);
                // Schedule a new search after 500ms (0.5 seconds)
                searchHandler.postDelayed(searchRunnable, 500);
            }
        });

        // Search functionality
        editTextSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    animateEditTextFocus(v, true);
                    if (isDropdownVisible) {
                        hideDropdownMenu();
                    }
                } else {
                    animateEditTextFocus(v, false);
                }
            }
        });

        // Date/Time/Location button
        buttonDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CarListing", "DEBUG: Filtering by location: '" + pickupLocation + "'");
                animateButtonClick(v);
                showDateTimeLocationDialog();
            }
        });

        // Filter button
        buttonFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(v);
                handleFilterOptions();
            }
        });

        // Dropdown menu items
        btnMyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateMenuItemClick(v);
                hideDropdownMenu();
                handleMyProfile();
            }
        });

        btnMyBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateMenuItemClick(v);
                hideDropdownMenu();
                handleMyBooking();
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateMenuItemClick(v);
                hideDropdownMenu();
                handleSettings();
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateMenuItemClick(v);
                hideDropdownMenu();
                handleSignOut();
            }
        });
    }

    private void setupRecyclerView() {
        recyclerViewCars.setLayoutManager(new LinearLayoutManager(this));
        carList = new ArrayList<>();
        filteredCarList = new ArrayList<>();

        carAdapter = new CarListingAdapter(filteredCarList, new CarListingAdapter.OnCarItemClickListener() {
            @Override
            public void onRentalClick(Car car) {
                handleRentalNow(car);
            }

            @Override
            public void onFavoriteClick(Car car, int position) {
                handleFavoriteToggle(car, position);
            }
        });

        recyclerViewCars.setAdapter(carAdapter);
    }

    private void displayUserInfo() {
        // We've removed the profile section from the UI, so we no longer
        // need to set the text for textViewUserName and textViewUserPhone

        // Just store the user information for other uses
        String displayName = userName != null ? userName : "User";

        // Still display welcome toast if needed
        if (userName != null && !userName.isEmpty()) {
            Toast.makeText(this, "Welcome, " + userName + "!", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleFilterByLocation() {
        Log.d("CarListing", "DEBUG: Filtering by location: '" + pickupLocation + "'");

        // Clear the filtered list
        filteredCarList.clear();

        // If pickup location is not specified or empty, show all cars
        if (pickupLocation == null || pickupLocation.isEmpty() || "All Locations".equals(pickupLocation) || "Current Location".equals(pickupLocation)) {
            filteredCarList.addAll(carList);
            Log.d("CarListing", "Showing all cars because location is empty or 'All Locations'");
        } else {
            // Filter cars that match the pickup location
            for (Car car : carList) {
                String carLocation = car.getLocation();
                if (carLocation != null && matchesCity(carLocation, pickupLocation)) {
                    filteredCarList.add(car);
                    Log.d("CarListing", "Added car with location: " + carLocation);
                }
            }
        }

        // Update the UI
        carAdapter.notifyDataSetChanged();
        textViewCarCount.setText("Found " + filteredCarList.size() + " cars");

        // Show a message if no cars found with the selected location
        if (filteredCarList.isEmpty() && !carList.isEmpty()) {
            Toast.makeText(this, "No cars available in " + pickupLocation, Toast.LENGTH_SHORT).show();
        }
    }

    // Helper method to match city names
    private boolean matchesCity(String carLocation, String selectedCity) {
        // Handle "Hồ Chí Minh" vs "TP.HCM" special case
        if ((selectedCity.equals("TP.HCM") || selectedCity.equals("Hồ Chí Minh")) &&
                (carLocation.contains("TP.HCM") || carLocation.contains("Hồ Chí Minh"))) {
            return true;
        }

        // Extract city name from car location (format is typically "City - Address")
        String[] parts = carLocation.split(" - ", 2);
        if (parts.length > 0) {
            String cityPart = parts[0].trim();
            return cityPart.equals(selectedCity) ||
                    cityPart.contains(selectedCity) ||
                    selectedCity.contains(cityPart);
        }

        return carLocation.contains(selectedCity) || selectedCity.contains(carLocation);
    }
    private void loadCarData() {
        // Show loading indicator
        View loadingView = findViewById(R.id.loadingView);
        if (loadingView != null) {
            loadingView.setVisibility(View.VISIBLE);
        }

        // Use RetrofitClient to call API
        CarApiService apiService = RetrofitClient.getCarApiService();

        // Call the API to get all cars with status "available"
        apiService.getAllCars(null, null, "available").enqueue(new retrofit2.Callback<Map<String, Object>>() {
            @Override
            public void onResponse(retrofit2.Call<Map<String, Object>> call, retrofit2.Response<Map<String, Object>> response) {
                if (loadingView != null) {
                    loadingView.setVisibility(View.GONE);
                }

                if (response.isSuccessful() && response.body() != null) {
                    try {
                        // Parse the response
                        Map<String, Object> responseMap = response.body();
                        List<Map<String, Object>> records = (List<Map<String, Object>>) responseMap.get("records");

                        // Clear existing data
                        carList.clear();
                        filteredCarList.clear();

                        // Process each car record
                        for (Map<String, Object> carMap : records) {
                            Car car = new Car();

                            // Handle integer fields safely
                            if (carMap.get("id") instanceof Number) {
                                car.setVehicleId(((Number) carMap.get("id")).intValue());
                            }

                            car.setName((String) carMap.get("name"));

                            // Handle float fields safely
                            if (carMap.get("rating") instanceof Number) {
                                car.setRating(((Number) carMap.get("rating")).floatValue());
                            }

                            // Handle integer fields safely
                            if (carMap.get("trips") instanceof Number) {
                                car.setTotalTrips(((Number) carMap.get("trips")).intValue());
                            }

                            car.setLocation((String) carMap.get("location"));
                            car.setTransmission((String) carMap.get("transmission"));

                            // Handle integer fields safely
                            if (carMap.get("seats") instanceof Number) {
                                car.setSeats(((Number) carMap.get("seats")).intValue());
                            }

                            car.setFuelType((String) carMap.get("fuel"));

                            // Handle double fields safely
                            if (carMap.get("base_price") instanceof Number) {
                                car.setBasePrice(((Number) carMap.get("base_price")).doubleValue());
                            }

                            car.setPriceFormatted((String) carMap.get("price_formatted"));
                            car.setVehicleType((String) carMap.get("vehicle_type"));
                            car.setDescription((String) carMap.get("description"));
                            car.setStatus((String) carMap.get("status"));
                            car.setFavorite((Boolean) carMap.get("is_favorite"));
                            car.setFavoriteForUser((Boolean) carMap.get("is_favorite_for_user"));
                            car.setLessorName((String) carMap.get("lessor_name"));
                            car.setPrimaryImage((String) carMap.get("primary_image"));
                            car.setFuel_consumption((String) carMap.get("fuel_consumption"));
                            car.setBrandCar((String) carMap.get("brand"));

                            // Process amenities if available
                            if (carMap.containsKey("amenities") && carMap.get("amenities") instanceof List) {
                                List<Map<String, Object>> amenitiesList = (List<Map<String, Object>>) carMap.get("amenities");
                                List<com.midterm.mobiledesignfinalterm.CarDetail.Amenity> carAmenities = new ArrayList<>();

                                for (Map<String, Object> amenityMap : amenitiesList) {
                                    // Add null check before calling intValue()
                                    int id = 0; // Default value
                                    if (amenityMap.get("id") instanceof Number) {
                                        id = ((Number) amenityMap.get("id")).intValue();
                                    }
                                    String name = (String) amenityMap.get("name");
                                    String icon = (String) amenityMap.get("icon");
                                    String description = (String) amenityMap.get("description");

                                    carAmenities.add(new com.midterm.mobiledesignfinalterm.CarDetail.Amenity(id, name, icon, description));
                                }
                                car.setAmenities(carAmenities);
                            }

                            carList.add(car);
                        }

                        // Update filtered list
                        filteredCarList.addAll(carList);

                        // Update UI
                        runOnUiThread(() -> {
                            carAdapter.notifyDataSetChanged();
                            textViewCarCount.setText("Found " + filteredCarList.size() + " cars");

                            // Apply location filter if needed
                            if (pickupLocation != null && !pickupLocation.isEmpty()) {
                                handleFilterByLocation();
                            }
                        });

                    } catch (Exception e) {
                        Log.e("CarListing", "Error parsing response: " + e.getMessage(), e);
                        Toast.makeText(CarListing.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CarListing.this, "Failed to fetch car data", Toast.LENGTH_SHORT).show();
                    Log.e("CarListing", "API response error: " + response.code());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Map<String, Object>> call, Throwable t) {
                if (loadingView != null) {
                    loadingView.setVisibility(View.GONE);
                }
                Toast.makeText(CarListing.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("CarListing", "API call failed", t);
            }
        });
    }



    // Click handlers
    private void handleDateTimeLocationSetting() {
        // Show custom dialog for selecting location and time
        showDateTimeLocationDialog();
    }

    private void showDateTimeLocationDialog() {
        // Create a custom dialog view
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_datetime_location, null);
        builder.setView(dialogView);

        // Location spinners
        android.widget.Spinner spinnerPickupLocation = dialogView.findViewById(R.id.spinnerPickupLocation);
        android.widget.Spinner spinnerReturnLocation = dialogView.findViewById(R.id.spinnerReturnLocation);

        // Time buttons
        Button btnPickupTime = dialogView.findViewById(R.id.btnPickupTime);
        Button btnReturnTime = dialogView.findViewById(R.id.btnReturnTime);

        // Confirm button
        Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        // Change text color for buttons
        btnPickupTime.setTextColor(Color.WHITE);
        btnReturnTime.setTextColor(Color.WHITE);
        btnConfirm.setTextColor(Color.WHITE);
        btnCancel.setTextColor(Color.WHITE);

        // Setup location options with custom adapter for black text
        // Add "All Locations" as the first option
        String[] locations = {"All Locations", "Đà Nẵng", "TP.HCM", "Hà Nội"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.item_spinner_location,
                locations
        ) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(android.graphics.Color.BLACK);
                return textView;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                textView.setTextColor(android.graphics.Color.BLACK);
                return textView;
            }
        };

        spinnerPickupLocation.setAdapter(adapter);
        spinnerReturnLocation.setAdapter(adapter);

        // Store the current location to compare later
        String previousPickupLocation = pickupLocation;

        // Set current selection if available
        if (pickupLocation != null && !pickupLocation.isEmpty() && !"All Locations".equals(pickupLocation)) {
            int pickupIndex = findLocationIndex(locations, pickupLocation);
            if (pickupIndex != -1) {
                spinnerPickupLocation.setSelection(pickupIndex);
            }
        } else {
            // Select "All Locations" by default
            spinnerPickupLocation.setSelection(0);
        }

        if (dropoffLocation != null && !dropoffLocation.isEmpty() && !"All Locations".equals(dropoffLocation)) {
            int dropoffIndex = findLocationIndex(locations, dropoffLocation);
            if (dropoffIndex != -1) {
                spinnerReturnLocation.setSelection(dropoffIndex);
            }
        } else {
            // Default dropoff to pickup if not set or set to all locations
            spinnerReturnLocation.setSelection(spinnerPickupLocation.getSelectedItemPosition());
        }

        // Set current time and date for pickup and return buttons
        if (pickupTime != null && pickupDate != null) {
            btnPickupTime.setText(pickupTime + " - " + pickupDate);
        } else {
            btnPickupTime.setText("Select Time");
        }

        if (dropoffTime != null && dropoffDate != null) {
            btnReturnTime.setText(dropoffTime + " - " + dropoffDate);
        } else {
            btnReturnTime.setText("Select Time");
        }

        // Create dialog first so we can reference it
        android.app.AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Store dialog reference in button tags for later access
        btnPickupTime.setTag(dialog);
        btnReturnTime.setTag(dialog);

        // Time pickers
        btnPickupTime.setOnClickListener(v -> showDateTimePicker(btnPickupTime));
        btnReturnTime.setOnClickListener(v -> showDateTimePicker(btnReturnTime));

        // Instead of using CheckBox, we'll use a simple check for same location
        boolean sameLocation = (pickupLocation != null &&
                dropoffLocation != null &&
                pickupLocation.equals(dropoffLocation));

        btnConfirm.setOnClickListener(v -> {
            // Get selected pickup location
            pickupLocation = spinnerPickupLocation.getSelectedItem().toString();

            // Get return location (we don't have a checkbox, so just get the selected item)
            dropoffLocation = spinnerReturnLocation.getSelectedItem().toString();

            // Get the date/time values from the buttons
            String pickupTimeText = btnPickupTime.getText().toString();
            String returnTimeText = btnReturnTime.getText().toString();

            // Parse the date/time values if they're in the expected format
            if (!pickupTimeText.equals("Select Time") && pickupTimeText.contains(" - ")) {
                try {
                    // Expected format is "HH:mm - dd/MM/yyyy"
                    String[] parts = pickupTimeText.split(" - ", 2);
                    if (parts.length == 2) {
                        pickupTime = parts[0]; // HH:mm
                        pickupDate = parts[1]; // dd/MM/yyyy
                    }
                } catch (Exception e) {
                    Log.e("CarListing", "Error parsing pickup time/date: " + e.getMessage());
                }
            }

            if (!returnTimeText.equals("Select Time") && returnTimeText.contains(" - ")) {
                try {
                    // Expected format is "HH:mm - dd/MM/yyyy"
                    String[] parts = returnTimeText.split(" - ", 2);
                    if (parts.length == 2) {
                        dropoffTime = parts[0]; // HH:mm
                        dropoffDate = parts[1]; // dd/MM/yyyy
                    }
                } catch (Exception e) {
                    Log.e("CarListing", "Error parsing return time/date: " + e.getMessage());
                }
            }

            // Update the button text
            updateDateTimeLocationButton();

            // Apply the location filter if location changed
            if (!pickupLocation.equals(previousPickupLocation)) {
                handleFilterByLocation();
            }

            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    // Helper method to find location index in array
    private int findLocationIndex(String[] locations, String location) {
        if (location == null) return -1;

        // Handle different location formats
        String normalizedLocation = location;
        if (location.contains(" - ")) {
            normalizedLocation = location.split(" - ")[0]; // Extract city name before dash
        }

        for (int i = 0; i < locations.length; i++) {
            if (locations[i].equals(normalizedLocation) ||
                    normalizedLocation.contains(locations[i]) ||
                    locations[i].contains(normalizedLocation)) {
                return i;
            }
        }
        return -1;
    }

    private void showDateTimePicker(Button targetButton) {
        // Parse current value
        String current = targetButton.getText().toString();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm dd/MM/yyyy");
        java.util.Calendar calendar = java.util.Calendar.getInstance();

        // Try to parse the current button text if it has the correct format
        try {
            calendar.setTime(sdf.parse(current));
        } catch (Exception e) {
            // If parsing fails, set calendar to current time
            // Also try to use the existing date/time values from the class variables
            if (targetButton.getId() == R.id.btnPickupTime && pickupTime != null && pickupDate != null) {
                try {
                    calendar.setTime(sdf.parse(pickupTime + " " + pickupDate));
                } catch (Exception ex) {
                    // Just use current time if this also fails
                }
            } else if (targetButton.getId() == R.id.btnReturnTime && dropoffTime != null && dropoffDate != null) {
                try {
                    calendar.setTime(sdf.parse(dropoffTime + " " + dropoffDate));
                } catch (Exception ex) {
                    // Just use current time if this also fails
                }
            }
        }

        // Directly show Material date picker for the button that was clicked
        showMaterialDatePicker(targetButton, calendar);
    }
    private void showMaterialDatePicker(Button targetButton, java.util.Calendar calendar) {
        // Create constraints to limit date selection
        // Setting minimum date to today to prevent selecting past dates
        java.util.Calendar minDate = java.util.Calendar.getInstance();

        // Create the date picker builder
        com.google.android.material.datepicker.MaterialDatePicker.Builder<Long> builder =
                com.google.android.material.datepicker.MaterialDatePicker.Builder.datePicker();
        if (targetButton.getId() == R.id.btnPickupTime) {
            builder.setTitleText("Select Pickup Date");
        } else {
            builder.setTitleText("Select Return Date");
        }
        builder.setSelection(calendar.getTimeInMillis());
        builder.setCalendarConstraints(new com.google.android.material.datepicker.CalendarConstraints.Builder()
                .setValidator(com.google.android.material.datepicker.DateValidatorPointForward.from(minDate.getTimeInMillis()))
                .build());

        // Apply custom dark theme
        builder.setTheme(R.style.CustomMaterialCalendar);

        // Create and customize the date picker
        com.google.android.material.datepicker.MaterialDatePicker<Long> datePicker = builder.build();

        // Set up the positive button click listener
        datePicker.addOnPositiveButtonClickListener(selection -> {
            calendar.setTimeInMillis(selection);

            // After date is selected, show time picker for the SAME button that was clicked
            showMaterialTimePicker(targetButton, calendar);
        });
        // Show the date picker with a subtle animation
        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }

    private void showMaterialTimePicker(Button targetButton, java.util.Calendar calendar) {
        int hour = calendar.get(java.util.Calendar.HOUR_OF_DAY);
        int minute = calendar.get(java.util.Calendar.MINUTE);

        // Create the Material TimePicker
        com.google.android.material.timepicker.MaterialTimePicker.Builder builder =
                new com.google.android.material.timepicker.MaterialTimePicker.Builder()
                        .setTimeFormat(com.google.android.material.timepicker.TimeFormat.CLOCK_24H)
                        .setHour(hour)
                        .setMinute(minute)
                        .setTheme(R.style.CustomMaterialTimePicker); // Apply custom dark theme
        // Set different title based on which button was clicked
        if (targetButton.getId() == R.id.btnPickupTime) {
            builder.setTitleText("Select Pickup Time");
        } else {
            builder.setTitleText("Select Return Time");
        }
        com.google.android.material.timepicker.MaterialTimePicker timePicker = builder.build();

        // Set up listeners for the time picker
        timePicker.addOnPositiveButtonClickListener(view -> {
            calendar.set(java.util.Calendar.HOUR_OF_DAY, timePicker.getHour());
            calendar.set(java.util.Calendar.MINUTE, timePicker.getMinute());

            // Format time portion (HH:mm)
            java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("HH:mm");
            // Format date portion with month as text (dd MMMM yyyy), e.g. "15 June 2023"
            java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd MMMM yyyy");

            // Combine them with the desired format "HH:mm - dd MMMM yyyy" (e.g. "14:30 - 15 June 2023")
            String formattedDateTime = timeFormat.format(calendar.getTime()) + " - " + dateFormat.format(calendar.getTime());
            targetButton.setText(formattedDateTime);

            // Check if this is pickup time and adjust dropoff if needed
            Button btnReturnTime = ((android.app.AlertDialog)targetButton.getTag()).findViewById(R.id.btnReturnTime);

            if (targetButton.getId() == R.id.btnPickupTime && btnReturnTime != null) {
                String returnTimeStr = btnReturnTime.getText().toString();
                java.util.Calendar returnCal = java.util.Calendar.getInstance();

                if (returnTimeStr.equals("Select Time")) {
                    // If return time is not set yet, set it to pickup time + 1 hour
                    returnCal.setTimeInMillis(calendar.getTimeInMillis());
                    returnCal.add(java.util.Calendar.HOUR_OF_DAY, 1);
                    btnReturnTime.setText(timeFormat.format(returnCal.getTime()) + " - " + dateFormat.format(returnCal.getTime()));
                    Toast.makeText(CarListing.this, "Return time set to 1 hour after pickup", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        // Try to parse the return time using the new format with text month
                        if (returnTimeStr.contains(" - ")) {
                            String[] parts = returnTimeStr.split(" - ");

                            if (parts.length == 2) {
                                java.text.SimpleDateFormat fullFormat = new java.text.SimpleDateFormat("HH:mm dd MMMM yyyy");

                                try {
                                    // First try parsing with the new format (with text month)
                                    returnCal.setTime(fullFormat.parse(parts[0] + " " + parts[1]));
                                } catch (Exception e) {
                                    // If parsing fails, check if the old format was used
                                    try {
                                        java.text.SimpleDateFormat oldFormat = new java.text.SimpleDateFormat("HH:mm dd/MM/yyyy");
                                        returnCal.setTime(oldFormat.parse(parts[0] + " " + parts[1]));
                                    } catch (Exception ex) {
                                        // If both formats fail, just continue with current calendar
                                        Log.e("CarListing", "Error parsing return date: " + ex.getMessage());
                                    }
                                }

                                // If return date/time is before pickup date/time, adjust it
                                if (returnCal.before(calendar)) {
                                    // Set return time to 1 hour after pickup
                                    returnCal.setTimeInMillis(calendar.getTimeInMillis());
                                    returnCal.add(java.util.Calendar.HOUR_OF_DAY, 1);
                                    btnReturnTime.setText(timeFormat.format(returnCal.getTime()) + " - " + dateFormat.format(returnCal.getTime()));
                                    Toast.makeText(CarListing.this, "Return time adjusted to be after pickup time", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e("CarListing", "Error parsing return time: " + e.getMessage());
                    }
                }
            }

            // Update the class variables based on which button was clicked
            if (targetButton.getId() == R.id.btnPickupTime) {
                pickupTime = timeFormat.format(calendar.getTime());
                // Store the date in MM/dd/yyyy format to maintain compatibility
                pickupDate = new java.text.SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
            } else {
                dropoffTime = timeFormat.format(calendar.getTime());
                // Store the date in MM/dd/yyyy format to maintain compatibility
                dropoffDate = new java.text.SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
            }
        });
        // Show the time picker
        timePicker.show(getSupportFragmentManager(), "TIME_PICKER");
    }
    private boolean isSameDay(java.util.Calendar cal1, java.util.Calendar cal2) {
        return cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
                cal1.get(java.util.Calendar.DAY_OF_YEAR) == cal2.get(java.util.Calendar.DAY_OF_YEAR);
    }
    private void handleFilterOptions() {
        // Tạo và hiển thị dialog filter
        FilterDialog filterDialog = new FilterDialog(this);

        // Thiết lập các giá trị filter ban đầu nếu đã được áp dụng
        filterDialog.setInitialFilters(currentBrandFilter, currentVehicleTypeFilter, currentFuelTypeFilter, currentSeatsFilter);

        // Thiết lập listener khi áp dụng filter
        filterDialog.setListener(new FilterDialog.FilterDialogListener() {
            @Override
            public void onFiltersApplied(String brand, String vehicleType, String fuelType, Integer seats) {
                // Lưu các giá trị filter đã chọn
                currentBrandFilter = brand;
                currentVehicleTypeFilter = vehicleType;
                currentFuelTypeFilter = fuelType;
                currentSeatsFilter = seats;

                // Áp dụng filter
                applyFilters();
            }
        });

        // Hiển thị dialog
        filterDialog.show();
    }

    private void handleRentalNow(Car car) {
        // Navigate to Car Detail activity
        Toast.makeText(this, "Opening details for " + car.getName(), Toast.LENGTH_SHORT).show();

        // Create intent and pass car data to CarDetailActivity
        Intent intent = new Intent(CarListing.this, com.midterm.mobiledesignfinalterm.CarDetail.CarDetailActivity.class);
        intent.putExtra("car_id", car.getVehicleId());

        // Pass user information
        intent.putExtra("user_id", userId);
        intent.putExtra("user_phone", userPhone);
        intent.putExtra("user_name", userName);

        // Pass booking details
        intent.putExtra("pickup_time", pickupTime);
        intent.putExtra("pickup_date", pickupDate);
        intent.putExtra("dropoff_time", dropoffTime);
        intent.putExtra("dropoff_date", dropoffDate);
        intent.putExtra("pickup_location", pickupLocation);
        intent.putExtra("dropoff_location", dropoffLocation);

        startActivity(intent);
    }

    private void handleFavoriteToggle(Car car, int position) {
        // Check if user is logged in (userId is required)
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "Please log in to add favorites", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading indicator
        View loadingView = findViewById(R.id.loadingView);
        if (loadingView != null) {
            loadingView.setVisibility(View.VISIBLE);
        }

        // Prepare the request body
        java.util.HashMap<String, Object> requestBody = new java.util.HashMap<>();
        requestBody.put("account_id", Long.parseLong(userId));
        requestBody.put("vehicle_id", car.getVehicleId());

        Log.d("FavoriteToggle", "Toggling favorite for car ID: " + car.getVehicleId() +
                              ", account ID: " + userId +
                              ", current status: " + car.isFavorite());

        // Make API call to toggle favorite
        com.midterm.mobiledesignfinalterm.api.FavoriteApiService apiService =
                com.midterm.mobiledesignfinalterm.api.RetrofitClient.getFavoriteApiService();

        apiService.toggleFavorite(requestBody).enqueue(new retrofit2.Callback<Map<String, Object>>() {
            @Override
            public void onResponse(retrofit2.Call<Map<String, Object>> call, retrofit2.Response<Map<String, Object>> response) {
                // Hide loading indicator
                if (loadingView != null) {
                    loadingView.setVisibility(View.GONE);
                }

                if (response.isSuccessful() && response.body() != null) {
                    try {
                        Map<String, Object> responseData = response.body();
                        String status = (String) responseData.get("status");
                        String message = (String) responseData.get("message");
                        Boolean isFavorite = (Boolean) responseData.get("is_favorite");

                        if ("success".equals(status)) {
                            // Update the car's favorite status based on server response
                            car.setFavorite(isFavorite != null ? isFavorite : !car.isFavorite());

                            // Update the UI
                            carAdapter.notifyItemChanged(position);

                            // Show success message
                            Toast.makeText(CarListing.this, message, Toast.LENGTH_SHORT).show();

                            Log.d("FavoriteToggle", "Success: " + message + ", is_favorite: " + car.isFavorite());
                        } else {
                            // Show error message
                            Toast.makeText(CarListing.this, "Failed to update favorite: " + message, Toast.LENGTH_SHORT).show();
                            Log.e("FavoriteToggle", "API error: " + message);
                        }
                    } catch (Exception e) {
                        Toast.makeText(CarListing.this, "Error processing response", Toast.LENGTH_SHORT).show();
                        Log.e("FavoriteToggle", "Error parsing response: " + e.getMessage(), e);
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Toast.makeText(CarListing.this, "Failed to update favorite", Toast.LENGTH_SHORT).show();
                        Log.e("FavoriteToggle", "API response error: " + response.code() + " - " + errorBody);
                    } catch (Exception e) {
                        Log.e("FavoriteToggle", "Error reading error body: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Map<String, Object>> call, Throwable t) {
                // Hide loading indicator
                if (loadingView != null) {
                    loadingView.setVisibility(View.GONE);
                }

                Toast.makeText(CarListing.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("FavoriteToggle", "API call failed", t);
            }
        });
    }

    // Dropdown menu methods
    private void toggleDropdownMenu() {
        if (isDropdownVisible) {
            hideDropdownMenu();
        } else {
            showDropdownMenu();
        }
    }

    private void showDropdownMenu() {
        isDropdownVisible = true;
        dropdownMenu.setVisibility(View.VISIBLE);

        ObjectAnimator alpha = ObjectAnimator.ofFloat(dropdownMenu, "alpha", 0f, 1f);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(dropdownMenu, "translationY", -20f, 0f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(dropdownMenu, "scaleX", 0.8f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(dropdownMenu, "scaleY", 0.8f, 1f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(alpha, translationY, scaleX, scaleY);
        animatorSet.setDuration(300);
        animatorSet.setInterpolator(new OvershootInterpolator(1.2f));
        animatorSet.start();
    }

    private void hideDropdownMenu() {
        isDropdownVisible = false;

        ObjectAnimator alpha = ObjectAnimator.ofFloat(dropdownMenu, "alpha", 1f, 0f);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(dropdownMenu, "translationY", 0f, -20f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(dropdownMenu, "scaleX", 1f, 0.8f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(dropdownMenu, "scaleY", 1f, 0.8f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(alpha, translationY, scaleX, scaleY);
        animatorSet.setDuration(200);

        animatorSet.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                dropdownMenu.setVisibility(View.GONE);
            }
        });

        animatorSet.start();
    }

    // Profile menu handlers
    private void handleMyProfile() {
        Toast.makeText(this, "My Profile - Coming soon", Toast.LENGTH_SHORT).show();
        // TODO: Navigate to profile screen
    }

    private void handleMyBooking() {
        Toast.makeText(this, "My Booking - Coming soon", Toast.LENGTH_SHORT).show();
        // TODO: Navigate to booking screen
    }

    private void handleSettings() {
        Toast.makeText(this, "Settings - Coming soon", Toast.LENGTH_SHORT).show();
        // TODO: Navigate to settings screen
    }

    private void handleSignOut() {
        Intent intent = new Intent(CarListing.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // Animation Methods
    private void animateInitialEntrance() {
        View[] views = {editTextSearch, buttonDateTime, buttonFilter, recyclerViewCars};

        for (int i = 0; i < views.length; i++) {
            View view = views[i];
            view.setAlpha(0f);
            view.setTranslationY(50f);
            view.setScaleX(0.8f);
            view.setScaleY(0.8f);

            view.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(600)
                    .setStartDelay(i * 80)
                    .setInterpolator(new OvershootInterpolator(1.2f))
                    .start();
        }
    }

    private void animateButtonClick(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.95f, 1.1f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.95f, 1.1f, 1f);
        ObjectAnimator elevation = ObjectAnimator.ofFloat(view, "elevation", 4f, 12f, 4f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, elevation);
        animatorSet.setDuration(400);
        animatorSet.start();
    }

    private void animateMenuItemClick(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.95f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.95f, 1f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 1f, 0.8f, 1f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, alpha);
        animatorSet.setDuration(150);
        animatorSet.start();
    }

    private void animateEditTextFocus(View view, boolean hasFocus) {
        if (hasFocus) {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.05f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.05f, 1f);
            ObjectAnimator elevation = ObjectAnimator.ofFloat(view, "elevation", 0f, 8f);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(scaleX, scaleY, elevation);
            animatorSet.setDuration(300);
            animatorSet.setInterpolator(new OvershootInterpolator(1.1f));
            animatorSet.start();
        } else {
            ObjectAnimator elevation = ObjectAnimator.ofFloat(view, "elevation", 8f, 0f);
            elevation.setDuration(200);
            elevation.start();
        }
    }

    @Override
    public void onBackPressed() {
        if (isDropdownVisible) {
            hideDropdownMenu();
        } else {
            super.onBackPressed();
        }
    }

    // Getter methods for time and location (for use in other activities)
    public String getPickupTime() {
        return pickupTime;
    }

    public String getReturnTime() {
        return dropoffTime;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public String getReturnLocation() {
        return dropoffLocation;
    }

    // Setter methods to update time and location
    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
        updateDateTimeLocationButton();
    }

    public void setReturnTime(String returnTime) {
        this.dropoffTime = returnTime;
        updateDateTimeLocationButton();
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
        updateDateTimeLocationButton();
    }

    public void setReturnLocation(String returnLocation) {
        this.dropoffLocation = returnLocation;
        updateDateTimeLocationButton();
    }

    private void performSearch(String query) {
        // Show loading indicator
        View loadingView = findViewById(R.id.loadingView);
        if (loadingView != null) {
            loadingView.setVisibility(View.VISIBLE);
        }

        // Use RetrofitClient to call the search API
        CarApiService apiService = RetrofitClient.getCarApiService();

        // Kiểm tra location - nếu là "Current Location" hoặc "All Locations" thì không gửi tham số location
        String locationParam = null;
        if (pickupLocation != null && !pickupLocation.isEmpty() &&
            !pickupLocation.equals("Current Location") && !pickupLocation.equals("All Locations")) {
            locationParam = pickupLocation;
        }

        // Ghi log để debug
        Log.d("CarListing", "Searching with query: '" + query +
              "', location: '" + (locationParam != null ? locationParam : "null") + "'");

        // Call the searchCars API with appropriate parameters
        apiService.searchCars(query, null, locationParam, "available", null, null, null).enqueue(new retrofit2.Callback<Map<String, Object>>() {
            @Override
            public void onResponse(retrofit2.Call<Map<String, Object>> call, retrofit2.Response<Map<String, Object>> response) {
                if (loadingView != null) {
                    loadingView.setVisibility(View.GONE);
                }

                if (response.isSuccessful() && response.body() != null) {
                    try {
                        // Parse the response
                        Map<String, Object> responseMap = response.body();
                        List<Map<String, Object>> records = (List<Map<String, Object>>) responseMap.get("records");

                        // Clear existing filtered data
                        filteredCarList.clear();

                        if (records != null && !records.isEmpty()) {
                            // Process each car record from search results
                            for (Map<String, Object> carMap : records) {
                                Car car = new Car();

                                // Handle integer fields safely
                                if (carMap.get("id") instanceof Number) {
                                    car.setVehicleId(((Number) carMap.get("id")).intValue());
                                }

                                car.setName((String) carMap.get("name"));

                                // Handle float fields safely
                                if (carMap.get("rating") instanceof Number) {
                                    car.setRating(((Number) carMap.get("rating")).floatValue());
                                }

                                // Handle integer fields safely
                                if (carMap.get("trips") instanceof Number) {
                                    car.setTotalTrips(((Number) carMap.get("trips")).intValue());
                                }

                                car.setLocation((String) carMap.get("location"));
                                car.setTransmission((String) carMap.get("transmission"));

                                // Handle integer fields safely
                                if (carMap.get("seats") instanceof Number) {
                                    car.setSeats(((Number) carMap.get("seats")).intValue());
                                }

                                car.setFuelType((String) carMap.get("fuel"));

                                // Handle double fields safely
                                if (carMap.get("base_price") instanceof Number) {
                                    car.setBasePrice(((Number) carMap.get("base_price")).doubleValue());
                                }

                                car.setPriceFormatted((String) carMap.get("price_formatted"));
                                car.setVehicleType((String) carMap.get("vehicle_type"));
                                car.setDescription((String) carMap.get("description"));
                                car.setStatus((String) carMap.get("status"));
                                car.setFavorite((Boolean) carMap.get("is_favorite"));
                                car.setFavoriteForUser((Boolean) carMap.get("is_favorite_for_user"));
                                car.setLessorName((String) carMap.get("lessor_name"));
                                car.setPrimaryImage((String) carMap.get("primary_image"));
                                car.setFuel_consumption((String) carMap.get("fuel_consumption"));
                                car.setBrandCar((String) carMap.get("brand"));

                                // Process amenities if available
                                if (carMap.containsKey("amenities") && carMap.get("amenities") instanceof List) {
                                    List<Map<String, Object>> amenitiesList = (List<Map<String, Object>>) carMap.get("amenities");
                                    List<com.midterm.mobiledesignfinalterm.CarDetail.Amenity> carAmenities = new ArrayList<>();

                                    for (Map<String, Object> amenityMap : amenitiesList) {
                                        int id = 0;
                                        if (amenityMap.get("id") instanceof Number) {
                                            id = ((Number) amenityMap.get("id")).intValue();
                                        }
                                        String name = (String) amenityMap.get("name");
                                        String icon = (String) amenityMap.get("icon");
                                        String description = (String) amenityMap.get("description");

                                        carAmenities.add(new com.midterm.mobiledesignfinalterm.CarDetail.Amenity(id, name, icon, description));
                                    }
                                    car.setAmenities(carAmenities);
                                }

                                filteredCarList.add(car);
                            }

                            // Update UI
                            runOnUiThread(() -> {
                                carAdapter.notifyDataSetChanged();
                                textViewCarCount.setText("Found " + filteredCarList.size() + " cars");
                            });
                        } else {
                            // No results found
                            runOnUiThread(() -> {
                                carAdapter.notifyDataSetChanged();
                                textViewCarCount.setText("Found 0 cars");
                                Toast.makeText(CarListing.this, "No cars found for '" + query + "'", Toast.LENGTH_SHORT).show();
                            });
                        }

                    } catch (Exception e) {
                        Log.e("CarListing", "Error parsing search response: " + e.getMessage(), e);
                        Toast.makeText(CarListing.this, "Error parsing search data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CarListing.this, "Failed to search for cars", Toast.LENGTH_SHORT).show();
                    Log.e("CarListing", "API search response error: " + response.code());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Map<String, Object>> call, Throwable t) {
                if (loadingView != null) {
                    loadingView.setVisibility(View.GONE);
                }
                Toast.makeText(CarListing.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("CarListing", "API search call failed", t);
            }
        });
    }

    private void applyFilters() {
        // Hiển thị loading indicator
        View loadingView = findViewById(R.id.loadingView);
        if (loadingView != null) {
            loadingView.setVisibility(View.VISIBLE);
        }

        // Sử dụng RetrofitClient để gọi API với các filter
        CarApiService apiService = RetrofitClient.getCarApiService();

        // Lấy location parameter
        String locationParam = null;
        if (pickupLocation != null && !pickupLocation.isEmpty() &&
            !pickupLocation.equals("Current Location") && !pickupLocation.equals("All Locations")) {
            locationParam = pickupLocation;
        }

        // Ghi log các filter đã áp dụng
        Log.d("CarListing", "Đang áp dụng filter: " +
                "Brand: " + (currentBrandFilter != null ? currentBrandFilter : "Tất cả") +
                ", Vehicle Type: " + (currentVehicleTypeFilter != null ? currentVehicleTypeFilter : "Tất cả") +
                ", Fuel Type: " + (currentFuelTypeFilter != null ? currentFuelTypeFilter : "Tất cả") +
                ", Seats: " + (currentSeatsFilter != null ? currentSeatsFilter : "Tất cả") +
                ", Location: " + (locationParam != null ? locationParam : "Tất cả"));

        // Lấy query tìm kiếm hiện tại (nếu có)
        String searchQuery = editTextSearch.getText().toString().trim();

        // Gọi API với tất cả các filter
        apiService.searchCars(
            searchQuery.isEmpty() ? null : searchQuery,
            currentVehicleTypeFilter,
            locationParam,
            "available",
            currentBrandFilter,
            currentSeatsFilter,
            currentFuelTypeFilter
        ).enqueue(new retrofit2.Callback<Map<String, Object>>() {
            @Override
            public void onResponse(retrofit2.Call<Map<String, Object>> call, retrofit2.Response<Map<String, Object>> response) {
                if (loadingView != null) {
                    loadingView.setVisibility(View.GONE);
                }

                if (response.isSuccessful() && response.body() != null) {
                    try {
                        // Parse the response
                        Map<String, Object> responseMap = response.body();
                        List<Map<String, Object>> records = (List<Map<String, Object>>) responseMap.get("records");

                        // Clear existing filtered data
                        filteredCarList.clear();

                        if (records != null && !records.isEmpty()) {
                            // Process each car record from search results
                            for (Map<String, Object> carMap : records) {
                                Car car = new Car();

                                // Extract car data from response
                                if (carMap.get("id") instanceof Number) {
                                    car.setVehicleId(((Number) carMap.get("id")).intValue());
                                }

                                car.setName((String) carMap.get("name"));

                                if (carMap.get("rating") instanceof Number) {
                                    car.setRating(((Number) carMap.get("rating")).floatValue());
                                }

                                if (carMap.get("trips") instanceof Number) {
                                    car.setTotalTrips(((Number) carMap.get("trips")).intValue());
                                }

                                car.setLocation((String) carMap.get("location"));
                                car.setTransmission((String) carMap.get("transmission"));

                                if (carMap.get("seats") instanceof Number) {
                                    car.setSeats(((Number) carMap.get("seats")).intValue());
                                }

                                car.setFuelType((String) carMap.get("fuel"));

                                if (carMap.get("base_price") instanceof Number) {
                                    car.setBasePrice(((Number) carMap.get("base_price")).doubleValue());
                                }

                                car.setPriceFormatted((String) carMap.get("price_formatted"));
                                car.setVehicleType((String) carMap.get("vehicle_type"));
                                car.setDescription((String) carMap.get("description"));
                                car.setStatus((String) carMap.get("status"));
                                car.setFavorite((Boolean) carMap.get("is_favorite"));
                                car.setFavoriteForUser((Boolean) carMap.get("is_favorite_for_user"));
                                car.setLessorName((String) carMap.get("lessor_name"));
                                car.setPrimaryImage((String) carMap.get("primary_image"));
                                car.setBrandCar((String) carMap.get("brand"));

                                // Process amenities if available
                                if (carMap.containsKey("amenities") && carMap.get("amenities") instanceof List) {
                                    List<Map<String, Object>> amenitiesList = (List<Map<String, Object>>) carMap.get("amenities");
                                    List<com.midterm.mobiledesignfinalterm.CarDetail.Amenity> carAmenities = new ArrayList<>();

                                    for (Map<String, Object> amenityMap : amenitiesList) {
                                        int id = 0;
                                        if (amenityMap.get("id") instanceof Number) {
                                            id = ((Number) amenityMap.get("id")).intValue();
                                        }
                                        String name = (String) amenityMap.get("name");
                                        String icon = (String) amenityMap.get("icon");
                                        String description = (String) amenityMap.get("description");

                                        carAmenities.add(new com.midterm.mobiledesignfinalterm.CarDetail.Amenity(id, name, icon, description));
                                    }
                                    car.setAmenities(carAmenities);
                                }

                                filteredCarList.add(car);
                            }

                            // Update UI with filtered results
                            runOnUiThread(() -> {
                                carAdapter.notifyDataSetChanged();
                                textViewCarCount.setText("Found " + filteredCarList.size() + " cars");

                                // Show toast with filter summary if filters are applied
                                showFilterSummaryToast();
                            });
                        } else {
                            // No results found with applied filters
                            runOnUiThread(() -> {
                                carAdapter.notifyDataSetChanged();
                                textViewCarCount.setText("Found 0 cars");
                                Toast.makeText(CarListing.this, "Không tìm thấy xe nào phù hợp với bộ lọc đã chọn", Toast.LENGTH_SHORT).show();
                            });
                        }

                    } catch (Exception e) {
                        Log.e("CarListing", "Error parsing filter response: " + e.getMessage(), e);
                        Toast.makeText(CarListing.this, "Lỗi khi xử lý kết quả lọc", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CarListing.this, "Không thể áp dụng bộ lọc", Toast.LENGTH_SHORT).show();
                    Log.e("CarListing", "API filter response error: " + response.code());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Map<String, Object>> call, Throwable t) {
                if (loadingView != null) {
                    loadingView.setVisibility(View.GONE);
                }
                Toast.makeText(CarListing.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("CarListing", "API filter call failed", t);
            }
        });
    }

    private void showFilterSummaryToast() {
        // Chỉ hiển thị toast nếu có ít nhất một bộ lọc được áp dụng
        if (currentBrandFilter != null || currentVehicleTypeFilter != null || currentFuelTypeFilter != null || currentSeatsFilter != null) {

            StringBuilder filterSummary = new StringBuilder("Bộ lọc đã áp dụng: ");
            boolean hasFilter = false;

            if (currentBrandFilter != null) {
                filterSummary.append("Thương hiệu: ").append(currentBrandFilter);
                hasFilter = true;
            }

            if (currentVehicleTypeFilter != null) {
                if (hasFilter) filterSummary.append(", ");
                filterSummary.append("Loại xe: ").append(currentVehicleTypeFilter);
                hasFilter = true;
            }

            if (currentFuelTypeFilter != null) {
                if (hasFilter) filterSummary.append(", ");
                filterSummary.append("Nhiên liệu: ").append(currentFuelTypeFilter);
                hasFilter = true;
            }

            if (currentSeatsFilter != null) {
                if (hasFilter) filterSummary.append(", ");
                filterSummary.append("Số ghế: ").append(currentSeatsFilter);
            }

            Toast.makeText(this, filterSummary.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
