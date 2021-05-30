package ca.limin.restaurantfinder.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import ca.limin.restaurantfinder.R;
import ca.limin.restaurantfinder.model.*;
import ca.limin.restaurantfinder.service.*;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import org.jetbrains.annotations.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NavigationActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentLocation;
    ca.limin.restaurantfinder.model.Location destination;
    private RetrofitAPI retrofitAPI;
    private Direction direction = new Direction();
    private static final int LOCATION_REQUEST_CODE = 101;
    Button navigate, zoomIn, zoomOut;
    LatLng destinationLatLng;
    GoogleMap googleMap;
    String sourceLatitude, sourceLongitude, destinationLatitude, destinationLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(NavigationActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(NavigationActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            return;
        }
        initialize();
        fetchCurrentLocation();
    }

    private void initialize() {
        retrofitAPI = DirectionServiceCreator.createService(RetrofitAPI.class);
        navigate = findViewById(R.id.back_Nav_btn);
        zoomIn = findViewById(R.id.zoomin);
        zoomOut = findViewById(R.id.zoomout);
        navigate.setOnClickListener(this);
        zoomIn.setOnClickListener(this);
        zoomOut.setOnClickListener(this);
    }

    private void fetchCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    Toast.makeText(NavigationActivity.this,currentLocation.getLatitude()+" "+currentLocation.getLongitude(),Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment= (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
                    supportMapFragment.getMapAsync(NavigationActivity.this);
                }else{
                    Toast.makeText(NavigationActivity.this,"No Location recorded",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResult) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResult);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                fetchCurrentLocation();
            } else {
                Toast.makeText(NavigationActivity.this, "Location permission missing", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onClick(View v) {
        if(v == navigate)
        {
            String uri = "http://maps.google.com/maps?saddr=" + sourceLatitude + "," + sourceLongitude + "&daddr=" + destinationLatitude + "," + destinationLongitude;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            startActivity(intent);
        }
        else if(v == zoomIn)
            googleMap.moveCamera(CameraUpdateFactory.zoomIn());
        else
            googleMap.moveCamera(CameraUpdateFactory.zoomOut());
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;

        getLocations();
        LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        destinationLatLng = new LatLng(destination.getLatitude(),destination.getLongitude());
        addCurrentLocationMarker(currentLatLng);
        addDestinationMarker(destinationLatLng);
        getDirection(currentLatLng.latitude +","+ currentLatLng.longitude, destinationLatLng.latitude+","+ destinationLatLng.longitude, false, "driving");
    }

    private void getLocations () {
        Bundle bundle = getIntent().getBundleExtra("intentExtra");
        Serializable bundleLocation = bundle.getSerializable("bundleExtra");
        destination = (ca.limin.restaurantfinder.model.Location) bundleLocation;
        sourceLatitude = String.valueOf(currentLocation.getLatitude());
        sourceLongitude = String.valueOf(currentLocation.getLongitude());
        destinationLatitude = String.valueOf(destination.getLatitude());
        destinationLongitude = String.valueOf(destination.getLongitude());
    }

    private void addCurrentLocationMarker (LatLng currentLatLng) {
        MarkerOptions markerOptions = new MarkerOptions().position(currentLatLng);
        googleMap.addMarker(markerOptions);
        googleMap.setMyLocationEnabled(true);
    }

    private void addDestinationMarker  (LatLng destinationLatLng) {
        googleMap.addMarker( new MarkerOptions().position(destinationLatLng).title("Destination"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destinationLatLng,18));
    }

    private void getDirection(String origin, String destination, boolean sensor, String mode){
        Call<Direction> callDirection = retrofitAPI.getDirection(origin, destination,sensor, mode, this.getString(R.string.google_maps_key));
        callDirection.enqueue(new Callback<Direction>() {
            @Override
            public void onResponse(@NotNull Call<Direction> call, @NotNull Response<Direction> response) {
                if (response.isSuccessful() && response.body() != null) {
                    direction = response.body();
                    List<List<HashMap<String,String>>> routes;
                    DirectionParse directionParse = new DirectionParse();
                    routes = directionParse.parse(direction);
                    addPolyline(routes);
                }
            }

            @Override
            public void onFailure(Call<Direction> call, Throwable t) {
            }
        });
    }

    private void addPolyline(List<List<HashMap<String,String>>> routes) {
        List<LatLng> points;
        PolylineOptions polylineOptions = null;
        for (List<HashMap<String,String>> paths : routes)
        {
            points = new ArrayList<>();
            polylineOptions = new PolylineOptions();

            for(HashMap<String,String> point : paths)
            {
                double lat = Double.parseDouble(point.get("lat"));
                double lon = Double.parseDouble(point.get("lon"));

                points.add(new LatLng(lat,lon));
            }

            polylineOptions.addAll(points);
            polylineOptions.width(15);
            polylineOptions.color(Color.BLUE);
            polylineOptions.geodesic(true);
        }
        googleMap.addPolyline(polylineOptions);
    }
}