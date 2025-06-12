package com.midterm.mobiledesignfinalterm.CarDetail;

import com.midterm.mobiledesignfinalterm.R;

public class Amenity {
    private int id;
    private String name;
    private String icon;
    private String description;

    public Amenity(int id, String name, String icon, String description) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIconResId() {
        // Get drawable resource id from icon name
        return getResourceId(icon);
    }

    private int getResourceId(String iconName) {
        // Map icon names from database to drawable resources
        switch (iconName.toLowerCase()) {
            case "bluetooth":
                return R.drawable.cardetail_ic_bluetooth;
            case "camera":
                return R.drawable.cardetail_ic_adventurecamera;
            case "airbag":
                return R.drawable.cardetail_ic_airbag;
            case "etc":
                return R.drawable.cardetail_ic_etc;
            case "sunroof":
                return R.drawable.cardetail_ic_carroof;
            case "sportmode":
                return R.drawable.cardetail_ic_sportmode;
            case "tablet":
                return R.drawable.cardetail_ic_screencar;
            case "camera360":
                return R.drawable.cardetail_ic_camera360;
            case "map":
                return R.drawable.cardetail_ic_map;
            case "rotateccw":
                return R.drawable.cardetail_ic_rearviewcamera;
            case "circle":
                return R.drawable.cardetail_ic_cartire;
            case "package":
                return R.drawable.cardetail_ic_cartrunk;
            case "shield":
                return R.drawable.cardetail_ic_collisionsensor;
            case "radar":
                return R.drawable.cardetail_ic_reversesenser;
            case "childseat":
                return R.drawable.cardetail_ic_childseat;
            default:
                return R.drawable.cardetail_ic_default;
        }
    }
}
