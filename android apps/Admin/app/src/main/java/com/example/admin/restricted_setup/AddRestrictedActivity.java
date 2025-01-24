package com.example.admin.restricted_setup;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.admin.MainActivity;
import com.example.admin.auth.RegisterActivity;
import com.example.admin.databinding.ActivityAddFloorBinding;
import com.example.admin.databinding.ActivityAddRestrictedBinding;
import com.example.admin.models.CornerModel;
import com.example.admin.models.FloorModel;
import com.example.admin.models.RestrictedModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddRestrictedActivity extends AppCompatActivity {

    private static final String TAG = "AddFloorActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private EditText etData;
    private RestrictedModel floorModel;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private ActivityAddRestrictedBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddRestrictedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        floorModel = new RestrictedModel();

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Set up location callback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    double altitude = location.getAltitude();

                    if (etData.getId() == binding.et1.getId()) {
                        floorModel.getCornerModel1().setCornerId("1");
                        floorModel.getCornerModel1().setLat(latitude);
                        floorModel.getCornerModel1().setLang(longitude);
                        floorModel.getCornerModel1().setAltitude(altitude);

//                        Log.d(TAG, "onLocationResult: " + floorModel.get);
                    }
                    if (etData.getId() == binding.et2.getId()) {
                        floorModel.getCornerModel3().setCornerId("3");
                        floorModel.getCornerModel3().setLat(latitude);
                        floorModel.getCornerModel3().setLang(longitude);
                        floorModel.getCornerModel3().setAltitude(altitude);
                    }
                    if (etData.getId() == binding.et3.getId()) {
                        floorModel.getCornerModel5().setCornerId("5");
                        floorModel.getCornerModel5().setLat(latitude);
                        floorModel.getCornerModel5().setLang(longitude);
                        floorModel.getCornerModel5().setAltitude(altitude);
                    }
                    if (etData.getId() == binding.et4.getId()) {
                        floorModel.getCornerModel7().setCornerId("7");
                        floorModel.getCornerModel7().setLat(latitude);
                        floorModel.getCornerModel7().setLang(longitude);
                        floorModel.getCornerModel7().setAltitude(altitude);
                    }


                    Log.d(TAG, "Updated Latitude: " + latitude + ", Longitude: " + longitude + ", Altitude: " + altitude);
                    etData.setText("Lat: " + latitude + "\nLng: " + longitude + "\nAlt: " + altitude + " meters");

                    stopLocationUpdates();
                } else {
                    Log.e(TAG, "Location is null. Ensure GPS is enabled.");
                    Toast.makeText(AddRestrictedActivity.this, "Unable to fetch location. Please enable GPS.", Toast.LENGTH_SHORT).show();
                }
            }
        };


        binding.et1.setOnClickListener(v -> {
            etData = binding.et1;
            checkPermission();
        });

        binding.et2.setOnClickListener(v -> {
            etData = binding.et2;
            checkPermission();
        });

        binding.et3.setOnClickListener(v -> {
            etData = binding.et3;
            checkPermission();
        });

        binding.et4.setOnClickListener(v -> {
            etData = binding.et4;
            checkPermission();
        });

        binding.btnSubmit.setOnClickListener(v -> {
            if (validate()) {
                String floorName = binding.etRestrictedAreaName.getText().toString();
                double floorHeight = Double.parseDouble(binding.etRestrictedAreaHeight.getText().toString());

                floorModel.setRestrictedName(floorName);

                update(floorModel.getCornerModel2(), floorModel.getCornerModel1().getLat(), floorModel.getCornerModel1().getLang(), floorModel.getCornerModel1().getAltitude(), floorHeight, "2");
                update(floorModel.getCornerModel4(), floorModel.getCornerModel3().getLat(), floorModel.getCornerModel3().getLang(), floorModel.getCornerModel3().getAltitude(), floorHeight, "4");
                update(floorModel.getCornerModel6(), floorModel.getCornerModel5().getLat(), floorModel.getCornerModel5().getLang(), floorModel.getCornerModel5().getAltitude(), floorHeight, "6");
                update(floorModel.getCornerModel8(), floorModel.getCornerModel7().getLat(), floorModel.getCornerModel7().getLang(), floorModel.getCornerModel7().getAltitude(), floorHeight, "8");

                firebaseFirestore
                        .collection("admins")
                        .document(firebaseAuth.getCurrentUser().getEmail())
                        .collection("restricted")
                        .document(floorModel.getRestrictedId())
                        .set(floorModel)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Restricted Area Added", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(AddRestrictedActivity.this, RestrictedSetupMainActivity.class);
                            startActivity(intent);
                            finish();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(this, "Error Registering User: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });

                Log.d(TAG, floorModel.toString());
            } else {
                Log.d(TAG, "not validated");
            }
        });
    }

    private void update(CornerModel cornerModel, double lat, double lang, double alti, double floorHeight, String id) {
        cornerModel.setCornerId(id);
        cornerModel.setLat(lat);
        cornerModel.setLang(lang);
        cornerModel.setAltitude(alti + floorHeight);

        Log.d(TAG, "update: " + cornerModel);
    }

    private boolean validate() {
        return true;
    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Location permission not granted, requesting permission");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            Log.d(TAG, "Location permission already granted, requesting real-time location");
            requestRealTimeLocation();
        }

    }

    private void requestRealTimeLocation() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000) // Location update interval (ms)
                .setFastestInterval(500); // Fastest location update interval (ms)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());
        } else {
            Log.e(TAG, "Permission not granted for location");
        }
    }

    private void stopLocationUpdates() {
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Location permission granted by the user");
                requestRealTimeLocation();
            } else {
                Log.e(TAG, "Location permission denied by the user");
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
