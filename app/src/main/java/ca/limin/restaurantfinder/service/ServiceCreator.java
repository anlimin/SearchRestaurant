package ca.limin.restaurantfinder.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceCreator {

    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/";

    private static final HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

    private static final OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder().addInterceptor(loggingInterceptor);

    private static final Gson gson = new GsonBuilder().create();

    private static final Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson));
    private static final Retrofit retrofit = retrofitBuilder.client(okHttpBuilder.build()).build();

    static Retrofit getRetrofit() {
        return retrofit;
    }

    public static <S> S createService(Class<S> retrofitAPI) {
        return retrofit.create(retrofitAPI);
    }

}
