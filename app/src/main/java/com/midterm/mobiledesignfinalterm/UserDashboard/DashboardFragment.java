package com.midterm.mobiledesignfinalterm.UserDashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.midterm.mobiledesignfinalterm.R;
import com.midterm.mobiledesignfinalterm.models.Booking;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DashboardFragment extends Fragment implements BookingAdapter.BookingClickListener {

    private static final String TAG = "DashboardFragment";
    private RecyclerView recyclerViewBookings;
    private TextView textViewNoBookings;
    private BookingAdapter bookingAdapter;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Initialize views
        recyclerViewBookings = view.findViewById(R.id.recyclerViewBookings);
        textViewNoBookings = view.findViewById(R.id.textViewNoBookings);

        // Debug info about the views
        Log.d(TAG, "RecyclerView reference: " + recyclerViewBookings);
        Log.d(TAG, "TextView reference: " + textViewNoBookings);

        // Setup RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerViewBookings.setLayoutManager(layoutManager);

        bookingAdapter = new BookingAdapter(requireContext(), this);
        recyclerViewBookings.setAdapter(bookingAdapter);

        Log.d(TAG, "RecyclerView setup complete. LayoutManager: " + layoutManager + ", Adapter: " + bookingAdapter);

        // For debugging, add an adapter data observer
        bookingAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                Log.d(TAG, "Adapter data changed - Item count: " + bookingAdapter.getItemCount());
            }
        });

        // Get user ID from parent activity
        if (getActivity() instanceof UserDashboard) {
            userId = ((UserDashboard) getActivity()).getUserId();
            Log.d(TAG, "User ID received: " + userId);
            // Initial data load - call after adapter is attached
            fetchBookings();
        } else {
            Log.e(TAG, "Could not get UserDashboard activity");
            showNoBookingsMessage("Cannot access user information");
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh bookings when fragment becomes visible again
        if (userId != null && !userId.isEmpty()) {
            Log.d(TAG, "onResume: refreshing bookings for user: " + userId);
            fetchBookings();
        }
    }

    private void fetchBookings() {
        if (userId == null || userId.isEmpty()) {
            Log.e(TAG, "Cannot fetch bookings: User ID is null or empty");
            showNoBookingsMessage("User ID not available");
            return;
        }

        // Show temporary loading message
        textViewNoBookings.setText("Loading bookings...");
        textViewNoBookings.setVisibility(View.VISIBLE);
        recyclerViewBookings.setVisibility(View.GONE);

        OkHttpClient client = new OkHttpClient();

        // URL to fetch bookings API with renter_id parameter
        String url = "http://10.0.2.2/myapi/bookings.php?renter_id=" + userId;
        Log.d(TAG, "Fetching bookings from URL: " + url);

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "API call failed", e);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showNoBookingsMessage("Failed to load bookings: " + e.getMessage());
                        Toast.makeText(getContext(), "Network error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Log.d(TAG, "Raw API Response: " + responseData);

                if (!response.isSuccessful()) {
                    Log.e(TAG, "Unexpected response code: " + response.code() + " - " + responseData);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            showNoBookingsMessage("Server error: " + response.code());
                        });
                    }
                    return;
                }

                try {
                    JSONObject jsonResponse = new JSONObject(responseData);

                    if (jsonResponse.getBoolean("success")) {
                        JSONArray bookingsArray = jsonResponse.getJSONArray("data");
                        int bookingsCount = bookingsArray.length();
                        Log.d(TAG, "Number of bookings received: " + bookingsCount);

                        final List<Booking> bookings = parseBookings(bookingsArray);
                        Log.d(TAG, "Number of bookings parsed: " + bookings.size());

                        // Debug all bookings
                        for (int i = 0; i < bookings.size(); i++) {
                            Booking booking = bookings.get(i);
                            Log.d(TAG, "Parsed booking [" + i + "]: ID=" + booking.getBookingId() +
                                  ", vehicle=" + booking.getVehicleName() +
                                  ", status=" + booking.getStatus());
                        }

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                if (bookings.isEmpty()) {
                                    Log.d(TAG, "No bookings found after parsing");
                                    showNoBookingsMessage("No booking history found");
                                } else {
                                    Log.d(TAG, "Setting " + bookings.size() + " bookings to adapter");
                                    hideNoBookingsMessage();

                                    // Update the RecyclerView
                                    bookingAdapter.setBookingList(bookings);
                                    recyclerViewBookings.setVisibility(View.VISIBLE);

                                    // Debug check for adapter item count
                                    Log.d(TAG, "After setting data - Adapter item count: " + bookingAdapter.getItemCount());
                                    Log.d(TAG, "RecyclerView child count: " + recyclerViewBookings.getChildCount());

                                    // Force layout refresh
                                    recyclerViewBookings.post(() -> {
                                        Log.d(TAG, "Post-layout - RecyclerView child count: " + recyclerViewBookings.getChildCount());
                                    });
                                }
                            });
                        }
                    } else {
                        String errorMessage = jsonResponse.optString("error", "Unknown error");
                        Log.e(TAG, "API error: " + errorMessage);
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                showNoBookingsMessage("Error: " + errorMessage);
                            });
                        }
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "JSON parsing error", e);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            showNoBookingsMessage("Failed to parse data");
                        });
                    }
                }
            }
        });
    }

    private List<Booking> parseBookings(JSONArray jsonArray) throws JSONException {
        List<Booking> bookingList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject bookingJson = jsonArray.getJSONObject(i);
            Log.d(TAG, "Parsing booking #" + i + ": " + bookingJson.toString());

            try {
                Booking booking = new Booking();
                booking.setBookingId(bookingJson.getInt("booking_id"));
                booking.setVehicleName(bookingJson.getString("vehicle_name"));
                booking.setVehicleType(bookingJson.getString("vehicle_type"));
                booking.setPickupDate(bookingJson.getString("pickup_date"));
                booking.setPickupTime(bookingJson.getString("pickup_time"));
                booking.setReturnDate(bookingJson.getString("return_date"));
                booking.setReturnTime(bookingJson.getString("return_time"));
                booking.setPickupLocation(bookingJson.getString("pickup_location"));
                booking.setReturnLocation(bookingJson.getString("return_location"));
                booking.setTotalPrice(bookingJson.getDouble("total_price"));
                booking.setFinalPrice(bookingJson.getDouble("final_price"));
                booking.setStatus(bookingJson.getString("status"));

                // Try to get vehicle image URL if available
                String baseImageUrl = "http://10.0.2.2/myapi/cars/";
                String vehicleName = booking.getVehicleName().replace(" ", "").replace("-", "");
                booking.setVehicleImageUrl(baseImageUrl + vehicleName + "/1.png");

                bookingList.add(booking);
                Log.d(TAG, "Successfully parsed booking ID: " + booking.getBookingId() +
                        ", vehicle: " + booking.getVehicleName() +
                        ", status: " + booking.getStatus());
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing booking #" + i + ": " + e.getMessage());
            }
        }

        return bookingList;
    }

    private void showNoBookingsMessage(String message) {
        Log.d(TAG, "Showing message: " + message);
        if (recyclerViewBookings != null) {
            recyclerViewBookings.setVisibility(View.GONE);
        }
        if (textViewNoBookings != null) {
            textViewNoBookings.setText(message);
            textViewNoBookings.setVisibility(View.VISIBLE);
        }
    }

    private void hideNoBookingsMessage() {
        Log.d(TAG, "Hiding 'no bookings' message");
        if (recyclerViewBookings != null) {
            recyclerViewBookings.setVisibility(View.VISIBLE);
        }
        if (textViewNoBookings != null) {
            textViewNoBookings.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBookingClick(Booking booking) {
        // Handle booking item click
        Toast.makeText(getContext(), "Booking #" + booking.getBookingId() + " clicked", Toast.LENGTH_SHORT).show();

        // You can navigate to booking details screen here if needed
        // Intent intent = new Intent(getContext(), BookingDetailsActivity.class);
        // intent.putExtra("booking_id", booking.getBookingId());
        // startActivity(intent);
    }
}
