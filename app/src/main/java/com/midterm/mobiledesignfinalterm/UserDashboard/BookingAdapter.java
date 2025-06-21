package com.midterm.mobiledesignfinalterm.UserDashboard;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.midterm.mobiledesignfinalterm.R;
import com.midterm.mobiledesignfinalterm.models.Booking;

import java.util.ArrayList;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
    private static final String TAG = "BookingAdapter";
    private Context context;
    private List<Booking> bookingList;
    private BookingClickListener listener;

    public interface BookingClickListener {
        void onBookingClick(Booking booking);
    }

    public BookingAdapter(Context context) {
        this.context = context;
        this.bookingList = new ArrayList<>();
    }

    public BookingAdapter(Context context, BookingClickListener listener) {
        this.context = context;
        this.bookingList = new ArrayList<>();
        this.listener = listener;
    }

    public void setBookingList(List<Booking> bookingList) {
        Log.d(TAG, "setBookingList: receiving " + bookingList.size() + " bookings");
        for (int i = 0; i < bookingList.size(); i++) {
            Booking booking = bookingList.get(i);
            Log.d(TAG, "Booking at position " + i + ": ID=" + booking.getBookingId() +
                ", vehicle=" + booking.getVehicleName() + ", status=" + booking.getStatus());
        }
        this.bookingList = bookingList;
        notifyDataSetChanged();
        Log.d(TAG, "notifyDataSetChanged called, adapter item count: " + getItemCount());
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder called");
        View view = LayoutInflater.from(context).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder for position: " + position);
        if (position >= bookingList.size()) {
            Log.e(TAG, "Position out of bounds: position=" + position + ", size=" + bookingList.size());
            return;
        }

        Booking booking = bookingList.get(position);
        Log.d(TAG, "Binding booking ID: " + booking.getBookingId() +
              ", vehicle: " + booking.getVehicleName() +
              ", status: " + booking.getStatus());

        holder.textViewVehicleName.setText(booking.getVehicleName());
        holder.textViewVehicleType.setText(booking.getVehicleType());
        holder.textViewPickupInfo.setText(booking.getPickupDate() + ", " + booking.getPickupTime());
        holder.textViewReturnInfo.setText(booking.getReturnDate() + ", " + booking.getReturnTime());
        holder.textViewPrice.setText("$" + String.format("%.2f", booking.getFinalPrice()));

        // Style the status text based on booking status
        String status = booking.getStatus();
        holder.textViewStatus.setText(status);

        switch (status.toLowerCase()) {
            case "confirmed":
                holder.textViewStatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_light));
                break;
            case "pending":
                holder.textViewStatus.setTextColor(context.getResources().getColor(android.R.color.holo_orange_light));
                break;
            case "cancelled":
                holder.textViewStatus.setTextColor(context.getResources().getColor(android.R.color.holo_red_light));
                break;
            case "completed":
                holder.textViewStatus.setTextColor(context.getResources().getColor(android.R.color.white));
                break;
            case "ongoing":
                holder.textViewStatus.setTextColor(context.getResources().getColor(android.R.color.holo_blue_light));
                break;
            default:
                holder.textViewStatus.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                break;
        }

        // Load vehicle image if available
        if (booking.getVehicleImageUrl() != null && !booking.getVehicleImageUrl().isEmpty()) {
            Log.d(TAG, "Loading image from: " + booking.getVehicleImageUrl());
            Glide.with(context)
                    .load(booking.getVehicleImageUrl())
                    .placeholder(R.drawable.ic_car_placeholder)
                    .error(R.drawable.ic_car_placeholder)
                    .centerCrop()
                    .into(holder.imageViewVehicle);
        } else {
            // Use a placeholder if no image URL
            holder.imageViewVehicle.setImageResource(R.drawable.ic_car_placeholder);
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBookingClick(booking);
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount called: returning " + bookingList.size());
        return bookingList.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewVehicle;
        TextView textViewVehicleName;
        TextView textViewVehicleType;
        TextView textViewPickupInfo;
        TextView textViewReturnInfo;
        TextView textViewPrice;
        TextView textViewStatus;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewVehicle = itemView.findViewById(R.id.imageViewVehicle);
            textViewVehicleName = itemView.findViewById(R.id.textViewVehicleName);
            textViewVehicleType = itemView.findViewById(R.id.textViewVehicleType);
            textViewPickupInfo = itemView.findViewById(R.id.textViewPickupInfo);
            textViewReturnInfo = itemView.findViewById(R.id.textViewReturnInfo);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
        }
    }
}
