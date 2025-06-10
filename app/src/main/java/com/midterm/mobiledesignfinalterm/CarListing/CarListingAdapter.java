package com.midterm.mobiledesignfinalterm.CarListing;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.midterm.mobiledesignfinalterm.R;

import java.util.List;

public class CarListingAdapter extends RecyclerView.Adapter<CarListingAdapter.CarViewHolder> {

    private List<CarListing.CarItem> carList;
    private OnCarItemClickListener listener;

    public interface OnCarItemClickListener {
        void onRentalClick(CarListing.CarItem car);
        void onFavoriteClick(CarListing.CarItem car, int position);
    }

    public CarListingAdapter(List<CarListing.CarItem> carList, OnCarItemClickListener listener) {
        this.carList = carList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_car_listing, parent, false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        CarListing.CarItem car = carList.get(position);
        holder.bind(car, position);

        // Add entrance animation for each item
        animateItemEntrance(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return carList.size();
    }

    private void animateItemEntrance(View view, int position) {
        view.setAlpha(0f);
        view.setTranslationY(100f);
        view.setScaleX(0.8f);
        view.setScaleY(0.8f);

        view.animate()
                .alpha(1f)
                .translationY(0f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(500)
                .setStartDelay(position * 100)
                .setInterpolator(new OvershootInterpolator(1.1f))
                .start();
    }

    public class CarViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewCarName;
        private TextView textViewCarType;
        private TextView textViewFuelType;
        private TextView textViewTransmission;
        private TextView textViewSeats;
        private TextView textViewConsumption;
        private TextView textViewPrice;
        private TextView textViewOriginalPrice;
        private ImageView imageViewCar;
        private ImageView imageViewFavorite;
        private Button buttonRentalNow;

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewCarName = itemView.findViewById(R.id.textViewCarName);
            textViewCarType = itemView.findViewById(R.id.textViewCarType);
            textViewFuelType = itemView.findViewById(R.id.textViewFuelType);
            textViewTransmission = itemView.findViewById(R.id.textViewTransmission);
            textViewSeats = itemView.findViewById(R.id.textViewSeats);
            textViewConsumption = itemView.findViewById(R.id.textViewConsumption);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            textViewOriginalPrice = itemView.findViewById(R.id.textViewOriginalPrice);
            imageViewCar = itemView.findViewById(R.id.imageViewCar);
            imageViewFavorite = itemView.findViewById(R.id.imageViewFavorite);
            buttonRentalNow = itemView.findViewById(R.id.buttonRentalNow);
        }

        public void bind(CarListing.CarItem car, int position) {
            // Set car data
            textViewCarName.setText(car.getName());
            textViewCarType.setText(car.getType());
            textViewFuelType.setText(car.getFuelType());
            textViewTransmission.setText(car.getTransmission());
            textViewSeats.setText(car.getSeats());
            textViewConsumption.setText(car.getConsumption());
            textViewPrice.setText(car.getPrice());
            textViewOriginalPrice.setText(car.getOriginalPrice());
            imageViewCar.setImageResource(car.getImageResource());

            // Set favorite state
            updateFavoriteIcon(car.isFavorite());

            // Set click listeners
            imageViewFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    animateFavoriteClick(v);
                    if (listener != null) {
                        listener.onFavoriteClick(car, position);
                    }
                }
            });

            buttonRentalNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    animateRentalButtonClick(v);
                    if (listener != null) {
                        listener.onRentalClick(car);
                    }
                }
            });

            // Add click animation for the entire card
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    animateCardClick(v);
                    if (listener != null) {
                        listener.onRentalClick(car);
                    }
                }
            });
        }

        private void updateFavoriteIcon(boolean isFavorite) {
            if (isFavorite) {
                imageViewFavorite.setImageResource(R.drawable.ic_heart_filled);
                imageViewFavorite.setColorFilter(itemView.getContext().getResources().getColor(android.R.color.holo_red_light));
            } else {
                imageViewFavorite.setImageResource(R.drawable.ic_heart_outline);
                imageViewFavorite.setColorFilter(itemView.getContext().getResources().getColor(android.R.color.white));
            }
        }

        private void animateFavoriteClick(View view) {
            // Heart animation chỉ scale và xoay, không thay đổi elevation
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.5f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.5f, 1f);
            ObjectAnimator rotation = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(scaleX, scaleY, rotation);
            animatorSet.setDuration(600);
            animatorSet.setInterpolator(new OvershootInterpolator(1.5f));
            animatorSet.start();
        }

        private void animateRentalButtonClick(View view) {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.9f, 1.1f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.9f, 1.1f, 1f);
            ObjectAnimator elevation = ObjectAnimator.ofFloat(view, "elevation", 4f, 12f, 4f);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(scaleX, scaleY, elevation);
            animatorSet.setDuration(400);
            animatorSet.start();
        }

        private void animateCardClick(View view) {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.02f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.02f, 1f);
            ObjectAnimator elevation = ObjectAnimator.ofFloat(view, "elevation", 4f, 8f, 4f);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(scaleX, scaleY, elevation);
            animatorSet.setDuration(200);
            animatorSet.start();
        }
    }

    // Method to update the list (for search/filter functionality)
    public void updateList(List<CarListing.CarItem> newList) {
        this.carList = newList;
        notifyDataSetChanged();
    }

    // Method to add new items (for pagination)
    public void addItems(List<CarListing.CarItem> newItems) {
        int startPosition = carList.size();
        carList.addAll(newItems);
        notifyItemRangeInserted(startPosition, newItems.size());
    }

    // Method to clear all items
    public void clearItems() {
        carList.clear();
        notifyDataSetChanged();
    }
}
