package com.midterm.mobiledesignfinalterm.homepage;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.midterm.mobiledesignfinalterm.R;
import com.midterm.mobiledesignfinalterm.authentication.Login;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Homepage extends AppCompatActivity {

    private TextView textViewLocation;
    private TextView textViewPoints;
    private TextView textViewUserName;
    private TextView textViewUserPhone;
    private Button buttonHostEarn;
    private EditText editTextSearch;
    private TextView textViewTopBrands;
    private TextView textViewViewAllBrands;
    private TextView textViewTopRatedCars;
    private TextView textViewViewAllCars;
    private RecyclerView recyclerViewBrands;
    private RecyclerView recyclerViewCars;
    private ImageView imageViewProfile;
    private LinearLayout profileSection;
    private LinearLayout dropdownMenu;
    private Button btnMyProfile;
    private Button btnMyBooking;
    private Button btnSettings;
    private Button btnSignOut;

    // User information from login
    private String userPhone;
    private String userName;
    private List<String> userRoles;
    private boolean isDropdownVisible = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

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

        initializeViews();
        setupClickListeners();
        setupRecyclerViews();

        // Initial entrance animation
        animateInitialEntrance();

        // Setup user info display
        displayUserInfo();

        // Initialize dropdown as hidden
        dropdownMenu.setVisibility(View.GONE);
        dropdownMenu.setAlpha(0f);
        dropdownMenu.setTranslationY(-20f);
    }

    private void getUserInfoFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            userPhone = intent.getStringExtra("user_phone");
            userName = intent.getStringExtra("user_name"); // Make sure this matches the key used in Login
            userRoles = intent.getStringArrayListExtra("user_roles");
        }
    }

    private void initializeViews() {
        textViewLocation = findViewById(R.id.textViewLocation);
        textViewPoints = findViewById(R.id.textViewPoints);
        textViewUserName = findViewById(R.id.textViewUserName);
        textViewUserPhone = findViewById(R.id.textViewUserPhone);
        buttonHostEarn = findViewById(R.id.buttonHostEarn);
        editTextSearch = findViewById(R.id.editTextSearch);
        textViewTopBrands = findViewById(R.id.textViewTopBrands);
        textViewViewAllBrands = findViewById(R.id.textViewViewAllBrands);
        textViewTopRatedCars = findViewById(R.id.textViewTopRatedCars);
        textViewViewAllCars = findViewById(R.id.textViewViewAllCars);
        recyclerViewBrands = findViewById(R.id.recyclerViewBrands);
        recyclerViewCars = findViewById(R.id.recyclerViewCars);
        imageViewProfile = findViewById(R.id.imageViewProfile);
        profileSection = findViewById(R.id.profileSection);
        dropdownMenu = findViewById(R.id.dropdownMenu);
        btnMyProfile = findViewById(R.id.btnMyProfile);
        btnMyBooking = findViewById(R.id.btnMyBooking);
        btnSettings = findViewById(R.id.btnSettings);
        btnSignOut = findViewById(R.id.btnSignOut);
    }

    private void setupClickListeners() {
        buttonHostEarn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButtonClick(v);
                handleHostEarn();
            }
        });

        textViewViewAllBrands.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateTextClick(v);
                handleViewAllBrands();
            }
        });

        textViewViewAllCars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateTextClick(v);
                handleViewAllCars();
            }
        });

        profileSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateProfileClick(v);
                toggleDropdownMenu();
            }
        });

        // Dropdown menu item listeners
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

        editTextSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    animateEditTextFocus(v, true);
                    // Hide dropdown if search is focused
                    if (isDropdownVisible) {
                        hideDropdownMenu();
                    }
                } else {
                    animateEditTextFocus(v, false);
                }
            }
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

    private void displayUserInfo() {
        if (userPhone != null) {
            // Display user name or default
            String displayName = userName != null ? userName : "User";
            textViewUserName.setText(displayName);
            textViewUserPhone.setText(userPhone);
        } else {
            textViewUserName.setText("User");
            textViewUserPhone.setText("Phone not available");
        }
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

        // Fix: Use addListener instead of withEndAction
        animatorSet.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                dropdownMenu.setVisibility(View.GONE);
            }
        });

        animatorSet.start();
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

    // Animation Methods
    private void animateInitialEntrance() {
        View[] views = {textViewLocation, textViewUserName, textViewUserPhone, buttonHostEarn,
                editTextSearch, textViewTopBrands, textViewViewAllBrands,
                textViewTopRatedCars, textViewViewAllCars, recyclerViewBrands, recyclerViewCars, profileSection};

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

    // Click handlers
    private void handleHostEarn() {
        Intent intent = new Intent(Homepage.this, com.midterm.mobiledesignfinalterm.CarListing.CarListing.class);
        intent.putExtra("user_phone", userPhone);
        intent.putExtra("user_name", userName);
        if (userRoles != null) {
            intent.putStringArrayListExtra("user_roles", new ArrayList<>(userRoles));
        }
        startActivity(intent);
    }

    private void handleViewAllBrands() {
        // Navigate to all brands screen
        // Intent intent = new Intent(Homepage.this, AllBrandsActivity.class);
        // startActivity(intent);
    }

    private void handleViewAllCars() {
        // Navigate to all cars screen
        // Intent intent = new Intent(Homepage.this, AllCarsActivity.class);
        // startActivity(intent);
    }

    private void handleMyProfile() {
        // Navigate to profile screen with user info
        // Intent intent = new Intent(Homepage.this, ProfileActivity.class);
        // intent.putExtra("user_phone", userPhone);
        // intent.putExtra("user_name", userName);
        // intent.putStringArrayListExtra("user_roles", (ArrayList<String>) userRoles);
        // startActivity(intent);
    }

    private void handleMyBooking() {
        // Navigate to bookings screen
        // Intent intent = new Intent(Homepage.this, BookingActivity.class);
        // intent.putExtra("user_phone", userPhone);
        // startActivity(intent);
    }

    private void handleSettings() {
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
