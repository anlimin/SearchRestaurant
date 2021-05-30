package ca.limin.restaurantfinder.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Model class for storing Geometry.
 */
public class Geometry implements Serializable {

    @SerializedName("location")
    @Expose
    private Location location;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
