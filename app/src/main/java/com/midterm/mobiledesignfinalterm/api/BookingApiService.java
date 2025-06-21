package com.midterm.mobiledesignfinalterm.api;

import com.midterm.mobiledesignfinalterm.models.ApiResponse;
import com.midterm.mobiledesignfinalterm.models.Booking;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Interface định nghĩa các API endpoints liên quan đến booking
 */
public interface BookingApiService {

    /**
     * Lấy danh sách booking của người dùng
     */
    @GET("bookings.php")
    Call<Map<String, Object>> getUserBookings(
            @Query("limit") Integer limit,
            @Query("offset") Integer offset,
            @Query("status") String status
    );

    /**
     * Lấy thông tin chi tiết của booking theo ID
     * @param bookingId ID của booking cần lấy thông tin
     */
    @GET("bookings.php")
    Call<Booking> getBookingDetails(@Query("id") int bookingId);

    /**
     * Tạo booking mới
     */
    @POST("bookings.php")
    Call<Map<String, Object>> createBooking(@Body Map<String, Object> bookingData);

    /**
     * Cập nhật trạng thái booking
     */
    @FormUrlEncoded
    @POST("bookings.php")
    Call<Map<String, Object>> updateBookingStatus(
            @Field("action") String action,
            @Field("booking_id") int bookingId,
            @Field("status") String status
    );

    /**
     * Hủy booking
     */
    @FormUrlEncoded
    @POST("bookings.php")
    Call<Map<String, Object>> cancelBooking(
            @Field("action") String action,
            @Field("booking_id") int bookingId
    );

    /**
     * Tìm kiếm booking
     */
    @GET("bookings.php")
    Call<Map<String, Object>> searchBookings(
            @Query("search") String search,
            @Query("status") String status,
            @Query("vehicle_type") String vehicleType,
            @Query("date_from") String dateFrom,
            @Query("date_to") String dateTo
    );
}
