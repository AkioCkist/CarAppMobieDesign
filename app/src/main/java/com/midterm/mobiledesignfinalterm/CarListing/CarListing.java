package com.midterm.mobiledesignfinalterm.CarListing;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
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
import android.widget.TextView;
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
import com.midterm.mobiledesignfinalterm.models.ApiResponse;
import com.midterm.mobiledesignfinalterm.models.Car;

import java.util.ArrayList;
import java.util.List;

public class CarListing extends AppCompatActivity {

    // UI Components
    private TextView textViewUserName;
    private TextView textViewUserPhone;
    private TextView textViewCarCount;
    private EditText editTextSearch;
    private Button buttonDateTime;
    private ImageButton buttonFilter;
    private RecyclerView recyclerViewCars;
    private ImageView imageViewProfile;
    private LinearLayout profileSection;
    private LinearLayout dropdownMenu;
    private Button btnMyProfile;
    private Button btnMyBooking;
    private Button btnSettings;
    private Button btnSignOut;

    // User information
    private String userPhone;
    private String userName;
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
        loadCarData();
        // Initial animations
        animateInitialEntrance();
        // Initialize dropdown as hidden
        dropdownMenu.setVisibility(View.GONE);
        dropdownMenu.setAlpha(0f);
        dropdownMenu.setTranslationY(-20f);
        if (pickupLocation != null && !pickupLocation.isEmpty()) {
            // Apply initial location filtering if a location was passed in the intent
            handleFilterByLocation();
        }
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
            pickupLocation = intent.getStringExtra("pickup_location");
            dropoffLocation = intent.getStringExtra("dropoff_location");
            pickupDate = intent.getStringExtra("pickup_date");
            pickupTime = intent.getStringExtra("pickup_time");
            dropoffDate = intent.getStringExtra("dropoff_date");
            dropoffTime = intent.getStringExtra("dropoff_time");
        }
    }

    private void initializeViews() {
        // Removed textViewLocation and textViewDateTime as they don't exist in XML
        textViewUserName = findViewById(R.id.textViewUserName);
        textViewUserPhone = findViewById(R.id.textViewUserPhone);
        textViewCarCount = findViewById(R.id.textViewCarCount);
        editTextSearch = findViewById(R.id.editTextSearch);
        buttonDateTime = findViewById(R.id.buttonDateTime);
        buttonFilter = findViewById(R.id.btn_Filter);
        recyclerViewCars = findViewById(R.id.recyclerViewCars);
        imageViewProfile = findViewById(R.id.imageViewProfile);
        profileSection = findViewById(R.id.profileSection);
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
            sb.append(pickupTime)
                    .append(", ")
                    .append(pickupDate)
                    .append(" --- ")
                    .append(dropoffTime)
                    .append(", ")
                    .append(dropoffDate);
        } else {
            sb.append("Time and date not specified");
        }

        return sb.toString();
    }

    private void setupClickListeners() {
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

        // Profile section
        profileSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateProfileClick(v);
                toggleDropdownMenu();
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
        if (userPhone != null) {
            String displayName = userName != null ? userName : "User";
            textViewUserName.setText(displayName);
            textViewUserPhone.setText(userPhone);
        } else {
            textViewUserName.setText("User");
            textViewUserPhone.setText("Phone not available");
        }
    }

    private void handleFilterByLocation() {
        Log.d("CarListing", "DEBUG: Filtering by location: '" + pickupLocation + "'");

        // Clear the filtered list
        filteredCarList.clear();

        // If pickup location is not specified, show all cars
        if (pickupLocation == null || pickupLocation.isEmpty()) {
            filteredCarList.addAll(carList);
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

        // Show a message if no cars found
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
        apiService.getAllCars().enqueue(new retrofit2.Callback<ApiResponse<List<Car>>>() {
            @Override
            public void onResponse(retrofit2.Call<ApiResponse<List<Car>>> call, retrofit2.Response<ApiResponse<List<Car>>> response) {
                if (loadingView != null) {
                    loadingView.setVisibility(View.GONE);
                }

                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    // Success - get cars from response
                    List<Car> fetchedCars = response.body().getData();
                    carList.clear();
                    carList.addAll(fetchedCars);

                    // Update filtered list and adapter
                    filteredCarList.clear();
                    filteredCarList.addAll(carList);
                    carAdapter.notifyDataSetChanged();

                    // Update car count
                    textViewCarCount.setText("Found " + filteredCarList.size() + " cars");
                } else {
                    // Error handling
                    Toast.makeText(CarListing.this, "Failed to load cars", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ApiResponse<List<Car>>> call, Throwable t) {
                if (loadingView != null) {
                    loadingView.setVisibility(View.GONE);
                }
                Toast.makeText(CarListing.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                t.printStackTrace();
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
        btnPickupTime.setTextColor(android.graphics.Color.BLACK);
        btnReturnTime.setTextColor(android.graphics.Color.BLACK);
        btnConfirm.setTextColor(android.graphics.Color.BLACK);
        btnCancel.setTextColor(android.graphics.Color.BLACK);

        // Setup location options with custom adapter for black text
        String[] locations = {"Đà Nẵng", "TP.HCM", "Hà Nội", "Huế", "Bắc Ninh"};
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
        if (pickupLocation != null && !pickupLocation.isEmpty()) {
            int pickupIndex = findLocationIndex(locations, pickupLocation);
            if (pickupIndex != -1) {
                spinnerPickupLocation.setSelection(pickupIndex);
            }
        }

        if (dropoffLocation != null && !dropoffLocation.isEmpty()) {
            int dropoffIndex = findLocationIndex(locations, dropoffLocation);
            if (dropoffIndex != -1) {
                spinnerReturnLocation.setSelection(dropoffIndex);
            }
        } else if (pickupLocation != null) {
            // Default dropoff to pickup if not set
            int pickupIndex = findLocationIndex(locations, pickupLocation);
            if (pickupIndex != -1) {
                spinnerReturnLocation.setSelection(pickupIndex);
            }
        }

        // Set current time
        btnPickupTime.setText(pickupTime != null ? pickupTime : "Select Time");
        btnReturnTime.setText(dropoffTime != null ? dropoffTime : "Select Time");

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
        try { calendar.setTime(sdf.parse(current)); } catch (Exception ignored) {}
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

            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm dd/MM/yyyy");
            targetButton.setText(sdf.format(calendar.getTime()));

            // Check if this is pickup time and adjust dropoff if needed
            Button btnReturnTime = ((android.app.AlertDialog)targetButton.getTag()).findViewById(R.id.btnReturnTime);

            if (targetButton.getId() == R.id.btnPickupTime && btnReturnTime != null) {
                String returnTimeStr = btnReturnTime.getText().toString();
                java.util.Calendar returnCal = java.util.Calendar.getInstance();
                try {
                    returnCal.setTime(sdf.parse(returnTimeStr));
                    // If return date/time is before pickup date/time, adjust it
                    if (returnCal.before(calendar)) {
                        // Set return time to 1 hour after pickup
                        returnCal.setTimeInMillis(calendar.getTimeInMillis());
                        returnCal.add(java.util.Calendar.HOUR_OF_DAY, 1);
                        btnReturnTime.setText(sdf.format(returnCal.getTime()));
                        Toast.makeText(CarListing.this, "Return time adjusted to be after pickup time", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        // TODO: Open filter options dialog
        Toast.makeText(this, "Filter options - Coming soon", Toast.LENGTH_SHORT).show();
    }

    private void handleRentalNow(Car car) {
        // Navigate to Car Detail activity
        Toast.makeText(this, "Opening details for " + car.getName(), Toast.LENGTH_SHORT).show();

        // Create intent and pass car data to CarDetailActivity
        Intent intent = new Intent(CarListing.this, com.midterm.mobiledesignfinalterm.CarDetail.CarDetailActivity.class);
        intent.putExtra("car_name", car.getName());
        intent.putExtra("car_type", car.getVehicleType());
        intent.putExtra("car_fuel", car.getFuelType());
        intent.putExtra("car_transmission", car.getTransmission());
        intent.putExtra("car_seats", car.getSeats());
        intent.putExtra("car_consumption", car.getFormattedConsumption());
        intent.putExtra("car_price", car.getPriceFormatted());
//        intent.putExtra("car_original_price", car.getOriginalPrice());
        intent.putExtra("car_image", car.getPrimaryImage());

        // Pass user information
        intent.putExtra("user_phone", userPhone);
        intent.putExtra("user_name", userName);

        // Pass booking details
        intent.putExtra("pickup_time", pickupTime);
        intent.putExtra("return_time", dropoffTime);
        intent.putExtra("pickup_location", pickupLocation);
        intent.putExtra("return_location", dropoffLocation);

        startActivity(intent);
    }

    private void handleFavoriteToggle(Car car, int position) {
        car.setFavorite(!car.isFavorite());
        carAdapter.notifyItemChanged(position);

        String message = car.isFavorite() ? "Added to favorites" : "Removed from favorites";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
        View[] views = {editTextSearch, buttonDateTime, buttonFilter, recyclerViewCars, profileSection};

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

    private void animateProfileClick(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.1f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.1f, 1f);
        ObjectAnimator elevation = ObjectAnimator.ofFloat(view, "elevation", 0f, 8f, 0f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, elevation);
        animatorSet.setDuration(300);
        animatorSet.setInterpolator(new OvershootInterpolator(1.2f));
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
}

