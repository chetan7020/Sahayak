package com.example.saftytracking.GMap;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.saftytracking.R;

public class GetLiveLocation extends AppCompatActivity {


    private final ActivityResultLauncher<String> backgroundLocationPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean isGranted) {
                    if (isGranted) {
                        // Permission granted, start the service
                        startLocationLoggingService();
                    } else {
                        // Permission denied, show a message or handle accordingly
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_live_location);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Request background location permission explicitly if the device is running Android 10 or higher
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Request background location permission using the launcher
                backgroundLocationPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            } else {
                // If already granted, start the service directly
                startLocationLoggingService();
            }
        }

        findViewById(R.id.send).setOnClickListener(view -> {
//            Log.d(TAG, "Button clicked to fetch real-time location");
            Toast.makeText( this, "Button clicked to fetch real-time location", Toast.LENGTH_SHORT).show();

            GMapLocationHelper locationHelper = new GMapLocationHelper(this);
            locationHelper.getLocation((latitude, longitude, altitude) -> {
//                Log.d(TAG, "Latitude: " + latitude + ", Longitude: " + longitude + ", Altitude: " + altitude);
                Toast.makeText(GetLiveLocation.this,
                        "Latitude: " + latitude + ", Longitude: " + longitude + ", Altitude: " + altitude,
                        Toast.LENGTH_LONG).show();
            });
        });
    }

    private void startLocationLoggingService() {
        Intent serviceIntent = new Intent(this, LocationService.class);
        startService(serviceIntent);
    }
}