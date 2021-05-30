package ca.limin.restaurantfinder.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ParseException;
import android.os.Bundle;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import ca.limin.restaurantfinder.R;
import ca.limin.restaurantfinder.model.*;
import ca.limin.restaurantfinder.service.APIService;
import ca.limin.restaurantfinder.utils.*;

import java.util.Arrays;
import java.util.Objects;

import ca.limin.restaurantfinder.utils.NetworkChecker;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, APIService.FetchResults,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, View.OnClickListener {

    private final Location location = new Location();
    private GoogleMap googleMap;
    private APIService apiService;
    private IntentFilter connectivityIntentFilter;
    private BottomSheetDialog bottomSheetDialog;
    private View dialogView;
    private Button filterButton;
    private SwitchCompat switchCompat;
    private SeekBar radiusSeekBar;
    private SeekBar priceSeekBar;
    private FloatingActionButton floatingActionButton;
    private Boolean isOpenOnly;
    private Integer maxPrice;
    private Integer radius;
    private Place previousPlace;
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!NetworkChecker.getInstance().isNetworkAvailable(context)) {
                Snackbar.make(findViewById(R.id.main_layout), getString(R.string.no_active_connection), Snackbar.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Places.isInitialized()) {
            Places.initialize(this, this.getString(R.string.google_maps_key));
        }
        setContentView(R.layout.activity_maps);
        initializeMap();
        autoCompleteSearch();
        initializeFloatingAction();
    }

    private void initializeMap() {
        apiService = new APIService(this);
        connectivityIntentFilter = new IntentFilter();
        GooglePlayServiceChecker googlePlayServiceChecker = new GooglePlayServiceChecker();
        if (!googlePlayServiceChecker.checkGooglePlayServices(this)) {
            Snackbar.make(findViewById(R.id.main_layout), getString(R.string.no_google_play_services), Snackbar.LENGTH_SHORT).show();
            finish();
        }
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void initializeFloatingAction() {
        bottomSheetDialog = new BottomSheetDialog(this);
        dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
        bottomSheetDialog.setContentView(dialogView);
        floatingActionButton = findViewById(R.id.fab_filter);
        floatingActionButton.setOnClickListener(this);
        switchCompat = dialogView.findViewById(R.id.openCloseSwitch);
        radiusSeekBar = dialogView.findViewById(R.id.seekbar_radius);
        priceSeekBar = dialogView.findViewById(R.id.seekbar_price);
        filterButton = dialogView.findViewById(R.id.bt_submit_filter);
        filterButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v == floatingActionButton) {
            dialogView.setVisibility(View.VISIBLE);
            bottomSheetDialog.show();
        }else if(v == filterButton) {
            radius = radiusSeekBar.getProgress();
            maxPrice = priceSeekBar.getProgress();
            isOpenOnly = switchCompat.isChecked();
            bottomSheetDialog.dismiss();
            //Get places using filter.
            if (previousPlace == null) {
                apiService.getPlaceSearch(getString(R.string.default_city), getString(R.string.place_type), radius, maxPrice, isOpenOnly);
            } else {
                apiService.getPlaceSearch(previousPlace.getName(), getString(R.string.place_type), radius, maxPrice, isOpenOnly);

            }
        }
    }

    private void autoCompleteSearch() {
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.place_autocomplete);
        autocompleteFragment.setHint(getString(R.string.default_city));
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                previousPlace = place;
                apiService.getPlaceSearch(place.getName(), getString(R.string.place_type), radius, maxPrice, isOpenOnly);
            }

            @Override
            public void onError(@NonNull Status status) {
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnInfoWindowClickListener(this);

        if (!NetworkChecker.getInstance().isNetworkAvailable(this))
            Snackbar.make(findViewById(R.id.main_layout), getString(R.string.no_active_connection), Snackbar.LENGTH_SHORT).show();
        else
            apiService.getPlaceSearch(getString(R.string.default_city), getString(R.string.place_type), radius, maxPrice, isOpenOnly);
    }

    /* OnClick Listener for Markers info windows on the map.
        Clicking will open a new activity with details of the marker.*/
    @Override
    public boolean onMarkerClick(Marker marker) {
        location.setLatitude(marker.getPosition().latitude);
        location.setLongitude(marker.getPosition().longitude);
        Bundle bundle = new Bundle();
        bundle.putSerializable("bundleExtra", location);
        Intent intent = new  Intent(this, NavigationActivity.class);
        intent.putExtra("intentExtra", bundle);
        startActivity(intent);
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(getString(R.string.intent_key_id_tag), Objects.requireNonNull(marker.getTag()).toString());
        if (NetworkChecker.getInstance().isNetworkAvailable(MapsActivity.this))
            startActivity(intent);
    }

    /*
     * Receiver to check information of Network Changes
     */

    @Override
    public void parseResults(Result result) {

        //Empty map for new data points.
        googleMap.clear();

        //Check conditions.
        if (result != null && result.getStatus() != null && result.getStatus().equals("OK")) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Restaurant restaurant : result.getRestaurants()) {
                Location pastLocation = restaurant.getGeometry().getLocation();

                LatLng latLng = new LatLng(pastLocation.getLatitude(), pastLocation.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title(restaurant.getName())
                        .snippet(restaurant.getFormattedAddress());
                Marker marker = googleMap.addMarker(markerOptions);
                marker.setTag(restaurant.getResID());
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                builder.include(marker.getPosition());
            }

            //try block to avoid forming bounds when no locations are selected.
            try {
                LatLngBounds latLngBounds = builder.build();
                int padding = 10;
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(latLngBounds, padding);
                googleMap.animateCamera(cameraUpdate);
            } catch (IllegalStateException | ParseException | NullPointerException e) {
                //Do not animate/move camera.
            }
        } else
            Snackbar.make(findViewById(R.id.main_layout), getString(R.string.error_message), Snackbar.LENGTH_SHORT).show();
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