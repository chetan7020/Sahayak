package com.example.admin.auth;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.admin.MainActivity;
import com.example.admin.R;
import com.example.admin.databinding.ActivityAddFloorBinding;
import com.example.admin.databinding.ActivityRegisterBinding;
import com.example.admin.models.AdminModel;
import com.example.admin.org_setup.AddFloorActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    private static final String TAG = "RegisterActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private AdminModel adminModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adminModel = new AdminModel();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

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

                    adminModel.setLat(latitude);
                    adminModel.setLang(longitude);
                    adminModel.setAltitude(altitude);

                    Log.d("TAG", "Updated Latitude: " + latitude + ", Longitude: " + longitude + ", Altitude: " + altitude);
                    binding.etLocation.setText("Lat: " + latitude + "\nLng: " + longitude + "\nAlt: " + altitude + " meters");

                    stopLocationUpdates();
                } else {
                    Log.e("TAG", "Location is null. Ensure GPS is enabled.");
                    Toast.makeText(RegisterActivity.this, "Unable to fetch location. Please enable GPS.", Toast.LENGTH_SHORT).show();
                }
            }
        };

        binding.tvLogin.setOnClickListener(v->{
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        binding.btnGet.setOnClickListener(v->{
            checkPermission();
        });

        binding.btnSignUp.setOnClickListener(v -> {
            if(validate()){
                String name = binding.etName.getText().toString();
                String email = binding.etEmail.getText().toString();
                String password = binding.etPassword.getText().toString();
                String phoneNumber = binding.etPhoneNumber.getText().toString();

                adminModel.setAdminName(name);
                adminModel.setAdminEmail(email);
                adminModel.setPhoneNumber(phoneNumber);


                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        firebaseFirestore
                                .collection("admins")
                                .document(email)
                                .set(adminModel)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Admin Registered Successfully", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error Registering User: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }else{
                Log.d("TAG", "onCreate: invalid data");
            }
        });

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

    private void checkPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Location permission not granted, requesting permission");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            Log.d(TAG, "Location permission already granted, requesting real-time location");
            requestRealTimeLocation();
        }

    }


    private boolean validate() {
        return true;
    }
}