package com.example.go4lunch.Repository;


import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.Utils.PlaceService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class RetrofitRepository {
    private final PlaceService placeService;
    private static final float OFFICE_LONGITUDE = 2.3467211059168793f;
    private static final float OFFICE_LATITUDE = 48.86501071160738f;
    private static final int LOCATION_REQUEST_PROVIDER_IN_MS = 60000;
    private static final float SMALLEST_DISPLACEMENT_THRESHOLD_METER = 50;
    private static final String OFFICE = "Office";

    private FusedLocationProviderClient fusedLocationProviderClient;
    public final MutableLiveData<Location> locationMutableLiveData = new MutableLiveData<>();

    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    public RetrofitRepository(PlaceService placeService) {
        this.placeService = placeService;
    }


    public int getRadius() {
        return 500;
    }

    public LiveData<Location> getCurrentLocation() {
        return locationMutableLiveData;
    }


    public Location getOfficeLocation() {
        Location officeLocation = new Location(OFFICE);
        officeLocation.setLongitude(OFFICE_LONGITUDE);
        officeLocation.setLatitude(OFFICE_LATITUDE);
        return officeLocation;
    }

    @SuppressLint("MissingPermission")
    public void startLocationRequest(Context context, Activity activity) {

        instantiateFusedLocationProviderClient(context);

        getLastKnownLocation(activity);

        setupLocationRequest();

        createLocationCallback();

        startLocationUpdates();
    }

    public void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void instantiateFusedLocationProviderClient(Context context) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
    }

    @SuppressLint("MissingPermission")
    private void getLastKnownLocation(Activity activity) {
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(activity, location -> {
                    if (location != null) {
                        Log.i(TAG, "onSuccess: we got the last location", null);
                    }
                });
    }

    private void setupLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(LOCATION_REQUEST_PROVIDER_IN_MS);
        locationRequest.setSmallestDisplacement(SMALLEST_DISPLACEMENT_THRESHOLD_METER);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                if (locationResult == null) {
                    return;
                }

                for (Location location : locationResult.getLocations()) {
                    locationMutableLiveData.setValue(location);
                }
            }
        };
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }


}
