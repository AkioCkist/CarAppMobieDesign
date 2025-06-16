package com.midterm.mobiledesignfinalterm.models;

import com.google.gson.annotations.SerializedName;

public class CarImage {
    @SerializedName("image_id")
    private int image_id;
    @SerializedName("vehicle_id")
    private int vehicle_id;
    @SerializedName("image_url")
    private String image_url;
    @SerializedName("is_primary")
    private boolean is_primary;
    @SerializedName("display_order")
    private int display_order;

    public CarImage() {}

    public CarImage(int image_id, int vehicle_id, String image_url, boolean is_primary, int display_order) {
        this.image_id = image_id;
        this.vehicle_id = vehicle_id;
        this.image_url = image_url;
        this.is_primary = is_primary;
        this.display_order = display_order;
    }

    public int getImageId() {
        return image_id;
    }

    public void setImageId(int image_id) {
        this.image_id = image_id;
    }

    public int getVehicleId() {
        return vehicle_id;
    }

    public void setVehicleId(int vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public String getImageUrl() {
        return image_url;
    }

    public void setImageUrl(String image_url) {
        this.image_url = image_url;
    }

    public boolean isPrimary() {
        return is_primary;
    }

    public void setPrimary(boolean is_primary) {
        this.is_primary = is_primary;
    }

    public int getDisplayOrder() {
        return display_order;
    }

    public void setDisplayOrder(int display_order) {
        this.display_order = display_order;
    }
}
