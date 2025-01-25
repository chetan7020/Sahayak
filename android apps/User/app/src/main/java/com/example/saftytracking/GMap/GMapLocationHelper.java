package com.example.saftytracking.GMap;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class GMapLocationHelper {

    private static final String TAG = "GMapLocationHelper";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private final FusedLocationProviderClient fusedLocationClient;
//    private final Context context;
    private LocationCallback locationCallback;
    private LocationResultListener locationResultListener;

    public GMapLocationHelper(Context context) {
//        this.context = context;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public void getLocation(@NonNull LocationResultListener listener) {
        this.locationResultListener = listener;

//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            Log.d(TAG, "Location permission not granted, requesting permission");
//            if (context instanceof AppCompatActivity) {
//                ActivityCompat.requestPermissions((AppCompatActivity) context,
//                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                        LOCATION_PERMISSION_REQUEST_CODE);
//            } else {
//                Toast.makeText(context, "Context is not an instance of AppCompatActivity", Toast.LENGTH_SHORT).show();
//            }
//            return;
//        }

        requestRealTimeLocation();
    }

    private void requestRealTimeLocation() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000) // Location update interval (ms)
                .setFastestInterval(500); // Fastest location update interval (ms)

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    double altitude = location.getAltitude();

                    Log.d(TAG, "Latitude: " + latitude + ", Longitude: " + longitude + ", Altitude: " + altitude);
                    if (locationResultListener != null) {
                        locationResultListener.onLocationResult(latitude, longitude, altitude);
                    }
                } else {
                    Log.e(TAG, "Location is null. Ensure GPS is enabled.");
//                    Toast.makeText(context, "Unable to fetch location. Please enable GPS.", Toast.LENGTH_SHORT).show();
                }
                stopLocationUpdates();
            }
        };

//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void stopLocationUpdates() {
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    public interface LocationResultListener {
        void onLocationResult(double latitude, double longitude, double altitude);
    }
}
