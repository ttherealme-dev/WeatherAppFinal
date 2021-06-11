package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Address;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weatherapp.utils.NetworkUtils;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends AppCompatActivity implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, ActivityCompat.OnRequestPermissionsResultCallback, GoogleMap.OnMyLocationClickListener, GoogleMap.OnMapClickListener {
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int DEFAULT_ZOOM = 15;
    private Location lastKnownLocation;
    private static final String TAG = MapsActivity.class.getSimpleName();
    private boolean locationPermissionGranted;
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private boolean permissionDenied = false;
    private Marker mCurrLocationMarker;
    private GoogleApiClient mGoogleApiClient;
    GoogleMap map;
    Marker marker;
    double lat, lng;
    private EditText editTextInput;
    private TextView textViewShowInfo;
    private  final String URL = "https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=AIzaSyCrTiUmAPPUIn7N_8fBZtoN263uWRPKaGs";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setOnMyLocationClickListener(this);
        map.setOnMyLocationButtonClickListener(this);
        map.setOnMarkerClickListener(this);
        map.setOnMapClickListener(this);

        updateLocationUI();
        getDeviceLocation();
    }


    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        if(marker == null) {
            marker = map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(" "));
            mCurrLocationMarker.remove();
        }

        if(marker.getTitle().contains("Current Position")){
            marker.remove();
        }
        marker.setPosition(latLng);

        lat = latLng.latitude;
        lng = latLng.longitude;
        NetworkUtils.CityAsyncTask cst = new NetworkUtils.CityAsyncTask(this, lat, lng);
        cst.execute();

        String lo = null;
        try {
            lo = cst.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if(lo != null) {
            marker.setTitle(lo);
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        LatLng latLng = marker.getPosition();
        lat = latLng.latitude;
        lng = latLng.longitude;
        NetworkUtils.CityAsyncTask cst = new NetworkUtils.CityAsyncTask(this, lat, lng);
        cst.execute();

        String lo = null;
        try {
            lo = cst.get().toString();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if(lo != null) {
            marker.setTitle(lo);
        }
        if(lo != null) {
            marker.setTitle(lo);
        }
        return false;
    }

    @Override
    public boolean onMyLocationButtonClick() {
        getDeviceLocation();
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        getDeviceLocation();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            Toast.makeText(this, "Нет соответствующего разрешения", Toast.LENGTH_LONG).show();
            permissionDenied = false;
        }
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        /*
         Переопределиение обратного вызова метода onRequestPermissionsResult(), чтобы обработать результат запроса доступа
         */
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                                mCurrLocationMarker = map.addMarker(new MarkerOptions()
                                                .position(new LatLng(lastKnownLocation.getLatitude(),  lastKnownLocation.getLongitude()))
                                                .title("Current Location")
                                                .draggable(true)
                                );
                                                lat = lastKnownLocation.getLatitude();
                                                lng = lastKnownLocation.getLongitude();
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    public void onClick(View view) {
        Intent intent = new Intent(this, ActivityWeather.class);
        intent.putExtra("lat", lat);
        intent.putExtra("lng", lng);
        startActivity(intent);
    }

}




