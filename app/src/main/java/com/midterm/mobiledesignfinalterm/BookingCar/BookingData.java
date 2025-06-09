package com.midterm.mobiledesignfinalterm.BookingCar;

import java.io.Serializable;

public class BookingData implements Serializable {
    private String bookingId;
    private String pickupLocation;
    private String dropoffLocation;
    private String pickupDate;
    private String pickupTime;
    private String dropoffDate;
    private String dropoffTime;
    private String fullName;
    private String email;
    private String phone;
    private String aadharNumber;
    private String panNumber;
    private String paymentMethod;
    private double totalAmount;
    private long bookingTimestamp;

    public BookingData() {
        this.bookingTimestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getDropoffLocation() {
        return dropoffLocation;
    }

    public void setDropoffLocation(String dropoffLocation) {
        this.dropoffLocation = dropoffLocation;
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

    public String getDropoffDate() {
        return dropoffDate;
    }

    public void setDropoffDate(String dropoffDate) {
        this.dropoffDate = dropoffDate;
    }

    public String getDropoffTime() {
        return dropoffTime;
    }

    public void setDropoffTime(String dropoffTime) {
        this.dropoffTime = dropoffTime;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAadharNumber() {
        return aadharNumber;
    }

    public void setAadharNumber(String aadharNumber) {
        this.aadharNumber = aadharNumber;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public long getBookingTimestamp() {
        return bookingTimestamp;
    }

    public void setBookingTimestamp(long bookingTimestamp) {
        this.bookingTimestamp = bookingTimestamp;
    }

    @Override
    public String toString() {
        return "BookingData{" +
                "bookingId='" + bookingId + '\'' +
                ", pickupLocation='" + pickupLocation + '\'' +
                ", dropoffLocation='" + dropoffLocation + '\'' +
                ", pickupDate='" + pickupDate + '\'' +
                ", pickupTime='" + pickupTime + '\'' +
                ", fullName='" + fullName + '\'' +
                ", totalAmount=" + totalAmount +
                '}';
    }
}