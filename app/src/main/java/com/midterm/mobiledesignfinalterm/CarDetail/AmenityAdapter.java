package com.midterm.mobiledesignfinalterm.CarDetail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.midterm.mobiledesignfinalterm.R;

import java.util.List;

public class AmenityAdapter extends RecyclerView.Adapter<AmenityAdapter.AmenityViewHolder> {

    private final Context context;
    private List<Amenity> amenityList;

    public AmenityAdapter(Context context, List<Amenity> amenityList) {
        this.context = context;
        this.amenityList = amenityList;
    }
    public void setAmenities(List<Amenity> amenities) {
        this.amenityList = amenities;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AmenityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.amenity_item, parent, false);
        return new AmenityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AmenityViewHolder holder, int position) {
        Amenity amenity = amenityList.get(position);
        holder.tvAmenityName.setText(amenity.getName());
        holder.imgAmenityIcon.setImageResource(amenity.getIconResId());
    }

    @Override
    public int getItemCount() {
        return amenityList.size();
    }

    public static class AmenityViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAmenityIcon;
        TextView tvAmenityName;

        public AmenityViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAmenityIcon = itemView.findViewById(R.id.iv_iconAmenity);
            tvAmenityName = itemView.findViewById(R.id.tv_amenityName);
        }
    }
}
