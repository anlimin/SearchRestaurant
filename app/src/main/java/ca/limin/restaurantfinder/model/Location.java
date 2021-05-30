package ca.limin.restaurantfinder.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Model class to store lat, lng.
 */

public class Location implements Serializable {

    @SerializedName("lat")
    @Expose
    private double latitude;

    @SerializedName("lng")
    @Expose
    private double longitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
