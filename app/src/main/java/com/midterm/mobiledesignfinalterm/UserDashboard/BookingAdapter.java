package com.midterm.mobiledesignfinalterm.UserDashboard;

import android.content.Context;
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
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
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
        this.bookingList = bookingList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        holder.textViewVehicleName.setText(booking.getVehicleName());
        holder.textViewVehicleType.setText(booking.getVehicleType());
        holder.tvPickupLocation.setText(booking.getPickupLocation());
        holder.tvReturnLocation.setText(booking.getReturnLocation());
        holder.textViewPickupInfo.setText(booking.getPickupDate() + ", " + booking.getPickupTime());
        holder.textViewReturnInfo.setText(booking.getReturnDate() + ", " + booking.getReturnTime());

        // Format price in Vietnamese currency format
        NumberFormat currencyFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        String formattedPrice = currencyFormat.format(booking.getFinalPrice()) + " VNĐ";
        holder.textViewPrice.setText(formattedPrice);

        // Style the status text based on booking status
        String status = booking.getStatus();
        holder.textViewStatus.setText(status);
        setStatusColor(holder.textViewStatus, status);

        // Load vehicle image if available
        loadVehicleImage(holder.imageViewVehicle, booking.getVehicleImageUrl());

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBookingClick(booking);
            }
        });
    }

    private void setStatusColor(TextView textView, String status) {
        switch (status.toLowerCase()) {
            case "confirmed":
                textView.setTextColor(context.getResources().getColor(android.R.color.holo_green_light));
                break;
            case "pending":
                textView.setTextColor(context.getResources().getColor(android.R.color.holo_orange_light));
                break;
            case "cancelled":
                textView.setTextColor(context.getResources().getColor(android.R.color.holo_red_light));
                break;
            case "completed":
                textView.setTextColor(context.getResources().getColor(android.R.color.white));
                break;
            case "ongoing":
                textView.setTextColor(context.getResources().getColor(android.R.color.holo_blue_light));
                break;
            default:
                textView.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                break;
        }
    }

    private void loadVehicleImage(ImageView imageView, String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_car_placeholder)
                    .error(R.drawable.ic_car_placeholder)
                    .centerCrop()
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.ic_car_placeholder);
        }
    }

    @Override
    public int getItemCount() {
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
        TextView tvPickupLocation;
        TextView tvReturnLocation;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewVehicle = itemView.findViewById(R.id.imageViewVehicle);
            textViewVehicleName = itemView.findViewById(R.id.textViewVehicleName);
            textViewVehicleType = itemView.findViewById(R.id.textViewVehicleType);
            textViewPickupInfo = itemView.findViewById(R.id.textViewPickupInfo);
            textViewReturnInfo = itemView.findViewById(R.id.textViewReturnInfo);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            tvPickupLocation = itemView.findViewById(R.id.tv_pickupLocationBooking);
            tvReturnLocation = itemView.findViewById(R.id.tv_dropoffLocationBooking);
        }
    }
}
