package ca.limin.restaurantfinder.service;

import androidx.annotation.NonNull;

import ca.limin.restaurantfinder.model.Result;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class APIService {

    private final RetrofitAPI retrofitAPI = ServiceCreator.createService(RetrofitAPI.class);
    private final FetchResults fetchResults;
    public APIService(FetchResults fetchResults) {
        this.fetchResults = fetchResults;
    }

    public void getPlaceSearch(String query, String type, Integer radius, Integer maxPrice, Boolean openOnly) {
        String KEY = "AIzaSyBSILH_Vl30Un8p7VaSOUcwNxgFvfiqUeg";
        Call<Result> callTextSearch = retrofitAPI.getResultFromTextSearch(query, type, KEY, radius, maxPrice, openOnly);
        callTextSearch.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(@NonNull Call<Result> call, @NonNull Response<Result> response) {
                if (response.isSuccessful() && response.body() != null) {
                    fetchResults.parseResults(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result> call, @NonNull Throwable t) {
            }
        });

    }

    public interface FetchResults {
        void parseResults(Result result);
    }
}
