package com.midterm.mobiledesignfinalterm.UserDashboard;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.midterm.mobiledesignfinalterm.R;

public class UserDashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // Set the default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new DashboardFragment()).commit();
        }
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.nav_dashboard) {
                    selectedFragment = new DashboardFragment();
                } else if (itemId == R.id.nav_profile) {
                    selectedFragment = new ProfileFragment();
                } else if (itemId == R.id.nav_favorites) {
                    selectedFragment = new FavoriteCarsFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                }
                return true;
            };

    // --- You will need to create these Fragment classes ---

    /**
     * Placeholder Fragment for the Dashboard.
     */
    public static class DashboardFragment extends Fragment {
        // You would inflate your dashboard layout here
    }

    /**
     * Placeholder Fragment for updating user profile.
     */
    public static class ProfileFragment extends Fragment {
        // You would inflate your profile update layout here
    }

    /**
     * Placeholder Fragment for the list of favorite cars.
     */
    public static class FavoriteCarsFragment extends Fragment {
        // You would inflate your favorite cars list layout here
    }
}

