package com.example.admin;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.admin.adapters.UserAdapter;
import com.example.admin.auth.LoginActivity;
import com.example.admin.background.ListenToUserFlagsService;
import com.example.admin.databinding.ActivityMainBinding;
import com.example.admin.manage_users.AddUserActivity;
import com.example.admin.manage_users.ManageUsersMainActivity;
import com.example.admin.models.AdminModel;
import com.example.admin.models.CornerModel;
import com.example.admin.models.FloorModel;
import com.example.admin.models.NotificationModel;
import com.example.admin.models.RestrictedModel;
import com.example.admin.models.UserModel;
import com.example.admin.org_setup.AddFloorActivity;
import com.example.admin.org_setup.OrgSetupMainActivity;
import com.example.admin.restricted_setup.AddRestrictedActivity;
import com.example.admin.restricted_setup.RestrictedSetupMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static AdminModel adminModel;

    private ActivityMainBinding binding;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private UserAdapter userAdapter;
    private List<UserModel> userList;

    private List<FloorModel> floorList;

    private static final int REQUEST_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        binding.rvUsersData.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(this, userList);
        binding.rvUsersData.setAdapter(userAdapter);

        floorList = new ArrayList<>();

        getFloorList();

        getData();

        fetchDataFromFirestore();

        setupClickListeners();

        addSnapShotListener();

    }

    private void getFloorList() {
        firebaseFirestore
                .collection("admins")
                .document(firebaseAuth.getCurrentUser().getEmail())
                .collection("floors")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots != null) {
                            floorList.clear(); // Clear any existing data
                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                FloorModel floorModel = document.toObject(FloorModel.class);
                                floorList.add(floorModel); // Add the FloorModel to the list
                            }

                        }
                    }
                });
    }


    private void fetchDataFromFirestore() {
        String currentUserEmail = firebaseAuth.getCurrentUser().getEmail();

        if (currentUserEmail != null) {
            firebaseFirestore
                    .collection("admins")
                    .document(currentUserEmail)
                    .collection("users") // Assuming users are stored under this sub-collection
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            userList.clear(); // Clear the list to avoid duplication

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UserModel userModel = document.toObject(UserModel.class);

                                if (!userModel.isVolunteer()) { // Filter out volunteers
                                    userList.add(userModel); // Add user directly, assuming duplicates are managed
                                }
                            }

                            // Notify the adapter about the change
                            userAdapter.notifyDataSetChanged();
                        } else {
                            Log.e("Firestore", "Error fetching data", task.getException());
                        }
                    });
        }

        // Set up the adapter
        userAdapter = new UserAdapter(this, userList);
        binding.rvUsersData.setAdapter(userAdapter);
    }

    private void getData() {

        firebaseFirestore
                .collection("admins")
                .document(firebaseAuth.getCurrentUser().getEmail())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        adminModel = documentSnapshot.toObject(AdminModel.class);
                        binding.tvHi.setText("Hi ," + adminModel.getAdminName());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void addSnapShotListener() {
        firebaseFirestore
                .collection("admins")
                .document(firebaseAuth.getCurrentUser().getEmail())
                .collection("users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("Firestore", "Listen failed.", e);
                            return;
                        }

                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            for (DocumentChange change : querySnapshot.getDocumentChanges()) {
                                DocumentSnapshot documentSnapshot = change.getDocument();
                                if (change.getType() == DocumentChange.Type.MODIFIED) {
                                    UserModel userModel = documentSnapshot.toObject(UserModel.class);
                                    if (userModel != null) {
                                        showEmergencyNotification(userModel);
                                    }
                                }
                            }

                        } else {
                            Log.d("Firestore", "No documents found!");
                        }
                    }
                });
    }

    private void showEmergencyNotification(UserModel userModel) {
//        fetchDataFromFirestore();
        String floor = getFloor(userModel);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
                Toast.makeText(this, "Out", Toast.LENGTH_SHORT).show();
                return; // Exit to wait for user response
            }
        }

        double destinationLatitude = userModel.getLat();
        double destinationLongitude = userModel.getLang();

        Uri gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=My+Location&destination="
                + destinationLatitude + "," + destinationLongitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

// PendingIntent for the map action
        PendingIntent mapPendingIntent = PendingIntent.getActivity(
                this,
                0,
                mapIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

// Notification with action button
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "EmergencyChannel")
                .setContentTitle("Emergency Alert")
                .setContentText("This is an emergency, please help " + userModel.getUserName() + " at the apptware first floor")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL)  // Make sound and vibrate
                .setSmallIcon(android.R.drawable.ic_dialog_alert)  // Default icon
                .setAutoCancel(true)  // Dismiss notification on click
                .addAction(android.R.drawable.ic_menu_directions, "Get Directions", mapPendingIntent);  // Add action button

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "EmergencyChannel",
                    "Emergency Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(1, builder.build());
    }

    private void setupClickListeners() {
        Log.d(TAG, "Setting up click listeners.");
        binding.orgSetup.setOnClickListener(v -> {
            Log.d(TAG, "Org Setup clicked.");
            startActivity(new Intent(this, AddFloorActivity.class));
        });

        binding.restrictedSetup.setOnClickListener(v -> {
            Log.d(TAG, "Restricted Setup clicked.");
            startActivity(new Intent(this, AddRestrictedActivity.class));
        });

        binding.userManagement.setOnClickListener(v -> {
            Log.d(TAG, "User Management clicked.");
            startActivity(new Intent(this, AddUserActivity.class));
        });

        binding.btnAddUser.setOnClickListener(v -> {
            Log.d(TAG, "User Management clicked.");
            startActivity(new Intent(this, AddUserActivity.class));
        });

        binding.heatMap.setOnClickListener(v -> {

            firebaseFirestore
                    .collection("admins")
                    .document(firebaseAuth.getCurrentUser().getEmail())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            AdminModel model = documentSnapshot.toObject(AdminModel.class);
                            model.setEmergencyFlag(1);

                            firebaseFirestore
                                    .collection("admins")
                                    .document(firebaseAuth.getCurrentUser().getEmail())
                                    .set(model)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(MainActivity.this, "Emergency Message Sent", Toast.LENGTH_SHORT).show();
                                            model.setEmergencyFlag(0);

                                            firebaseFirestore
                                                    .collection("admins")
                                                    .document(firebaseAuth.getCurrentUser().getEmail())
                                                    .set(model)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
//                                                            Toast.makeText(MainActivity.this, "Emergency Message Sent", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

//                            binding.tvHi.setText("Hi ," + adminModel.getAdminName());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        });

        binding.btnLogout.setOnClickListener(v -> {
            Log.d(TAG, "Logout button clicked.");
            firebaseAuth.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private String getFloor(UserModel userModel) {

//        getFloorList();
//        fetchDataFromFirestore();

        String floorName = "";

        for (FloorModel floorModel : floorList) {
            if (check(userModel, floorModel) == true) {
                floorName = floorModel.getFloorName();
//                break;
            }
        }

        return floorName;
    }

    private boolean check(UserModel userModel, FloorModel floorModel) {

        double minLat = Math.min(Math.min(floorModel.getCornerModel1().getLat(), floorModel.getCornerModel2().getLat()), Math.min(Math.min(floorModel.getCornerModel3().getLat(), floorModel.getCornerModel4().getLat()), Math.min(Math.min(floorModel.getCornerModel5().getLat(), floorModel.getCornerModel6().getLat()), Math.min(floorModel.getCornerModel7().getLat(), floorModel.getCornerModel8().getLat()))));
        double maxLat = Math.max(Math.max(floorModel.getCornerModel1().getLat(), floorModel.getCornerModel2().getLat()), Math.max(Math.max(floorModel.getCornerModel3().getLat(), floorModel.getCornerModel4().getLat()), Math.max(Math.max(floorModel.getCornerModel5().getLat(), floorModel.getCornerModel6().getLat()), Math.max(floorModel.getCornerModel7().getLat(), floorModel.getCornerModel8().getLat()))));

        double minLng = Math.min(Math.min(floorModel.getCornerModel1().getLang(), floorModel.getCornerModel2().getLang()), Math.min(Math.min(floorModel.getCornerModel3().getLang(), floorModel.getCornerModel4().getLang()), Math.min(Math.min(floorModel.getCornerModel5().getLang(), floorModel.getCornerModel6().getLang()), Math.min(floorModel.getCornerModel7().getLang(), floorModel.getCornerModel8().getLang()))));
        double maxLng = Math.max(Math.max(floorModel.getCornerModel1().getLang(), floorModel.getCornerModel2().getLang()), Math.max(Math.max(floorModel.getCornerModel3().getLang(), floorModel.getCornerModel4().getLang()), Math.max(Math.max(floorModel.getCornerModel5().getLang(), floorModel.getCornerModel6().getLang()), Math.max(floorModel.getCornerModel7().getLang(), floorModel.getCornerModel8().getLang()))));

        double minAltitude = Math.min(Math.min(floorModel.getCornerModel1().getAltitude(), floorModel.getCornerModel2().getAltitude()), Math.min(Math.min(floorModel.getCornerModel3().getAltitude(), floorModel.getCornerModel4().getAltitude()), Math.min(Math.min(floorModel.getCornerModel5().getAltitude(), floorModel.getCornerModel6().getAltitude()), Math.min(floorModel.getCornerModel7().getAltitude(), floorModel.getCornerModel8().getAltitude()))));
        double maxAltitude = Math.max(Math.max(floorModel.getCornerModel1().getAltitude(), floorModel.getCornerModel2().getAltitude()), Math.max(Math.max(floorModel.getCornerModel3().getAltitude(), floorModel.getCornerModel4().getAltitude()), Math.max(Math.max(floorModel.getCornerModel5().getAltitude(), floorModel.getCornerModel6().getAltitude()), Math.max(floorModel.getCornerModel7().getAltitude(), floorModel.getCornerModel8().getAltitude()))));

        Log.d(TAG, "check: " + userModel.getAltitude());
        Log.d(TAG, "check: " + minAltitude);
        Log.d(TAG, "check: " + maxAltitude);

        return (userModel.getAltitude() >= minAltitude && userModel.getAltitude() <= maxAltitude);
    }
}