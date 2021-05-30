package ca.limin.restaurantfinder.controller;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;
import androidx.databinding.DataBindingUtil;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

import ca.limin.restaurantfinder.R;
import ca.limin.restaurantfinder.databinding.ActivityDetailBinding;
import ca.limin.restaurantfinder.model.*;
import ca.limin.restaurantfinder.service.*;
import ca.limin.restaurantfinder.utils.NetworkChecker;
import ca.limin.restaurantfinder.view.ViewPagerAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {
    private String resId;
    private RetrofitAPI retrofitAPI;
    private ProgressDialog progressDialog;
    private IntentFilter connectivityIntentFilter;
    private List<String> photoLinks = new ArrayList<>();
    private RestaurantDetail restaurantDetail = new RestaurantDetail();
    ActivityDetailBinding activityDetailBinding;
    /**
     * Receiver to check information of Network Changes
     */

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!NetworkChecker.getInstance().isNetworkAvailable(context)) {
                Snackbar.make(activityDetailBinding.scrolling, getString(R.string.no_active_connection), Snackbar.LENGTH_SHORT).show();
            } else {
                progressDialog.show();
                if (resId != null) getRestaurantById(resId);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        activityDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        connectivityIntentFilter = new IntentFilter();
        resId = getIntent().getStringExtra(getString(R.string.intent_key_id_tag));
        retrofitAPI = ServiceCreator.createService(RetrofitAPI.class);
        progressDialog = new ProgressDialog(this);
        progressDialog.show();

        if (resId != null) getRestaurantById(resId);
        /*
         * Open URL Intent
         */
        activityDetailBinding.relWebsite.setOnClickListener(v -> {
            if (restaurantDetail.getWebsite() != null) {
                String uri = restaurantDetail.getWebsite().trim();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        });
        /*
         * Open call dialer Intent
         */
        activityDetailBinding.relContact.setOnClickListener(v -> {
            if (restaurantDetail.getFormattedPhone() != null) {
                String uri = "tel:" + restaurantDetail.getFormattedPhone().trim();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        });

        /*
         * Handle swipe and refresh
         */

        activityDetailBinding.swipeRefresh.setOnRefreshListener(() -> {
            if (resId != null) {
                progressDialog.show();
                getRestaurantById(resId);
            }
        });

    }

    /**
     * Call to get Restaurant details via retrofit
     */
    private void getRestaurantById(String placeId) {
        Call<ResultDetails> detailsCall = retrofitAPI.getRestaurantById(placeId, this.getString(R.string.google_maps_key));
        detailsCall.enqueue(new Callback<ResultDetails>() {

            @Override
            public void onResponse(@NonNull Call<ResultDetails> call, @NonNull Response<ResultDetails> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    restaurantDetail = response.body().getRestaurantDetail();
                    if (restaurantDetail != null) {
                        fillInformation(restaurantDetail);
                    }
                }
                activityDetailBinding.swipeRefresh.setRefreshing(false);
                progressDialog.dismiss();
                setUpPhotos();
            }

            @Override
            public void onFailure(@NonNull Call<ResultDetails> call, @NonNull Throwable t) {
                activityDetailBinding.swipeRefresh.setRefreshing(false);
                progressDialog.dismiss();
            }
        });
    }

    /**
     * Load photos into viewpager and retrofit returns ArrayList of photo references
     */
    private void setUpPhotos() {
        ViewPager2 viewPager2 = activityDetailBinding.viewpager;
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, photoLinks);
        viewPager2.setAdapter(viewPagerAdapter);
    }

    private void fillInformation(RestaurantDetail restaurantDetail) {
        photoLinks = new ArrayList<>();
        activityDetailBinding.setRestaurantDetail(restaurantDetail);

        if (restaurantDetail.getPriceLevel() == null)
            activityDetailBinding.ratingPrice.setVisibility(View.GONE);

        if (restaurantDetail.getRating() == null)
            activityDetailBinding.ratingReview.setVisibility(View.GONE);

        if (restaurantDetail.getWebsite() == null)
            activityDetailBinding.relWebsite.setVisibility(View.GONE);

        if (restaurantDetail.getPhotos() != null) {
            for (Photo photo : restaurantDetail.getPhotos()) {
                photoLinks.add(photo.getPhotoReference());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, connectivityIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
}