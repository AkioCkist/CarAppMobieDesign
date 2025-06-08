package com.midterm.mobiledesignfinalterm.CarListing;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.midterm.mobiledesignfinalterm.R;
import com.midterm.mobiledesignfinalterm.authentication.Login;

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
    private List<CarItem> carList;
    private List<CarItem> filteredCarList;

    // Thông tin thời gian và địa điểm
    private String pickupTime = "21:00 21/6/2025";
    private String returnTime = "19:00 23/6/2025";
    private String pickupLocation = "Headquarter: 123 Main St, Đà Nẵng";
    private String returnLocation = "Headquarter: 123 Main St, Đà Nẵng";

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
        // Format: Đà Nẵng --- Đà Nẵng\n12:00 21/07/2025 --- 12:00 31/07/2025
        String buttonText = pickupLocation + " --- " + returnLocation + "\n"
                + pickupTime + " --- " + returnTime;
        buttonDateTime.setText(buttonText);
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
                animateButtonClick(v);
                handleDateTimeLocationSetting();
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
            public void onRentalClick(CarItem car) {
                handleRentalNow(car);
            }

            @Override
            public void onFavoriteClick(CarItem car, int position) {
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

    private void loadCarData() {
        // Simulate API call - replace with actual API integration
        carList.clear();

        // Sample car data - replace with API response
        carList.add(new CarItem("All New Rush", "SUV", "Xăng", "Số sàn", "6 People",
                "5L/100km", "$72.00", "$80.00", R.drawable.tesla_model_x, false));

        carList.add(new CarItem("CR - V", "SUV", "Xăng", "Số sàn", "6 People",
                "5L/100km", "$80.00", "$90.00", R.drawable.tesla_model_3, true));

        carList.add(new CarItem("Tesla Model S", "Sedan", "Electric", "Auto", "5 People",
                "0L/100km", "$95.00", "$110.00", R.drawable.tesla_model_x, false));

        carList.add(new CarItem("BMW X5", "SUV", "Xăng", "Auto", "7 People",
                "8L/100km", "$120.00", "$140.00", R.drawable.tesla_model_3, false));

        // Update filtered list and adapter
        filteredCarList.clear();
        filteredCarList.addAll(carList);
        carAdapter.notifyDataSetChanged();

        // Update car count
        textViewCarCount.setText("Found " + filteredCarList.size() + " cars");
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

        // Setup location options
        String[] locations = {"Đà Nẵng", "Hồ Chí Minh", "Hà Nội"};
        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, locations);
        spinnerPickupLocation.setAdapter(adapter);
        spinnerReturnLocation.setAdapter(adapter);
        // Set current selection
        spinnerPickupLocation.setSelection(java.util.Arrays.asList(locations).indexOf(pickupLocation));
        spinnerReturnLocation.setSelection(java.util.Arrays.asList(locations).indexOf(returnLocation));

        // Set current time
        btnPickupTime.setText(pickupTime);
        btnReturnTime.setText(returnTime);

        // Time pickers
        btnPickupTime.setOnClickListener(v -> showDateTimePicker(btnPickupTime));
        btnReturnTime.setOnClickListener(v -> showDateTimePicker(btnReturnTime));

        android.app.AlertDialog dialog = builder.create();
        btnConfirm.setOnClickListener(v -> {
            pickupLocation = spinnerPickupLocation.getSelectedItem().toString();
            returnLocation = spinnerReturnLocation.getSelectedItem().toString();
            pickupTime = btnPickupTime.getText().toString();
            returnTime = btnReturnTime.getText().toString();
            updateDateTimeLocationButton();
            dialog.dismiss();
        });
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showDateTimePicker(Button targetButton) {
        // Parse current value
        String current = targetButton.getText().toString();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm dd/MM/yyyy");
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        try { calendar.setTime(sdf.parse(current)); } catch (Exception ignored) {}
        int year = calendar.get(java.util.Calendar.YEAR);
        int month = calendar.get(java.util.Calendar.MONTH);
        int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);
        int hour = calendar.get(java.util.Calendar.HOUR_OF_DAY);
        int minute = calendar.get(java.util.Calendar.MINUTE);
        android.app.DatePickerDialog datePicker = new android.app.DatePickerDialog(this, (view, y, m, d) -> {
            android.app.TimePickerDialog timePicker = new android.app.TimePickerDialog(this, (tp, h, min) -> {
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.set(y, m, d, h, min);
                targetButton.setText(sdf.format(cal.getTime()));
            }, hour, minute, true);
            timePicker.show();
        }, year, month, day);
        datePicker.show();
    }

    private void handleFilterOptions() {
        // TODO: Open filter options dialog
        Toast.makeText(this, "Filter options - Coming soon", Toast.LENGTH_SHORT).show();
    }

    private void handleRentalNow(CarItem car) {
        // TODO: Navigate to Car Detail activity
        Toast.makeText(this, "Opening details for " + car.getName(), Toast.LENGTH_SHORT).show();

        // Uncomment when CarDetail activity is created
        // Intent intent = new Intent(CarListing.this, CarDetailActivity.class);
        // intent.putExtra("car_data", car);
        // intent.putExtra("user_phone", userPhone);
        // intent.putExtra("user_name", userName);
        // intent.putExtra("pickup_time", pickupTime);
        // intent.putExtra("return_time", returnTime);
        // intent.putExtra("pickup_location", pickupLocation);
        // intent.putExtra("return_location", returnLocation);
        // startActivity(intent);
    }

    private void handleFavoriteToggle(CarItem car, int position) {
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
        return returnTime;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public String getReturnLocation() {
        return returnLocation;
    }

    // Setter methods to update time and location
    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
        updateDateTimeLocationButton();
    }

    public void setReturnTime(String returnTime) {
        this.returnTime = returnTime;
        updateDateTimeLocationButton();
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
        updateDateTimeLocationButton();
    }

    public void setReturnLocation(String returnLocation) {
        this.returnLocation = returnLocation;
        updateDateTimeLocationButton();
    }

    // Car Item Data Class
    public static class CarItem {
        private String name;
        private String type;
        private String fuelType;
        private String transmission;
        private String seats;
        private String consumption;
        private String price;
        private String originalPrice;
        private int imageResource;
        private boolean isFavorite;

        public CarItem(String name, String type, String fuelType, String transmission,
                       String seats, String consumption, String price, String originalPrice,
                       int imageResource, boolean isFavorite) {
            this.name = name;
            this.type = type;
            this.fuelType = fuelType;
            this.transmission = transmission;
            this.seats = seats;
            this.consumption = consumption;
            this.price = price;
            this.originalPrice = originalPrice;
            this.imageResource = imageResource;
            this.isFavorite = isFavorite;
        }

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getFuelType() { return fuelType; }
        public void setFuelType(String fuelType) { this.fuelType = fuelType; }

        public String getTransmission() { return transmission; }
        public void setTransmission(String transmission) { this.transmission = transmission; }

        public String getSeats() { return seats; }
        public void setSeats(String seats) { this.seats = seats; }

        public String getConsumption() { return consumption; }
        public void setConsumption(String consumption) { this.consumption = consumption; }

        public String getPrice() { return price; }
        public void setPrice(String price) { this.price = price; }

        public String getOriginalPrice() { return originalPrice; }
        public void setOriginalPrice(String originalPrice) { this.originalPrice = originalPrice; }

        public int getImageResource() { return imageResource; }
        public void setImageResource(int imageResource) { this.imageResource = imageResource; }

        public boolean isFavorite() { return isFavorite; }
        public void setFavorite(boolean favorite) { isFavorite = favorite; }
    }
}

