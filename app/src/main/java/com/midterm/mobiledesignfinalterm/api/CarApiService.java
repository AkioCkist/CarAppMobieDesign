package com.midterm.mobiledesignfinalterm.api;

import com.midterm.mobiledesignfinalterm.models.ApiResponse;
import com.midterm.mobiledesignfinalterm.models.Car;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interface định nghĩa các API endpoints liên quan đến xe
 */
public interface CarApiService {

    /**
     * Lấy danh sách tất cả các xe
     */
    @GET("get_all_cars.php")
    Call<ApiResponse<List<Car>>> getAllCars();

    /**
     * Lấy thông tin chi tiết của xe theo ID
     * @param carId ID của xe cần lấy thông tin
     */
    @GET("get_car_details.php")
    Call<ApiResponse<Car>> getCarDetails(@Query("car_id") int carId);
}