package com.midterm.mobiledesignfinalterm.homepage;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.midterm.mobiledesignfinalterm.R;
import com.midterm.mobiledesignfinalterm.authentication.Login;

import android.widget.Toast;
import android.Manifest;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Homepage extends AppCompatActivity implements LocationListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    private TextView textViewLocation;
    private TextView textViewPoints;
    private TextView textViewTopBrands;
    private TextView textViewViewAllBrands;
    private TextView textViewTopRatedCars;
    private TextView textViewViewAllCars;
    private TextView textViewWelcome;
    private TextView textViewPointsLabel;
    private RecyclerView recyclerViewBrands;
    private RecyclerView recyclerViewCars;
    private ImageView imageViewProfile;
    private ImageView imageViewPoints;
    private LinearLayout profileSection;
    private LinearLayout dropdownMenu;
    private LinearLayout layoutLocationHeader;
    private LinearLayout layoutBookingSection;
    private LinearLayout layoutTopBrandsSection;
    private LinearLayout layoutTopCarsSection;
    private Button btnMyProfile;
    private Button btnMyBooking;
    private Button btnSettings;
    private Button btnSignOut;

    // Pickup/Drop-off elements
    private TextView textViewPickupLocation;
    private TextView textViewPickupDate;
    private TextView textViewPickupTime;
    private TextView textViewDropoffLocation;
    private TextView textViewDropoffDate;
    private TextView textViewDropoffTime;

    // User information from login
    private String userPhone;
    private String userName;
    private List<String> userRoles;
    private boolean isDropdownVisible = false;
    private LinearLayout layoutPickupLocation;
    private LinearLayout layoutPickupDate;
    private LinearLayout layoutPickupTime;
    private LinearLayout layoutDropoffLocation;
    private LinearLayout layoutDropoffDate;
    private LinearLayout layoutDropoffTime;
    private ImageView imageViewSwapLocations;

    // Location related
    private LocationManager locationManager;
    private boolean isLocationEnabled = false;
    private boolean canGetLocation = false;

    // Calendar instances for date/time pickers
    private Calendar pickupCalendar;
    private Calendar dropoffCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        // Initialize views first
        initializeViews();

        // Make status bar transparent and content edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat windowInsetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        windowInsetsController.setAppearanceLightStatusBars(false);

        // Get user information from intent
        getUserInfoFromIntent();

        // Show Toast confirmation
        if (userName != null && !userName.isEmpty()) {
            Toast.makeText(this, "Welcome, " + userName + "!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Welcome! (No username received)", Toast.LENGTH_SHORT).show();
        }

        setupClickListeners();
        setupRecyclerViews();

        // Initialize dropdown as hidden
        if (dropdownMenu != null) {
            dropdownMenu.setVisibility(View.GONE);
            dropdownMenu.setAlpha(0f);
            dropdownMenu.setTranslationY(-20f);
        }

        // Initialize default dates and times for pickers
        initializeDefaultDateTime();

        // Initialize location services
        initializeLocation();

        // Initial entrance animation - moved to after location setup
        animateInitialEntrance();
    }

    private void getUserInfoFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            userPhone = intent.getStringExtra("user_phone");
            userName = intent.getStringExtra("user_name");
            userRoles = intent.getStringArrayListExtra("user_roles");
        }
    }

    private void initializeViews() {
        textViewLocation = findViewById(R.id.textViewLocation);
        textViewTopBrands = findViewById(R.id.textViewTopBrands);
        textViewViewAllBrands = findViewById(R.id.textViewViewAllBrands);
        textViewTopRatedCars = findViewById(R.id.textViewTopRatedCars);
        textViewViewAllCars = findViewById(R.id.textViewViewAllCars);
        textViewWelcome = findViewById(R.id.textViewWelcome);
        recyclerViewBrands = findViewById(R.id.recyclerViewBrands);
        recyclerViewCars = findViewById(R.id.recyclerViewCars);
        imageViewProfile = findViewById(R.id.imageViewProfile);
        profileSection = findViewById(R.id.profileSection);
        dropdownMenu = findViewById(R.id.dropdownMenu);
        layoutLocationHeader = findViewById(R.id.layoutLocationHeader);
        layoutBookingSection = findViewById(R.id.layoutBookingSection);
        layoutTopBrandsSection = findViewById(R.id.layoutTopBrandsSection);
        layoutTopCarsSection = findViewById(R.id.layoutTopCarsSection);
        btnMyProfile = findViewById(R.id.btnMyProfile);
        btnMyBooking = findViewById(R.id.btnMyBooking);
        btnSettings = findViewById(R.id.btnSettings);
        btnSignOut = findViewById(R.id.btnSignOut);

        // Initialize pickup/drop-off elements
        layoutPickupLocation = findViewById(R.id.layoutPickupLocation);
        layoutPickupDate = findViewById(R.id.layoutPickupDate);
        layoutPickupTime = findViewById(R.id.layoutPickupTime);
        layoutDropoffLocation = findViewById(R.id.layoutDropoffLocation);
        layoutDropoffDate = findViewById(R.id.layoutDropoffDate);
        layoutDropoffTime = findViewById(R.id.layoutDropoffTime);
        imageViewSwapLocations = findViewById(R.id.imageViewSwapLocations);

        // TextViews for data display
        textViewPickupLocation = findViewById(R.id.textViewPickupLocation);
        textViewPickupDate = findViewById(R.id.textViewPickupDate);
        textViewPickupTime = findViewById(R.id.textViewPickupTime);
        textViewDropoffLocation = findViewById(R.id.textViewDropoffLocation);
        textViewDropoffDate = findViewById(R.id.textViewDropoffDate);
        textViewDropoffTime = findViewById(R.id.textViewDropoffTime);
    }

    private void initializeDefaultDateTime() {
        pickupCalendar = Calendar.getInstance();
        dropoffCalendar = Calendar.getInstance();
        dropoffCalendar.add(Calendar.DAY_OF_YEAR, 1); // Default drop-off to next day

        // Set initial text for date and time fields
        updateDateTextView(textViewPickupDate, pickupCalendar);
        updateTimeTextView(textViewPickupTime, pickupCalendar);
        updateDateTextView(textViewDropoffDate, dropoffCalendar);
        updateTimeTextView(textViewDropoffTime, dropoffCalendar);
    }

    private void initializeLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            // Request location permissions
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permissions already granted, get location
            getLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, get location
                getLocation();
            } else {
                // Permission denied
                textViewLocation.setText("Location permission denied");
                Toast.makeText(this, "Location permission is required for better experience",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // Check if GPS is enabled
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // Check if Network is enabled
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // No network provider is enabled
                showLocationSettingsDialog();
            } else {
                this.canGetLocation = true;

                // Check permissions again before requesting location updates
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED) {

                    // Get location from Network Provider
                    if (isNetworkEnabled) {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        if (locationManager != null) {
                            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                onLocationChanged(location);
                            }
                        }
                    }

                    // Get location from GPS Provider
                    if (isGPSEnabled) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        if (locationManager != null) {
                            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                onLocationChanged(location);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            textViewLocation.setText("Error getting location");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            // Get address from coordinates
            getAddressFromLocation(latitude, longitude);

            // Stop location updates after getting the location
            if (locationManager != null) {
                locationManager.removeUpdates(this);
            }
        }
    }

    private void getAddressFromLocation(double latitude, double longitude) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                String cityName = address.getLocality();
                String countryName = address.getCountryName();

                // Check if it's a Vietnamese city
                if (countryName != null && countryName.toLowerCase().contains("vietnam")) {
                    if (cityName != null && !cityName.isEmpty()) {
                        textViewLocation.setText(cityName + ", Vietnam");
                    } else {
                        // If city is null, try to get administrative area or other location info
                        String adminArea = address.getAdminArea();
                        if (adminArea != null && !adminArea.isEmpty()) {
                            textViewLocation.setText(adminArea + ", Vietnam");
                        } else {
                            textViewLocation.setText("Vietnam");
                        }
                    }
                } else {
                    // Not in Vietnam, show general location
                    if (cityName != null && countryName != null) {
                        textViewLocation.setText(cityName + ", " + countryName);
                    } else if (countryName != null) {
                        textViewLocation.setText(countryName);
                    } else {
                        textViewLocation.setText("Location detected");
                    }
                }
            } else {
                textViewLocation.setText("Unable to get address");
            }
        } catch (IOException e) {
            e.printStackTrace();
            textViewLocation.setText("Error getting address");
        }
    }

    private void showLocationSettingsDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Location Settings");
        alertDialog.setMessage("Location services are not enabled. Do you want to go to settings menu?");

        alertDialog.setPositiveButton("Settings", (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        });

        alertDialog.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
            textViewLocation.setText("Location services disabled");
        });

        alertDialog.show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    private void setupClickListeners() {
        // Location header click listener
        layoutLocationHeader.setOnClickListener(v -> {
            animateTextClick(v);
            // Refresh location
            getLocation();
            Toast.makeText(Homepage.this, "Refreshing location...", Toast.LENGTH_SHORT).show();
        });

        textViewViewAllBrands.setOnClickListener(v -> {
            animateTextClick(v);
            handleViewAllBrands();
        });

        textViewViewAllCars.setOnClickListener(v -> {
            animateTextClick(v);
            handleViewAllCars();
        });

        profileSection.setOnClickListener(v -> {
            animateProfileClick(v);
            toggleDropdownMenu();
        });

        // Dropdown menu item listeners
        btnMyProfile.setOnClickListener(v -> {
            animateMenuItemClick(v);
            hideDropdownMenu();
            handleMyProfile();
        });

        btnMyBooking.setOnClickListener(v -> {
            animateMenuItemClick(v);
            hideDropdownMenu();
            handleMyBooking();
        });

        btnSettings.setOnClickListener(v -> {
            animateMenuItemClick(v);
            hideDropdownMenu();
            handleSettings();
        });

        btnSignOut.setOnClickListener(v -> {
            animateMenuItemClick(v);
            hideDropdownMenu();
            handleSignOut();
        });

        // Pickup/Drop-off click listeners
        layoutPickupLocation.setOnClickListener(v -> {
            animateTextClick(v);
            showLocationSelectionDialog(textViewPickupLocation);
        });

        layoutPickupDate.setOnClickListener(v -> {
            animateTextClick(v);
            showDatePicker(textViewPickupDate, pickupCalendar);
        });

        layoutPickupTime.setOnClickListener(v -> {
            animateTextClick(v);
            showTimePicker(textViewPickupTime, pickupCalendar);
        });

        layoutDropoffLocation.setOnClickListener(v -> {
            animateTextClick(v);
            showLocationSelectionDialog(textViewDropoffLocation);
        });

        layoutDropoffDate.setOnClickListener(v -> {
            animateTextClick(v);
            showDatePicker(textViewDropoffDate, dropoffCalendar);
        });

        layoutDropoffTime.setOnClickListener(v -> {
            animateTextClick(v);
            showTimePicker(textViewDropoffTime, dropoffCalendar);
        });

        // Search/proceed button (central arrow)
        imageViewSwapLocations.setOnClickListener(v -> {
            animateButtonClick(v);
            // Start CarListing activity and pass user info and pickup/drop-off data
            Intent intent = new Intent(Homepage.this, com.midterm.mobiledesignfinalterm.CarListing.CarListing.class);
            intent.putExtra("user_phone", userPhone);
            intent.putExtra("user_name", userName);
            if (userRoles != null) {
                intent.putStringArrayListExtra("user_roles", new ArrayList<>(userRoles));
            }
            intent.putExtra("pickup_location", textViewPickupLocation.getText().toString());
            intent.putExtra("dropoff_location", textViewDropoffLocation.getText().toString());
            intent.putExtra("pickup_date", textViewPickupDate.getText().toString());
            intent.putExtra("pickup_time", textViewPickupTime.getText().toString());
            intent.putExtra("dropoff_date", textViewDropoffDate.getText().toString());
            intent.putExtra("dropoff_time", textViewDropoffTime.getText().toString());
            startActivity(intent);
        });
    }

    private void setupRecyclerViews() {
        // Setup brands RecyclerView
        recyclerViewBrands.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        BrandAdapter brandAdapter = new BrandAdapter(getBrandsList());
        recyclerViewBrands.setAdapter(brandAdapter);

        // Setup cars RecyclerView
        recyclerViewCars.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        CarAdapter carAdapter = new CarAdapter(getCarsList());
        recyclerViewCars.setAdapter(carAdapter);
    }

    private void toggleDropdownMenu() {
        if (isDropdownVisible) {
            hideDropdownMenu();
        } else {
            showDropdownMenu();
        }
    }

    private void showDropdownMenu() {
        isDropdownVisible = true;
        dropdownMenu.bringToFront();
        dropdownMenu.setVisibility(View.VISIBLE);

        dropdownMenu.setAlpha(0f);
        dropdownMenu.setTranslationY(-20f);
        dropdownMenu.setScaleX(0.8f);
        dropdownMenu.setScaleY(0.8f);

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

    /**
     * Shows a dialog for selecting a city from a predefined list.
     * @param locationTextView The TextView to update with the selected location.
     */
    private void showLocationSelectionDialog(final TextView locationTextView) {
        final String[] cities = {"Hanoi", "Da Nang", "Ho Chi Minh"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme); // Use a custom theme for dark background
        builder.setTitle("Select City");
        builder.setItems(cities, (dialog, which) -> {
            String selectedCity = cities[which];
            locationTextView.setText(selectedCity);
            Toast.makeText(Homepage.this, "Selected: " + selectedCity, Toast.LENGTH_SHORT).show();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Shows a DatePickerDialog for selecting a date.
     * @param dateTextView The TextView to update with the selected date.
     * @param calendar The Calendar instance to use for initial date and to update after selection.
     */
    private void showDatePicker(final TextView dateTextView, final Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                // Using a custom theme to ensure dark mode compatibility if needed
                // For a default dark theme, you might not need a specific style here
                android.R.style.Theme_Holo_Dialog_MinWidth, // Or Theme_DeviceDefault_Dialog_NoActionBar_MinWidth for a more modern dark look
                (view, year1, monthOfYear, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year1);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateTextView(dateTextView, calendar);
                }, year, month, day);

        datePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent); // Optional: makes background transparent to better show dialog corners
        datePickerDialog.show();
    }

    /**
     * Updates the given TextView with the formatted date from the Calendar instance.
     * @param dateTextView The TextView to update.
     * @param calendar The Calendar instance containing the date.
     */
    private void updateDateTextView(TextView dateTextView, Calendar calendar) {
        String dateFormat = "dd MMMM yyyy"; // e.g., 20 July 2022
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
        dateTextView.setText(sdf.format(calendar.getTime()));
    }

    /**
     * Shows a TimePickerDialog for selecting a time.
     * @param timeTextView The TextView to update with the selected time.
     * @param calendar The Calendar instance to use for initial time and to update after selection.
     */
    private void showTimePicker(final TextView timeTextView, final Calendar calendar) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                // Using a custom theme to ensure dark mode compatibility if needed
                android.R.style.Theme_Holo_Dialog_MinWidth, // Or Theme_DeviceDefault_Dialog_NoActionBar_MinWidth
                (view, hourOfDay, minute1) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute1);
                    updateTimeTextView(timeTextView, calendar);
                }, hour, minute, true); // true for 24 hour format

        timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent); // Optional
        timePickerDialog.show();
    }

    /**
     * Updates the given TextView with the formatted time from the Calendar instance.
     * @param timeTextView The TextView to update.
     * @param calendar The Calendar instance containing the time.
     */
    private void updateTimeTextView(TextView timeTextView, Calendar calendar) {
        String timeFormat = "HH:mm"; // e.g., 07:00
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat, Locale.getDefault());
        timeTextView.setText(sdf.format(calendar.getTime()));
    }

    private List<Brand> getBrandsList() {
        List<Brand> brands = new ArrayList<>();
        brands.add(new Brand("All", R.drawable.ic_all_brands));
        brands.add(new Brand("Tesla", R.drawable.ic_tesla));
        brands.add(new Brand("BMW", R.drawable.ic_bmw));
        brands.add(new Brand("Ferrari", R.drawable.ic_ferrari));
        brands.add(new Brand("Audi", R.drawable.ic_audi));
        return brands;
    }

    private List<Car> getCarsList() {
        List<Car> cars = new ArrayList<>();
        cars.add(new Car("Tesla Model X", "Available from 2 August", "4 Seats", "$28.32/hour", "5.00", R.drawable.tesla_model_x));
        cars.add(new Car("Tesla Model 3", "Available Now", "4 Seats", "$25.50/hour", "4.8", R.drawable.tesla_model_3));
        return cars;
    }

    // Animation Methods - Enhanced with all components
    private void animateInitialEntrance() {
        View[] views = {
                layoutLocationHeader, textViewWelcome, profileSection,
                layoutBookingSection,
                layoutTopBrandsSection, recyclerViewBrands,
                layoutTopCarsSection, recyclerViewCars
        };

        for (int i = 0; i < views.length; i++) {
            View view = views[i];
            if (view == null) continue;

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
                    .setStartDelay(i * 100)
                    .setInterpolator(new OvershootInterpolator(1.2f))
                    .start();
        }
    }

    private void animateButtonClick(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.95f, 1.1f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.95f, 1.1f, 1f);
        ObjectAnimator elevation = ObjectAnimator.ofFloat(view, "elevation", view.getElevation(), view.getElevation() + 8f, view.getElevation());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, elevation);
        animatorSet.setDuration(400);
        animatorSet.start();
    }

    private void animateTextClick(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.2f, 1f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 1f, 0.7f, 1f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, alpha);
        animatorSet.setDuration(250);
        animatorSet.setInterpolator(new OvershootInterpolator(1.3f));
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

    // Click handlers
    private void handleViewAllBrands() {
        Toast.makeText(this, "View all brands", Toast.LENGTH_SHORT).show();
        // Navigate to all brands screen
        // Intent intent = new Intent(Homepage.this, AllBrandsActivity.class);
        // startActivity(intent);
    }

    private void handleViewAllCars() {
        Toast.makeText(this, "View all cars", Toast.LENGTH_SHORT).show();
        // Navigate to all cars screen
        // Intent intent = new Intent(Homepage.this, AllCarsActivity.class);
        // startActivity(intent);
    }

    private void handleMyProfile() {
        Toast.makeText(this, "My Profile", Toast.LENGTH_SHORT).show();
        // Navigate to profile screen with user info
        // Intent intent = new Intent(Homepage.this, ProfileActivity.class);
        // intent.putExtra("user_phone", userPhone);
        // intent.putExtra("user_name", userName);
        // intent.putStringArrayListExtra("user_roles", (ArrayList<String>) userRoles);
        // startActivity(intent);
    }

    private void handleMyBooking() {
        Toast.makeText(this, "My Booking", Toast.LENGTH_SHORT).show();
        // Navigate to bookings screen
        // Intent intent = new Intent(Homepage.this, BookingActivity.class);
        // intent.putExtra("user_phone", userPhone);
        // startActivity(intent);
    }

    private void handleSettings() {
        Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
        // Navigate to settings screen
        // Intent intent = new Intent(Homepage.this, SettingsActivity.class);
        // startActivity(intent);
    }

    private void handleSignOut() {
        // Clear user session and navigate to login
        Intent intent = new Intent(Homepage.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // Pickup/Drop-off handlers (Removed individual handlers as they now call the generic picker methods)
    // The logic to call the pickers is directly in setupClickListeners

    private void handleProceedToSearch() {
        String pickup = textViewPickupLocation.getText().toString();
        String dropoff = textViewDropoffLocation.getText().toString();
        Toast.makeText(this, "Searching cars from " + pickup + " to " + dropoff, Toast.LENGTH_SHORT).show();
        // Add navigation logic here if needed
    }
    @Override
    public void onBackPressed() {
        if (isDropdownVisible) {
            hideDropdownMenu();
        } else {
            super.onBackPressed();
        }
    }

    // Data classes for RecyclerView
    public static class Brand {
        private String name;
        private int iconResource;

        public Brand(String name, int iconResource) {
            this.name = name;
            this.iconResource = iconResource;
        }

        public String getName() { return name; }
        public int getIconResource() { return iconResource; }
    }

    public static class Car {
        private String name;
        private String availability;
        private String seats;
        private String price;
        private String rating;
        private int imageResource;

        public Car(String name, String availability, String seats, String price, String rating, int imageResource) {
            this.name = name;
            this.availability = availability;
            this.seats = seats;
            this.price = price;
            this.rating = rating;
            this.imageResource = imageResource;
        }

        public String getName() { return name; }
        public String getAvailability() { return availability; }
        public String getSeats() { return seats; }
        public String getPrice() { return price; }
        public String getRating() { return rating; }
        public int getImageResource() { return imageResource; }
    }
}

