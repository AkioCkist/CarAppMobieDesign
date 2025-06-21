package com.midterm.mobiledesignfinalterm.models;

public class Booking {
    private int bookingId;
    private String vehicleName;
    private String vehicleType;
    private String pickupDate;
    private String pickupTime;
    private String returnDate;
    private String returnTime;
    private String pickupLocation;
    private String returnLocation;
    private double totalPrice;
    private double finalPrice;
    private String status;
    private String vehicleImageUrl;

    public Booking() {
    }

    public Booking(int bookingId, String vehicleName, String vehicleType, String pickupDate,
                  String pickupTime, String returnDate, String returnTime,
                  String pickupLocation, String returnLocation,
                  double totalPrice, double finalPrice, String status) {
        this.bookingId = bookingId;
        this.vehicleName = vehicleName;
        this.vehicleType = vehicleType;
        this.pickupDate = pickupDate;
        this.pickupTime = pickupTime;
        this.returnDate = returnDate;
        this.returnTime = returnTime;
        this.pickupLocation = pickupLocation;
        this.returnLocation = returnLocation;
        this.totalPrice = totalPrice;
        this.finalPrice = finalPrice;
        this.status = status;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(String pickupDate) {
        this.pickupDate = pickupDate;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public String getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(String returnTime) {
        this.returnTime = returnTime;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getReturnLocation() {
        return returnLocation;
    }

    public void setReturnLocation(String returnLocation) {
        this.returnLocation = returnLocation;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVehicleImageUrl() {
        return vehicleImageUrl;
    }

    public void setVehicleImageUrl(String vehicleImageUrl) {
        this.vehicleImageUrl = vehicleImageUrl;
    }
}
