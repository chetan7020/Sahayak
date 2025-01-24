package com.example.admin.manage_users;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
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
import com.example.admin.auth.RegisterActivity;
import com.example.admin.databinding.ActivityAddUserBinding;
import com.example.admin.databinding.ActivityRegisterBinding;
import com.example.admin.models.AdminModel;
import com.example.admin.models.UserModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddUserActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private static final String TAG = "AddUserActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private UserModel userModel;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private ActivityAddUserBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userModel = new UserModel();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

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

                    userModel.setLat(latitude);
                    userModel.setLang(longitude);
                    userModel.setAltitude(altitude);

                    Log.d("TAG", "Updated Latitude: " + latitude + ", Longitude: " + longitude + ", Altitude: " + altitude);
//                    binding.etLocation.setText("Lat: " + latitude + "\nLng: " + longitude + "\nAlt: " + altitude + " meters");

                    stopLocationUpdates();
                } else {
                    Log.e("TAG", "Location is null. Ensure GPS is enabled.");
                    Toast.makeText(AddUserActivity.this, "Unable to fetch location. Please enable GPS.", Toast.LENGTH_SHORT).show();
                }
            }
        };

        checkPermission();

//        binding.btnGet.setOnClickListener(v -> {
//            checkPermission();
//        });

        binding.btnAddUser.setOnClickListener(v -> {
            if (validate()) {
                String name = binding.etName.getText().toString();
                String email = binding.etEmail.getText().toString();
                String phoneNumber = binding.etPhoneNumber.getText().toString();

                getLatLongFromAddress(binding.etAddress.getText().toString());

                userModel.setPhoneNumber(phoneNumber);

                int isVol;


                if (binding.etIsVolunteer.isActivated()) {
                    isVol = 1;
                } else {
                    isVol = 0;
                }


                String collectionName;

                if (isVol == 1) {
                    userModel.setVolunteer(true);
                    collectionName = "volunteers";
                } else {
                    userModel.setVolunteer(false);
                    collectionName = "users";
                }

                Toast.makeText(this, "" + collectionName, Toast.LENGTH_SHORT).show();

                String adminEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                userModel.setAdminEmail(adminEmail);

                userModel.setUserName(name);
                userModel.setUserEmail(email);

                firebaseFirestore
                        .collection("admins")
                        .document(adminEmail)
                        .collection(collectionName)
                        .document(email)
                        .set(userModel)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "User Added", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AddUserActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(this, "Error adding User: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
//                Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


//            }else{
//                Log.d("TAG", "onCreate: invalid data");
//            }
//        });

    }

    private boolean validate() {
        return true;
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

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Location permission not granted, requesting permission");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            Log.d(TAG, "Location permission already granted, requesting real-time location");
            requestRealTimeLocation();
        }

    }

    public void getLatLongFromAddress(String address) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            // Get a list of Address objects from the address string
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address location = addresses.get(0);
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                userModel.setLat(latitude);
                userModel.setLang(longitude);

//                Log.d("Geocoder", "Address: " + address + ", Latitude: " + latitude + ", Longitude: " + longitude);

                // Use latitude and longitude for further actions, e.g., navigation
            } else {
                Log.e("Geocoder", "No location found for the address: " + address);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Geocoder", "Failed to get latitude and longitude from address");
        }
    }

}