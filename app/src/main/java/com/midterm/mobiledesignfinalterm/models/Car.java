package com.midterm.mobiledesignfinalterm.models;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.SerializedName;
import com.midterm.mobiledesignfinalterm.CarDetail.Amenity;

public class Car {
    @SerializedName("vehicle_id")
    private int vehicle_id;
    @SerializedName("model")
    private String model;
    @SerializedName("name")
    private String name;
    @SerializedName("rating")
    private float rating;
    @SerializedName("total_trips")
    private int total_trips;
    @SerializedName("location")
    private String location;
    @SerializedName("transmission")
    private String transmission;
    @SerializedName("seats")
    private int seats;
    @SerializedName("fuel_type")
    private String fuel_type;
    @SerializedName("base_price")
    private double base_price;
    @SerializedName("vehicle_type")
    private String vehicle_type;
    @SerializedName("description")
    private String description;
    @SerializedName("status")
    private String status;
    @SerializedName("is_favorite")
    private boolean is_favorite;
    @SerializedName("primary_image")
    private String primary_image;
    private String price_formatted;
    private boolean is_favorite_for_user;
    private String lessor_name;
    private double insurance_price;
    private String insurance_price_formatted;
    @SerializedName("total_price")
    private double total_price;
    @SerializedName("total_price_formatted")
    private String total_price_formatted;
    @SerializedName("brand")
    private String brand;

    public String getFuel_consumption() {
        return fuel_consumption;
    }

    public void setFuel_consumption(String fuel_consumption) {
        this.fuel_consumption = fuel_consumption;
    }

    @SerializedName("fuel_consumption")
    private String fuel_consumption;

    private List<CarImage> images = new ArrayList<>();
    private List<Amenity> amenities = new ArrayList<>();

    // Getters and setters
    public int getVehicleId() {
        return vehicle_id;
    }

    public void setVehicleId(int vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public String getBrandCar() {
        return brand != null ? brand : "";
    }

    public void setBrandCar(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model != null ? model : "";
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getTotalTrips() {
        return total_trips;
    }

    public void setTotalTrips(int total_trips) {
        this.total_trips = total_trips;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public String getFuelType() {
        return fuel_type;
    }

    public void setFuelType(String fuel_type) {
        this.fuel_type = fuel_type;
    }

    public double getBasePrice() {
        return base_price;
    }

    public void setBasePrice(double base_price) {
        this.base_price = base_price;
    }

    public String getVehicleType() {
        return vehicle_type;
    }

    public void setVehicleType(String vehicle_type) {
        this.vehicle_type = vehicle_type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isFavorite() {
        return is_favorite;
    }

    public void setFavorite(boolean is_favorite) {
        this.is_favorite = is_favorite;
    }

    public String getPrimaryImage() {
        return primary_image;
    }

    public void setPrimaryImage(String primary_image) {
        this.primary_image = primary_image;
    }

    public String getPriceFormatted() {
        return price_formatted;
    }

    public void setPriceFormatted(String price_formatted) {
        this.price_formatted = price_formatted;
    }

    public boolean isFavoriteForUser() {
        return is_favorite_for_user;
    }

    public void setFavoriteForUser(boolean is_favorite_for_user) {
        this.is_favorite_for_user = is_favorite_for_user;
    }

    public List<CarImage> getImages() {
        return images;
    }

    public void setImages(List<CarImage> images) {
        this.images = images;
    }

    public List<Amenity> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<Amenity> amenities) {
        this.amenities = amenities;
    }

    public String getLessorName() {
        return lessor_name;
    }

    public void setLessorName(String lessor_name) {
        this.lessor_name = lessor_name;
    }

    public double getInsurancePrice() {
        return insurance_price;
    }

    public void setInsurancePrice(double insurance_price) {
        this.insurance_price = insurance_price;
    }

    public String getInsurancePriceFormatted() {
        return insurance_price_formatted;
    }

    public void setInsurancePriceFormatted(String insurance_price_formatted) {
        this.insurance_price_formatted = insurance_price_formatted;
    }

    public double getTotalPrice() {
        return total_price;
    }

    public void setTotalPrice(double total_price) {
        this.total_price = total_price;
    }

    public String getTotalPriceFormatted() {
        return total_price_formatted;
    }

    public void setTotalPriceFormatted(String total_price_formatted) {
        this.total_price_formatted = total_price_formatted;
    }

    public String getFullName() {
        return (brand != null ? brand : "") + " " + (model != null ? model : "");
    }
    public String getFormattedSeats() {
        return seats + " People";
    }

    public String getFormattedConsumption() {
        return fuel_consumption != null ? fuel_consumption : "N/A";
    }
}
