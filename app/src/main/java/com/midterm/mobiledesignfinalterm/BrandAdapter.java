package com.midterm.mobiledesignfinalterm;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.midterm.mobiledesignfinalterm.R;
import com.midterm.mobiledesignfinalterm.Homepage;

import java.util.List;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.BrandViewHolder> {

    private List<Homepage.Brand> brandList;
    private int selectedPosition = 0; // Default to "All" selected

    public BrandAdapter(List<Homepage.Brand> brandList) {
        this.brandList = brandList;
    }

    @NonNull
    @Override
    public BrandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_brand, parent, false);
        return new BrandViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BrandViewHolder holder, int position) {
        Homepage.Brand brand = brandList.get(position);
        holder.bind(brand, position == selectedPosition);

        holder.itemView.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                Homepage.Brand clickedBrand = brandList.get(currentPosition);
                // Handle click using currentPosition or clickedBrand
                animateItemClick(v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return brandList.size();
    }

    private void animateItemClick(View view) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.1f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.1f, 1f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.setDuration(200);
        animatorSet.setInterpolator(new OvershootInterpolator(1.2f));
        animatorSet.start();
    }

    static class BrandViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewBrand;
        private TextView textViewBrandName;
        private View containerBrand;

        public BrandViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewBrand = itemView.findViewById(R.id.imageViewBrand);
            textViewBrandName = itemView.findViewById(R.id.textViewBrandName);
            containerBrand = itemView.findViewById(R.id.containerBrand);
        }

        public void bind(Homepage.Brand brand, boolean isSelected) {
            imageViewBrand.setImageResource(brand.getIconResource());
            textViewBrandName.setText(brand.getName());

            // Update selection state
            if (isSelected) {
                containerBrand.setBackgroundResource(R.drawable.brand_selected_background);
                textViewBrandName.setTextColor(itemView.getContext().getColor(R.color.black));
            } else {
                containerBrand.setBackgroundResource(R.drawable.brand_background);
                textViewBrandName.setTextColor(itemView.getContext().getColor(R.color.white));
            }
        }
    }
}
