package com.midterm.mobiledesignfinalterm.homepage;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.midterm.mobiledesignfinalterm.CarDetail.CarDetailActivity;
import com.midterm.mobiledesignfinalterm.R;
import com.midterm.mobiledesignfinalterm.api.FavoriteApiService;
import com.midterm.mobiledesignfinalterm.api.RetrofitClient;
import com.midterm.mobiledesignfinalterm.models.Car;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiCarAdapter extends RecyclerView.Adapter<ApiCarAdapter.CarViewHolder> {
    private List<Car> carList;
    private final Context context;
    private final String userId;

    public ApiCarAdapter(Context context, String userId) {
        this.carList = new ArrayList<>();
        this.context = context;
        this.userId = userId;
    }

    public void setCarList(List<Car> carList) {
        this.carList = carList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_car, parent, false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        Car car = carList.get(position);
        holder.bind(car);
    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

    public class CarViewHolder extends RecyclerView.ViewHolder {
        private final ImageView iv_primaryImageCarHomePage;
        private final TextView tv_cardCarNameHomePage;
        private final TextView tv_cardCarTypeHomePage;
        private final TextView tv_CardFuelTypeHomePage;
        private final TextView tv_CardTransmissionHomePage;
        private final TextView tv_SeatsHomePage;
        private final TextView tv_cardConsumptionHomePage;
        private final TextView tv_BasePriceHomePage;
        private final ImageView iv_FavoriteHomePage;
        private final Button btn_rentalNowHomePage;

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_primaryImageCarHomePage = itemView.findViewById(R.id.iv_primaryImageCarHomePage);
            tv_cardCarNameHomePage = itemView.findViewById(R.id.tv_cardCarNameHomePage);
            tv_cardCarTypeHomePage = itemView.findViewById(R.id.tv_cardCarTypeHomePage);
            tv_CardFuelTypeHomePage = itemView.findViewById(R.id.tv_CardFuelTypeHomePage);
            tv_CardTransmissionHomePage = itemView.findViewById(R.id.tv_CardTransmissionHomePage);
            tv_SeatsHomePage = itemView.findViewById(R.id.tv_SeatsHomePage);
            tv_cardConsumptionHomePage = itemView.findViewById(R.id.tv_cardConsumptionHomePage);
            tv_BasePriceHomePage = itemView.findViewById(R.id.tv_BasePriceHomePage);
            iv_FavoriteHomePage = itemView.findViewById(R.id.iv_FavoriteHomePage);
            btn_rentalNowHomePage = itemView.findViewById(R.id.btn_rentalNowHomePage);
        }

        public void bind(Car car) {
            // Set car name
            tv_cardCarNameHomePage.setText(car.getName());
            tv_cardCarTypeHomePage.setText(car.getVehicleType() != null ? car.getVehicleType() : "SUV");
            tv_CardFuelTypeHomePage.setText(car.getFuelType() != null ? car.getFuelType() : "Xăng");
            tv_CardTransmissionHomePage.setText(car.getTransmission() != null ? car.getTransmission() : "Số tự động");
            tv_SeatsHomePage.setText(car.getFormattedSeats() != null ? car.getFormattedSeats() : "4 People");
            tv_cardConsumptionHomePage.setText(car.getFormattedConsumption() != null ? car.getFormattedConsumption() : "6.5L/100km");
            tv_BasePriceHomePage.setText(car.getPriceFormatted());

            // Load image using Glide
            String imageUrl = car.getPrimaryImage();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.tesla_model_x) // Fallback to a model we know exists
                        .error(R.drawable.tesla_model_x)
                        .into(iv_primaryImageCarHomePage);
            } else {
                iv_primaryImageCarHomePage.setImageResource(R.drawable.tesla_model_x);
            }

            // Set favorite icon
            if (car.isFavorite()) {
                iv_FavoriteHomePage.setImageResource(R.drawable.ic_heart_filled);
            } else {
                iv_FavoriteHomePage.setImageResource(R.drawable.ic_heart_outline);
            }

            // Handle favorite button click
            iv_FavoriteHomePage.setOnClickListener(v -> toggleFavorite(car, iv_FavoriteHomePage));

            // Handle rental button click
            btn_rentalNowHomePage.setOnClickListener(v -> {
                Intent intent = new Intent(context, CarDetailActivity.class);
                intent.putExtra("car_id", car.getVehicleId());
                intent.putExtra("user_id", userId);
                context.startActivity(intent);

                // Animation for button click
                animateButtonClick(v);
            });

            // Set click listener for the entire item
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, CarDetailActivity.class);
                intent.putExtra("car_id", car.getVehicleId());
                intent.putExtra("user_id", userId);
                context.startActivity(intent);

                // Animation for card click
                animateCardClick(v);
            });
        }

        private void animateCardClick(View view) {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.95f, 1.05f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.95f, 1.05f, 1f);
            ObjectAnimator elevation = ObjectAnimator.ofFloat(view, "elevation", 4f, 12f, 4f);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(scaleX, scaleY, elevation);
            animatorSet.setDuration(300);
            animatorSet.setInterpolator(new OvershootInterpolator(1.1f));
            animatorSet.start();
        }

        private void animateButtonClick(View view) {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.9f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.9f, 1f);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(scaleX, scaleY);
            animatorSet.setDuration(200);
            animatorSet.setInterpolator(new OvershootInterpolator(1.2f));
            animatorSet.start();
        }

        private void toggleFavorite(Car car, ImageView favoriteIcon) {
            if (userId == null || userId.isEmpty()) {
                Toast.makeText(context, "Vui lòng đăng nhập để thêm vào yêu thích", Toast.LENGTH_SHORT).show();
                return;
            }

            FavoriteApiService service = RetrofitClient.getFavoriteApiService();
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("user_id", userId);
            requestBody.put("vehicle_id", car.getVehicleId());

            Call<Map<String, Object>> call = service.toggleFavorite(requestBody);
            call.enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Map<String, Object> result = response.body();
                        String status = (String) result.get("status");

                        if ("success".equals(status)) {
                            boolean isNowFavorite = !car.isFavorite();
                            car.setFavorite(isNowFavorite);

                            if (isNowFavorite) {
                                favoriteIcon.setImageResource(R.drawable.ic_heart_filled);
                                Toast.makeText(context, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
                            } else {
                                favoriteIcon.setImageResource(R.drawable.ic_heart_outline);
                                Toast.makeText(context, "Đã xóa khỏi yêu thích", Toast.LENGTH_SHORT).show();
                            }
                            // Animate the favorite icon
                            animateFavoriteClick(favoriteIcon);
                        } else {
                            Toast.makeText(context, "Không thể cập nhật yêu thích", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Lỗi: Không thể cập nhật yêu thích", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void animateFavoriteClick(View view) {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.3f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.3f, 1f);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(scaleX, scaleY);
            animatorSet.setDuration(400);
            animatorSet.setInterpolator(new OvershootInterpolator(1.2f));
            animatorSet.start();
        }
    }
}
