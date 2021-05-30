package ca.limin.restaurantfinder.service;

import ca.limin.restaurantfinder.model.*;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitAPI {

    @GET("textsearch/json")
    Call<Result> getResultFromTextSearch(@Query("query") String query,
                                         @Query("type") String type,
                                         @Query("key") String key,
                                         @Query("radius") Integer radius,
                                         @Query("maxprice") Integer maxPrice,
                                         @Query("opennow") Boolean openNow);

    @GET("details/json")
    Call<ResultDetails> getRestaurantById(@Query("placeid") String placeId,
                                          @Query("key") String key);

    @GET("directions/json")
    Call<Direction> getDirection(@Query("origin") String origin,
                                 @Query("destination") String destination,
                                 @Query("sensor") boolean sensor,
                                 @Query("mode") String drive,
                                 @Query("key") String key);
}
