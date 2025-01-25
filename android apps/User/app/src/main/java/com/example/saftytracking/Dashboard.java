package com.example.saftytracking;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.saftytracking.EmergencySos.SmsReceiver;
import com.example.saftytracking.GMap.EmergencyAlertService;
import com.example.saftytracking.GMap.GMapLocationHelper;
import com.example.saftytracking.GMap.GetLiveLocation;
import com.example.saftytracking.GMap.LocationService;
import com.example.saftytracking.Login.AdminModel;
import com.example.saftytracking.Login.UserModel;
import com.example.saftytracking.notification.FloorModel;
import com.example.saftytracking.notification.Notifications;
import com.example.saftytracking.notification.RestrictedModel;
import com.example.saftytracking.notification.SignLanguage;
import com.google.android.gms.common.api.Response;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.os.Bundle;
//import androidx.appcompat.app.AppCompatActivity;
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Dashboard extends AppCompatActivity {


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


    private final ActivityResultLauncher<String> bckgroundEmergencyTracker = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean isGranted) {
                    if (isGranted) {
                        // Permiss     ion granted, start the service
                        startEmargencyAlertService();
                    } else {
                        // Permission denied, show a message or handle accordingly
                    }
                }
            });


    private final ActivityResultLauncher<String> smsService = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean isGranted) {
                    if (isGranted) {
                        // Permiss     ion granted, start the service
                        startsmsServices();
                    } else {
                        // Permission denied, show a message or handle accordingly
                    }
                }
            });

//    private void startSmsService() {
//    }

    private UserModel userModel;
    Double latc;
    Double langc;
    private List<RestrictedModel> restrictedList = new ArrayList<>();



    private String emergencyNumber = "7387579912";
    private long lastPressTime = 0;
    private long pressInterval = 5000;
    private boolean volumeUpPressed = false;
    private boolean volumeDownPressed = false;
    private long volumeUpPressStartTime = 0;
    private long volumeDownPressStartTime = 0;
    private long longPressThreshold = 3;
    private FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        String emails = getIntent().getStringExtra("email");

//        FloatingActionButton fl = findViewById(R.id.sosButton);
        RelativeLayout rl = findViewById(R.id.rel);
        rl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                stopVibration();
                return true;
            }
        });
//        makePhoneCall(emergencyNumber);

//        userModel = storeDataInSharedPrefrence("rushi123@gmail.com");
        userModel = storeDataInSharedPrefrence(emails);
//        makePhoneCall("7387579912");

        ImageView notification = findViewById(R.id.notification_icon);
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, Notifications.class);
                startActivity(intent);
            }
        });

        LinearLayout signl = findViewById(R.id.signLanguage);
        signl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, SignLanguage.class);
                startActivity(intent);
            }
        });

        Intent intent = new Intent(Dashboard.this, SignLanguage.class);
//        startActivity(intent);

        startsmsservice();


    }


    private void sendSosMessage() {
        if (emergencyNumber != null) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("7823859849", null, "I am in need please help me..", null, null);
            Toast.makeText(this, "SOS Message Sent", Toast.LENGTH_SHORT).show();


            firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseFirestore
                    .collection("admins")
                    .document(userModel.getAdminEmail())
                    .collection("users")
                    .document(userModel.getUserEmail())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                UserModel adminModel = document.toObject(UserModel.class);

//                                            adminModel.setLat(latitude);
//                                            adminModel.setLang(longitude);
//                                            adminModel.setAltitude(altitude);
//                                Toast.makeText(this, userModel.getUserEmail() + userModel.getAdminEmail() + adminModel.getUserEmail() + adminModel.getAdminEmail(), Toast.LENGTH_SHORT).show();

                                sendNotificationAlert(userModel);
                                adminModel.setEmergencyFlag(1);

                                firebaseFirestore
                                        .collection("admins")
                                        .document(userModel.getAdminEmail())
                                        .collection("users")
                                        .document(userModel.getUserEmail())
                                        .set(adminModel);
                            } else {
                                Log.e("Firestore", "No such document");
                            }
                        } else {
                            Log.e("Firestore", "Error getting document", task.getException());
                        }
                    });


        }
    }

    // Overriding the onKeyDown method to detect hardware key presses (Volume buttons)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        long currentTime = SystemClock.elapsedRealtime();

        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            volumeUpPressed = true;
            volumeUpPressStartTime = currentTime;  // Record press start time
        }

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            volumeDownPressed = true;
            volumeDownPressStartTime = currentTime;  // Record press start time
        }

        return super.onKeyDown(keyCode, event);
    }

    // Overriding onKeyUp to reset the flags when keys are released
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        long currentTime = SystemClock.elapsedRealtime();

        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            volumeUpPressed = false;
            // Check if the button was held down for 5 seconds
            if (currentTime - volumeUpPressStartTime >= longPressThreshold && volumeDownPressed) {
                sendSosMessage();  // Send SOS message if volume up and down pressed together for long enough
            }
        }

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            volumeDownPressed = false;
            // Check if the button was held down for 5 seconds
            if (currentTime - volumeDownPressStartTime >= longPressThreshold && volumeUpPressed) {
                sendSosMessage();  // Send SOS message if volume up and down pressed together for long enough
            }
        }

        return super.onKeyUp(keyCode, event);
    }


    //--------------------------------------------------------------------------------------------------------
    //Emrgency alert by sensor
    // Update the value in user flag emergency  = 1
    // all users and valunter can come notification






    private void startsmsservice() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Request background location permission explicitly if the device is running Android 10 or higher
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Request background location permission using the launcher
                bckgroundEmergencyTracker.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            } else {
                // If already granted, start the service directly
                startsmsServices();
            }
        }


    }

    private void startsmsServices() {
//        Toast.makeText(this, "Recive", Toast.LENGTH_SHORT).show();
        Intent serviceIntent = new Intent(this, SmsReceiver.class);
        serviceIntent.putExtra("userEmail", userModel.getUserEmail());
        serviceIntent.putExtra("adminEmail", userModel.getAdminEmail());
        startService(serviceIntent);
    }





    private void updateEmargency() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Request background location permission explicitly if the device is running Android 10 or higher
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Request background location permission using the launcher
                bckgroundEmergencyTracker.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            } else {
                // If already granted, start the service directly
                startEmargencyAlertService();
            }
        }


    }

    private void startEmargencyAlertService() {
        Intent serviceIntent = new Intent(this, EmergencyAlertService.class);
        serviceIntent.putExtra("userEmail", userModel.getUserEmail());
        serviceIntent.putExtra("adminEmail", userModel.getAdminEmail());
        startService(serviceIntent);
    }


    private void sendNotificationAlert(UserModel userModel) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersCollectionRef = db.collection("admins")
                .document(userModel.getAdminEmail())  // Navigate to the specific admin document
                .collection("users");  // Reference the users sub-collection

        GMapLocationHelper locationHelper = new GMapLocationHelper(this);
        locationHelper.getLocation((latitude, longitude, altitude) -> {
            latc = latitude;
            langc = latitude;

        });

        usersCollectionRef.addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                Log.e("Firestore", "Listen failed.", e);
                return;
            }

            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                for (DocumentChange change : querySnapshot.getDocumentChanges()) {
                    DocumentSnapshot documentSnapshot = change.getDocument();

                    if (change.getType() == DocumentChange.Type.MODIFIED) {
                        // Get the value of 'emergencyFlag'
                        UserModel n = documentSnapshot.toObject(UserModel.class);
                        Double callFlag = documentSnapshot.getDouble("emergencyFlag");
                        String email = documentSnapshot.getString("userEmail");
                        Double lat = documentSnapshot.getDouble("lat");
                        Double lang = documentSnapshot.getDouble("lang");

                        if (callFlag != null && callFlag == 1.0) {
                            // Call the method to show notification

                            if (latc != null && langc != null && lat != null && lang != null) {
                                double earthRadius = 6371; // Earth's radius in km

                                // Convert degrees to radians
                                double dLat = Math.toRadians(lat - latc);
                                double dLon = Math.toRadians(lang - langc);

                                double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                                        Math.cos(Math.toRadians(latc)) * Math.cos(Math.toRadians(lat)) *
                                                Math.sin(dLon / 2) * Math.sin(dLon / 2);

                                double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                                double distance = earthRadius * c; // Distance in km
//                                Toast.makeText(this, distance+"", Toast.LENGTH_SHORT).show();


//                                Toast.makeText(this, n.getUserName()+userModel.getUserName(), Toast.LENGTH_SHORT).show();


                                if(!userModel.getUserEmail().equals(email)){
                                    showEmergencyNotification(n);

                                }

                            }


                            if (userModel.getUserEmail().equals(email)) {
                                documentSnapshot.getReference().update("emergencyFlag", 0)
                                        .addOnSuccessListener(aVoid -> Log.d("Firestore", "callFlag updated to 0"))
                                        .addOnFailureListener(e1 -> Log.e("Firestore", "Error updating callFlag", e1));
                            }

                        }
                    }
                }
            } else {
                Log.d("Firestore", "No documents or empty collection");
            }
        });


    }

    private void showEmergencyNotification(UserModel userModel) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
                return; // Exit to wait for user response
            }
        }
//        triggerLongVibration(Dashboard.this);
        Uri soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.alarm);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "EmergencyChannel")
                .setSmallIcon(R.drawable.notification) // Replace with your drawable icon

                .setContentTitle("\uD83C\uDD98Emergency Alert: Help Needed!\uD83C\uDD98")
                .setContentText(userModel.getUserName() + " need help and requires immediate assistance.\n"
                        + "Mobile: " + userModel.getPhoneNumber() + " | "
                        + "Address: " + getAddress(userModel.getLat(), userModel.getLang()))
                .setStyle(new NotificationCompat.BigTextStyle()  // Use BigTextStyle for expandable notifications
                        .bigText(userModel.getUserName() + " need help and requires immediate assistance.\n"
                                + "Mobile: " + userModel.getPhoneNumber() + " | "
                                + "Address: " + getAddress(userModel.getLat(), userModel.getLang())))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSound(soundUri, AudioManager.STREAM_MUSIC)// Make sound and vibrate
                .setSmallIcon(android.R.drawable.ic_dialog_alert);


        // Default icon
//                .setContentIntent(pendingIntent)  // Add action on clicking the notification

        // Get NotificationManager to issue the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Check if the device is running Android O or above, and create notification channel if required
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "EmergencyChannel1",
                    "Emergency Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        // Issue the notification
        notificationManager.notify(1, builder.build());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
//                showEmergencyNotification();
            } else {
                // Permission denied
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    String getAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return address.getAddressLine(0); // Full address as a single string
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Unable to fetch address.";
    }


    //--------------------------------------------------------------------------------------------------------


    //--------------------------------------------------------------------------------------------------------
    //update live location to the firebase every time

    private void updateLiveLocation(UserModel userModel) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Request background location permission explicitly if the device is running Android 10 or higher
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Request background location permission using the launcher
                backgroundLocationPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            } else {
                // If already granted, start the service directly
                startLocationLoggingService();
            }
        }
    }

    private void startLocationLoggingService() {

//        Toast.makeText(this, "start service", Toast.LENGTH_SHORT).show();
        Intent serviceIntent = new Intent(this, LocationService.class);
        serviceIntent.putExtra("userEmail", userModel.getUserEmail());
        serviceIntent.putExtra("adminEmail", userModel.getAdminEmail());
        startService(serviceIntent);
    }


    //----------------------------------------------------------------------------------------------------------


    //---------------------------------------------------------------------------------------------------------
    //Auto call back to manager

    private void listenToAutoCallField(UserModel userModel) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("admins")
                .document(userModel.getAdminEmail())  // Get the specific admin document
                .collection("users")  // Navigate to the users sub-collection
                .document(userModel.getUserEmail());  // Get the specific user document

        // Listen to real-time changes of the document
        docRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.e("Firestore", "Listen failed.", e);
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                // Get the value of the 'callFlag'
                Double callFlag = documentSnapshot.getDouble("callFlag");

                // Toast for debugging
//                Toast.makeText(this, "Current callFlag value: " + callFlag, Toast.LENGTH_SHORT).show();

                // Check if the callFlag is 1 and perform action, else do nothing
                if (callFlag != null && callFlag == 1.0) {
                    // Retrieve admin document details
                    db.collection("admins")
                            .document(userModel.getAdminEmail()) // Get the document for the specific admin
                            .get()
                            .addOnSuccessListener(adminSnapshot -> {

                                if (adminSnapshot.exists()) {
                                    // Map the admin document to AdminModel

                                    AdminModel adminModel = adminSnapshot.toObject(AdminModel.class);

//                                    Toast.makeText(this, adminModel.getPhoneNumber(), Toast.LENGTH_SHORT).show();

                                    // Call the method to make the phone call
                                    if (adminModel != null && adminModel.getPhoneNumber() != null) {
                                        makePhoneCall(adminModel.getPhoneNumber()); // Replace with desired phone number
                                    }
                                }
                            })
                            .addOnFailureListener(e1 -> {
                                Log.e("Firestore", "Error getting admin document", e1);
                            });

                    // Update the callFlag field to 0 after making the call
                    documentSnapshot.getReference().update("callFlag", 0)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("Firestore", "callFlag updated to 0");
                            })
                            .addOnFailureListener(e1 -> {
                                Log.e("Firestore", "Error updating callFlag", e1);
                            });
                }
            } else {
                Log.d("Firestore", "Document does not exist or empty");
            }
        });

    }


    private void makePhoneCall(String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
//        try {
        // Start the call
        startActivity(callIntent);
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.setSpeakerphoneOn(true);
//        setAudioRoute(ROUTE_SPEAKER);

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String phoneNumber) {
                if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setMode(AudioManager.MODE_IN_CALL);
                    audioManager.setSpeakerphoneOn(true);
                }
                super.onCallStateChanged(state, phoneNumber);
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);


    }

    //-----------------------------------------------------------------------------------------------------------------


    //first time fetch data from firebase we pass simply email based on this fetch and store in sharedprefrencces
    UserModel storeDataInSharedPrefrence(String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("admins")
                .get()
                .addOnSuccessListener(adminSnapshot -> {

                    for (DocumentSnapshot adminDoc : adminSnapshot) {
                        String adminEmail = adminDoc.getId();  // Get admin email
//                        Toast.makeText(this, ""+email+adminEmail, Toast.LENGTH_SHORT).show();

                        db.collection("admins")
                                .document(adminEmail)
                                .collection("users")
                                .whereEqualTo("userEmail", email)  // Match user email
                                .get()
                                .addOnSuccessListener(userSnapshot -> {

                                    for (DocumentSnapshot userDoc : userSnapshot) {
//                                        Toast.makeText(this, email, Toast.LENGTH_SHORT).show();

                                        userModel = userDoc.toObject(UserModel.class);
                                        listenToAutoCallField(userModel);
                                        sendNotificationAlert(userModel);
                                        listenToAdminFieldChange();

                                        updateLiveLocation(userModel);
                                        updateEmargency();

                                    }
                                })
                                .addOnFailureListener(e -> {
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure to fetch admin data
                });


        return new UserModel();

    }


    //------------------------------------------------------------------------------
    //sign language detection


    private void listenToAdminFieldChange() {
        // Reference to the specific document
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference adminEmailDoc = firestore.collection("admins").document(userModel.getAdminEmail());

        // Add a snapshot listener
        ListenerRegistration listenerRegistration = adminEmailDoc.addSnapshotListener((DocumentSnapshot snapshot, FirebaseFirestoreException e) -> {
            if (e != null) {
                // Handle the error
                Log.e("FirestoreListener", "Error listening for updates", e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                // Get the value of "someField"
                Long someFieldValue = snapshot.getLong("emergencyFlag"); // Ensure the field is of type Long

                // Check if the value is 1
                if (someFieldValue != null && someFieldValue == 1) {
                    // Perform the desired action
//                    showNotification(Dashboard.this,"Vasudev","Raut");
//                    showNotification();
                    showManagerMessage();

                }
            } else {
                Log.d("FirestoreListener", "Document does not exist");
            }
        });
    }


    private Vibrator vibrator;
    private boolean isVibrating = false;

    private void showManagerMessage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
                return; // Exit to wait for user response
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "EmergencyChannel")
                .setSmallIcon(R.drawable.notification) // Replace with your drawable icon
                .setContentTitle("\uD83C\uDD98Emergency Alert!\uD83C\uDD98 ")
                .setContentText("Message from Manager\n"
                        + "Mobile: " + userModel.getPhoneNumber() + " | "
                        + "Address: " + getAddress(userModel.getLat(), userModel.getLang()))
                .setStyle(new NotificationCompat.BigTextStyle()  // Use BigTextStyle for expandable notifications
                        .bigText("Message from Manager\n"
                                + "Mobile: " + userModel.getPhoneNumber() + " | "
                                + "Address: " + getAddress(userModel.getLat(), userModel.getLang())))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL)  // Make sound and vibrate
                .setSmallIcon(android.R.drawable.ic_dialog_alert);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Trigger the vibration
        triggerLongVibration();

        // Check if the device is running Android O or above, and create notification channel if required
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "EmergencyChannel",
                    "Emergency Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        // Issue the notification
        notificationManager.notify(1, builder.build());
    }

    private void triggerLongVibration() {
        if (vibrator == null) {
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        }

        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!isVibrating) {
                    vibrator.vibrate(VibrationEffect.createWaveform(
                            new long[]{0, 100, 200, 300, 400, 500, 600}, // Vibration pattern (off/on intervals)
                            0 // Repeat (-1 means no repeat)
                    ));
                    isVibrating = true;
                }
            } else {
                if (!isVibrating) {
                    vibrator.vibrate(new long[]{0, 100, 200, 300, 400}, 0); // Legacy support
                    isVibrating = true;
                }
            }
        }
    }

    // Call this function when screen off is detected to stop the vibration
    private void stopVibration() {
        if (vibrator != null && isVibrating) {
            vibrator.cancel(); // Stop the vibration
            isVibrating = false;
        }
    }



















}