package ca.limin.restaurantfinder.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class Result {

    @SerializedName("html_attributions")
    @Expose
    private List<String> htmlAttributions = new ArrayList<>();

    @SerializedName("next_page_token")
    @Expose
    private String nextPageToken;

    @SerializedName("results")
    @Expose
    private List<Restaurant> restaurants = new ArrayList<>();

    @SerializedName("status")
    @Expose
    private String status;

    public List<String> getHtmlAttributions() {
        return htmlAttributions;
    }

    public void setHtmlAttributions(List<String> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public List<Restaurant> getRestaurants() {
        return restaurants;
    }

    public void setRestaurants(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
