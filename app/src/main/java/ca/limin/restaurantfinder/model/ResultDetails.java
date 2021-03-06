package ca.limin.restaurantfinder.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ResultDetails {

    @SerializedName("html_attributions")
    @Expose
    private List<String> htmlAttributions = new ArrayList<>();

    @SerializedName("result")
    @Expose
    private RestaurantDetail restaurantDetail;

    @SerializedName("status")
    @Expose
    private String status;

    public List<String> getHtmlAttributions() {
        return htmlAttributions;
    }

    public void setHtmlAttributions(List<String> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    public RestaurantDetail getRestaurantDetail() {
        return restaurantDetail;
    }

    public void setRestaurantDetail(RestaurantDetail restaurantDetail) {
        this.restaurantDetail = restaurantDetail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
