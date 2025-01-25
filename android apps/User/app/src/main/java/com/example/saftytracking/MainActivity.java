package com.example.saftytracking;

//
//import android.content.Context;
//import android.hardware.Sensor;
//import android.hardware.SensorEvent;
//import android.hardware.SensorEventListener;
//import android.hardware.SensorManager;
//import android.os.Bundle;
//import android.widget.TextView;
//import androidx.appcompat.app.AppCompatActivity;
//
//public class MainActivity extends AppCompatActivity implements SensorEventListener {
//
//    private SensorManager sensorManager;
//    private Sensor accelerometer, gyroscope;
//
//    private TextView accelerometerTextView, gyroscopeTextView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        // Initialize TextViews to display sensor data
//        accelerometerTextView = findViewById(R.id.accelerometerTextView);
//        gyroscopeTextView = findViewById(R.id.gyroscopeTextView);
//
//        // Initialize SensorManager and Sensors
//        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//
//        if (sensorManager != null) {
//            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
//
//            // Register the listeners
//            if (accelerometer != null) {
//                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
//            }
//
//            if (gyroscope != null) {
//                sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
//            }
//        }
//    }
//
//    @Override
//    public void onSensorChanged(SensorEvent event) {
//        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//            // Update accelerometer data on the screen
//            String accelerometerValues = String.format(
//                    "Accelerometer\nX: %.2f\nY: %.2f\nZ: %.2f",
//                    event.values[0], event.values[1], event.values[2]
//            );
//            accelerometerTextView.setText(accelerometerValues);
//        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
//            // Update gyroscope data on the screen
//            String gyroscopeValues = String.format(
//                    "Gyroscope\nX: %.2f\nY: %.2f\nZ: %.2f",
//                    event.values[0], event.values[1], event.values[2]
//            );
//            gyroscopeTextView.setText(gyroscopeValues);
//        }
//    }
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//        // Not used in this example
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        // Unregister the listeners when the activity is destroyed
//        if (sensorManager != null) {
//            sensorManager.unregisterListener(this);
//        }
//    }
//}

//import android.content.Context;
//import android.hardware.Sensor;
//import android.hardware.SensorEvent;
//import android.hardware.SensorEventListener;
//import android.hardware.SensorManager;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//public class MainActivity extends AppCompatActivity implements SensorEventListener {
//
//    private SensorManager sensorManager;
//    private Sensor accelerometer, gyroscope;
//
//    private static final float THRESHOLD_ACCELERATION = 80.0f; // Change based on testing
//    private static final float THRESHOLD_ROTATION = 30.0f;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        // Initialize the sensor manager and sensors
//        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//
//        if (sensorManager != null) {
//            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
//
//            // Register the listeners
//            if (accelerometer != null) {
//                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
//            }
//
//            if (gyroscope != null) {
//                sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
//            }
//        }
//    }
//
//    @Override
//    public void onSensorChanged(SensorEvent event) {
//        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//            detectAccidentFromAccelerometer(event.values);
//        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
//            detectAccidentFromGyroscope(event.values);
//        }
//    }
//
//    private void detectAccidentFromAccelerometer(float[] values) {
//        float accelerationMagnitude = (float) Math.sqrt(
//                values[0] * values[0] +
//                        values[1] * values[1] +
//                        values[2] * values[2]
//        );
//
//        // Log the acceleration magnitude
//        Log.d("AccidentDetection", "Acceleration: " + accelerationMagnitude);
//
//        if (accelerationMagnitude > THRESHOLD_ACCELERATION) {
//            Log.d("AccidentDetection", "High acceleration detected! Potential accident.");
//
//            // Show a Toast message
//            runOnUiThread(() -> {
//                Toast.makeText(
//                        MainActivity.this,
//                        "High Acceleration Detected! Potential Accident!",
//                        Toast.LENGTH_SHORT
//                ).show();
//            });
//        }
//    }
//
//    private void detectAccidentFromGyroscope(float[] values) {
//        float rotationMagnitude = (float) Math.sqrt(
//                values[0] * values[0] +
//                        values[1] * values[1] +
//                        values[2] * values[2]
//        );
//
//        // Log the rotation magnitude
//        Log.d("AccidentDetection", "Rotation: " + rotationMagnitude);
//
//        if (rotationMagnitude > THRESHOLD_ROTATION) {
//            Log.d("AccidentDetection", "High rotation detected! Potential accident.");
//
//            // Show a Toast message
//            runOnUiThread(() -> {
//                Toast.makeText(
//                        MainActivity.this,
//                        "High Rotation Detected! Potential Accident!",
//                        Toast.LENGTH_SHORT
//                ).show();
//            });
//        }
//    }
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//        // Handle changes in sensor accuracy if needed
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        // Unregister listeners to save battery
//        if (sensorManager != null) {
//            sensorManager.unregisterListener(this);
//        }
//    }
//}


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start the foreground service
        findViewById(R.id.startServiceButton).setOnClickListener(view -> {
            Intent serviceIntent = new Intent(this, AccidentDetectionService.class);
            startService(serviceIntent);
        });

        // Stop the foreground service
        findViewById(R.id.stopServiceButton).setOnClickListener(view -> {
            Intent serviceIntent = new Intent(this, AccidentDetectionService.class);
            stopService(serviceIntent);
        });
    }
}


