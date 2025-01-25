package com.example.saftytracking.GMap;


import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.saftytracking.Login.UserModel;
import com.example.saftytracking.notification.OnDataReceivedListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import org.json.JSONObject;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class EmergencyAlertService extends android.app.Service {

    private static final String CHANNEL_ID = "ForegroundServiceChannel";
    private static final String TAG = "MyForegroundService";
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private SensorHelper sensorHelper;

    private Boolean isSendAlert = false;

    String userEmail;
    String adminEmail;

    @Override
    public void onCreate() {
        super.onCreate();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        createNotificationChannel();


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
         sensorHelper = new SensorHelper(this);

        // Setup location callback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult != null && locationResult.getLastLocation() != null) {
                    double latitude = locationResult.getLastLocation().getLatitude();
                    double longitude = locationResult.getLastLocation().getLongitude();
                    double altitude = locationResult.getLastLocation().getAltitude();

                    //call here from the update locationon


                    sensorHelper.startMonitoring();
                    float acceleration = sensorHelper.getAcceleration();
                    float rotation = sensorHelper.getRotation();
                    float magneticField = sensorHelper.getMagneticField();
                    float light = sensorHelper.getLight();
                    float proximity = sensorHelper.getProximity();


                    //call api and api give result correct then alert to the

//                    Toast.makeText(EmergencyAlertService.this, userEmail+adminEmail, Toast.LENGTH_SHORT).show();
                    getdata(acceleration, rotation, magneticField, light, proximity) ;



                    if(userEmail!=null && adminEmail!=null && isSendAlert){







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

//                                            adminModel.setLat(latitude);
//                                            adminModel.setLang(longitude);
//                                            adminModel.setAltitude(altitude);
                                            adminModel.setEmergencyFlag(1);

                                            isSendAlert=false;

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



                    Log.d(TAG, "Location: Latitude: " + latitude + ", Longitude: " + longitude + ", Altitude: " + altitude);
                    }
                    }
            }


        };

        startLocationUpdates();
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
        // Create a notification for the foreground service

        userEmail = intent.getStringExtra("userEmail");
        adminEmail = intent.getStringExtra("adminEmail");
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






    private void getdata(float acceleration, float rotation, float magneticField, float light, float proximity) {
        // Create and pass the listener
        new SendMessageTask(new OnDataReceivedListener() {
            @Override
            public void onDataReceived(String result) {
                // Handle the result here
                if (result != null) {
                    try {
                        JSONObject jsonResponse = new JSONObject(result);
                        boolean sosTriggered = jsonResponse.optBoolean("sos_triggered", false);
                        isSendAlert = sosTriggered;
                        // Use sosTriggered as needed (true or false)
                        Log.d(TAG, "SOS Triggered: " + sosTriggered);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // Use the result as needed
            }
        }).execute(String.format("Acceleration: %.2f, Rotation: %.2f, Magnetic Field: %.2f, Light: %.2f, Proximity: %.2f",
                acceleration, rotation, magneticField, light, proximity));
    }



    private class SendMessageTask extends AsyncTask<String, Void, String> {


        private OnDataReceivedListener listener;

        public SendMessageTask(OnDataReceivedListener listener) {
            this.listener = listener;
        }
        @Override
        protected String doInBackground(String... params) {
            String dataString = params[0];
//            Toast.makeText(EmergencyAlertService.this, "Something issue", Toast.LENGTH_SHORT).show();
            // Process the received string, for example, split it by commas
            String[] values = dataString.split(", ");

            // You can now extract the individual sensor values like:
            String acceleration = values[0].split(": ")[1];
            String rotation = values[1].split(": ")[1];
            String magneticField = values[2].split(": ")[1];
            String light = values[3].split(": ")[1];
            String proximity = values[4].split(": ")[1];
            try {
                URL url = new URL("https://sos-detection-api.onrender.com/predict");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // Creating the JSON body
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("acceleration", acceleration);
                jsonBody.put("rotation", rotation);
                jsonBody.put("magnetic_field", magneticField);
                jsonBody.put("light", light);

                // Sending request
                OutputStream os = connection.getOutputStream();
                byte[] input = jsonBody.toString().getBytes("utf-8");
                os.write(input, 0, input.length);

                // Get response
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                StringBuilder response = new StringBuilder();
                int read;
                while ((read = reader.read()) != -1) {
//                    Toast.makeText(EmergencyAlertService.this, "vasudev", Toast.LENGTH_SHORT).show();
                    response.append((char) read);
                }
                return response.toString();

            } catch (Exception e) {
//                Toast.makeText(EmergencyAlertService.this, "Raut", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                // Handle the response here (e.g., parse JSON)
                if (listener != null) {
                    listener.onDataReceived(result);
                }
            }
        }
    }





}
