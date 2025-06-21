package com.midterm.mobiledesignfinalterm.homepage;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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

import java.util.List;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {

    private List<Homepage.Car> carList;

    public CarAdapter(List<Homepage.Car> carList) {
        this.carList = carList;
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_car, parent, false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        Homepage.Car car = carList.get(position);
        holder.bind(car);

        // Set click listener for the entire item
        holder.itemView.setOnClickListener(v -> {
            animateItemClick(v);
            // Mock navigation to car details since we don't have real IDs for mock cars
            Toast.makeText(v.getContext(), "Selected: " + car.getName(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

    private void animateItemClick(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.95f, 1.05f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.95f, 1.05f, 1f);
        ObjectAnimator elevation = ObjectAnimator.ofFloat(view, "elevation", 4f, 12f, 4f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, elevation);
        animatorSet.setDuration(300);
        animatorSet.setInterpolator(new OvershootInterpolator(1.1f));
        animatorSet.start();
    }

    static class CarViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_primaryImageCarHomePage;
        private TextView tv_cardCarNameHomePage;
        private TextView tv_cardCarTypeHomePage;
        private TextView tv_CardFuelTypeHomePage;
        private TextView tv_CardTransmissionHomePage;
        private TextView tv_SeatsHomePage;
        private TextView tv_cardConsumptionHomePage;
        private TextView tv_BasePriceHomePage;
        private ImageView iv_FavoriteHomePage;
        private Button btn_rentalNowHomePage;

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views using the correct IDs from item_car.xml
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

        public void bind(Homepage.Car car) {
            // Set car data to views
            tv_cardCarNameHomePage.setText(car.getName());
            tv_cardCarTypeHomePage.setText("SUV"); // Mock data as it's not available in Homepage.Car
            tv_SeatsHomePage.setText(car.getSeats());
            tv_BasePriceHomePage.setText(car.getPrice());

            // Set some default values for mock data
            tv_CardFuelTypeHomePage.setText("Xăng"); // Default fuel type
            tv_CardTransmissionHomePage.setText("Số tự động"); // Default transmission
            tv_cardConsumptionHomePage.setText("6.5L/100km"); // Default consumption

            // Set car image
            iv_primaryImageCarHomePage.setImageResource(car.getImageResource());

            // Handle favorite button click
            iv_FavoriteHomePage.setOnClickListener(v -> {
                animateFavoriteClick(v);
                // Toggle favorite icon for demo
                boolean isFavorite = iv_FavoriteHomePage.getTag() != null &&
                                     (boolean) iv_FavoriteHomePage.getTag();

                if (isFavorite) {
                    iv_FavoriteHomePage.setImageResource(R.drawable.ic_heart_outline);
                    iv_FavoriteHomePage.setTag(false);
                } else {
                    iv_FavoriteHomePage.setImageResource(R.drawable.ic_heart_filled);
                    iv_FavoriteHomePage.setTag(true);
                }
            });

            // Set click listener for rental button
            btn_rentalNowHomePage.setOnClickListener(v -> {
                Toast.makeText(v.getContext(), "Selected: " + car.getName(), Toast.LENGTH_SHORT).show();
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
