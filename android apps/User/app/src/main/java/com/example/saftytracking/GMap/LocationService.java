package com.example.saftytracking.GMap;


import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.ServiceCompat;

import com.example.saftytracking.EmergencySos.AlertTO;
import com.example.saftytracking.Login.UserModel;
import com.example.saftytracking.R;
import com.example.saftytracking.notification.RestrictedModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

public class LocationService extends android.app.Service {

    private static final String CHANNEL_ID = "ForegroundServiceChannel";
    private static final String TAG = "MyForegroundService";
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    Vibrator vibrator;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    String userEmail;
    String adminEmail;
    private List<RestrictedModel> restrictedList = new ArrayList<>();


    @Override
    public void onCreate() {
        super.onCreate();

//        Intent intent = getApplicationContext().getIntent();
//        String userEmail = intent.getStringExtra("userEmail");
//        String adminEmail = intent.getStringExtra("adminEmail");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        createNotificationChannel();


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Setup location callback

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult != null && locationResult.getLastLocation() != null) {
                    double latitude = locationResult.getLastLocation().getLatitude();
                    double longitude = locationResult.getLastLocation().getLongitude();
                    double altitude = locationResult.getLastLocation().getAltitude();


                    restictedAreaNotification();
                    UserModel u = new UserModel();
                    u.setLat(latitude);
                    u.setLang(longitude);
                    u.setAltitude(altitude);

                    for (RestrictedModel r : restrictedList) {
                        boolean b = check(u, r);
                        if (b == true) {
                            sendNotificationWithVibration(getApplicationContext(), "Restricted Area", "Don't Enter in area");
//                            break;
                        }

                    }

//                    Toast.makeText(LocationService.this, "In servuice", Toast.LENGTH_SHORT).show();

                    if (userEmail != null && adminEmail != null) {
                        firebaseFirestore
                                .collection("admins")
                                .document(adminEmail)
                                .collection("users")
                                .document(userEmail)
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();

                                        if (document.exists()) {
                                            UserModel adminModel = document.toObject(UserModel.class);

                                            adminModel.setLat(latitude);
                                            adminModel.setLang(longitude);
                                            adminModel.setAltitude(altitude);


                                            firebaseFirestore
                                                    .collection("admins")
                                                    .document(adminEmail)
                                                    .collection("users")
                                                    .document(userEmail)
                                                    .set(adminModel);
                                        } else {
                                            Log.e("Firestore", "No such document");
                                        }
                                    } else {
                                        Log.e("Firestore", "Error getting document", task.getException());
                                    }
                                });

                    }

                    Log.d(TAG, "Location: Latitude: " + latitude + ", Longitude: " + longitude + ", Altitude: " + altitude);
                }
            }
        };

        startLocationUpdates();
    }


    public void restictedAreaNotification() {

        FirebaseFirestore fb = FirebaseFirestore.getInstance();
        fb.collection("admins")
                .document(adminEmail)
                .collection("restricted")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        restrictedList.clear();

                        // Iterate through the documents and map them to RestrictedModel
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            RestrictedModel model = document.toObject(RestrictedModel.class);
                            if (model != null) {
                                restrictedList.add(model); // Add the model to the list
                            }
                        }

                    }
                });


    }


    private boolean check(UserModel userModel, RestrictedModel floorModel) {

//        double minLat = Math.min(Math.min(floorModel.getCornerModel1().getLat(), floorModel.getCornerModel2().getLat()), Math.min(Math.min(floorModel.getCornerModel3().getLat(), floorModel.getCornerModel4().getLat()), Math.min(Math.min(floorModel.getCornerModel5().getLat(), floorModel.getCornerModel6().getLat()), Math.min(floorModel.getCornerModel7().getLat(), floorModel.getCornerModel8().getLat()))));
//        double maxLat = Math.max(Math.max(floorModel.getCornerModel1().getLat(), floorModel.getCornerModel2().getLat()), Math.max(Math.max(floorModel.getCornerModel3().getLat(), floorModel.getCornerModel4().getLat()), Math.max(Math.max(floorModel.getCornerModel5().getLat(), floorModel.getCornerModel6().getLat()), Math.max(floorModel.getCornerModel7().getLat(), floorModel.getCornerModel8().getLat()))));
//
//        double minLng = Math.min(Math.min(floorModel.getCornerModel1().getLang(), floorModel.getCornerModel2().getLang()), Math.min(Math.min(floorModel.getCornerModel3().getLang(), floorModel.getCornerModel4().getLang()), Math.min(Math.min(floorModel.getCornerModel5().getLang(), floorModel.getCornerModel6().getLang()), Math.min(floorModel.getCornerModel7().getLang(), floorModel.getCornerModel8().getLang()))));
//        double maxLng = Math.max(Math.max(floorModel.getCornerModel1().getLang(), floorModel.getCornerModel2().getLang()), Math.max(Math.max(floorModel.getCornerModel3().getLang(), floorModel.getCornerModel4().getLang()), Math.max(Math.max(floorModel.getCornerModel5().getLang(), floorModel.getCornerModel6().getLang()), Math.max(floorModel.getCornerModel7().getLang(), floorModel.getCornerModel8().getLang()))));

        double minAltitude = Math.min(Math.min(floorModel.getCornerModel1().getAltitude(), floorModel.getCornerModel2().getAltitude()), Math.min(Math.min(floorModel.getCornerModel3().getAltitude(), floorModel.getCornerModel4().getAltitude()), Math.min(Math.min(floorModel.getCornerModel5().getAltitude(), floorModel.getCornerModel6().getAltitude()), Math.min(floorModel.getCornerModel7().getAltitude(), floorModel.getCornerModel8().getAltitude()))));
        double maxAltitude = Math.max(Math.max(floorModel.getCornerModel1().getAltitude(), floorModel.getCornerModel2().getAltitude()), Math.max(Math.max(floorModel.getCornerModel3().getAltitude(), floorModel.getCornerModel4().getAltitude()), Math.max(Math.max(floorModel.getCornerModel5().getAltitude(), floorModel.getCornerModel6().getAltitude()), Math.max(floorModel.getCornerModel7().getAltitude(), floorModel.getCornerModel8().getAltitude()))));


        boolean b = (userModel.getAltitude() >= minAltitude && userModel.getAltitude() <= maxAltitude);

        if(b){
            Log.d("Location", "" + floorModel.getRestrictedName());
//            Log.d("Location", "" + minLat);
//            Log.d("Location", "" + maxLat);
//            Log.d("Location", "" + minLng);
//            Log.d("Location", "" + maxLng);
            Log.d("Location", "" + minAltitude);
            Log.d("Location", "" + maxAltitude);
        }


        return b;
    }


    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000) // Update interval (ms)
                .setFastestInterval(500); // Fastest interval (ms)

        // Check if permission is granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());
        } else {
            Log.e(TAG, "Permission not granted for location.");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        userEmail = intent.getStringExtra("userEmail");
        adminEmail = intent.getStringExtra("adminEmail");
        // Create a notification for the foreground service
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Location Logging Service")
                .setContentText("Logging your location in the background")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build();

        // Start the service as a foreground service
        startForeground(1, notification);

        return START_STICKY; // Keeps the service running
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop location updates when the service is destroyed
        fusedLocationClient.removeLocationUpdates(locationCallback);
        Log.d(TAG, "Service stopped.");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // Not used in this case
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }


    private void sendNotificationWithVibration(Context context, String sender, String messageBody) {
        // Create a notification manager
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a notification channel for Android 8.0 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "SMS Alerts",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for incoming SMS");
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }

        // Create an intent to open your app when the notification is clicked
        Intent openAppIntent = new Intent(context, AlertTO.class);
        openAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        Uri soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.alarm);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info) // Replace with your app's icon
                .setContentTitle("SMS from: " + sender)
                .setContentText(messageBody)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent); // Set the intent to open the app

        // Show the notification
        notificationManager.notify(1, builder.build());

        // Trigger long vibration
        triggerLongVibration(context);
        scheduleVibrationStop(1000);
    }

    // Trigger a long vibration
    private void triggerLongVibration(Context context) {
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(android.os.VibrationEffect.createWaveform(
                        new long[]{0, 1000, 800, 600, 400, 200, 100}, // Vibration pattern (off/on intervals)
                        0 // Repeat (-1 means no repeat)
                ));
            } else {
                vibrator.vibrate(new long[]{0, 1000, 500, 1500, 500}, 0); // Legacy support
            }
        }
    }

    private void scheduleVibrationStop(long delayMillis) {
        new Handler().postDelayed(() -> stopVibration(), delayMillis);
    }

    // Method to stop the vibration
    private void stopVibration() {
        if (vibrator != null) {
            vibrator.cancel(); // Stop the vibration
//            Toast.makeText(this, "Vibration stopped after 30 seconds", Toast.LENGTH_SHORT).show();
        }
    }


}
