package com.example.saftytracking;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

public class AccidentDetectionService extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer, gyroscope, magnetometer, lightSensor, proximitySensor;

    private TextView sensorDataText;
    private Button startButton, resetButton;

    // Threshold values for detecting accidents (can be adjusted)
    private static final float THRESHOLD_ACCELERATION = 15.0f;
    private static final float THRESHOLD_ROTATION = 3.0f;
    private static final float THRESHOLD_MAGNETIC_FIELD = 40.0f;
    private static final float THRESHOLD_LIGHT = 10.0f;
    private static final float THRESHOLD_PROXIMITY = 5.0f;

    private boolean isMonitoring = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_sensor);

        // Initialize UI elements
//        sensorDataText = findViewById(R.id.sensorDataText);
        startButton = findViewById(R.id.startButton);
        resetButton = findViewById(R.id.resetButton);

        // Set click listeners for buttons
        startButton.setOnClickListener(view -> startMonitoring());
        resetButton.setOnClickListener(view -> resetMonitoring());

        // Initialize SensorManager and sensors
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    private void startMonitoring() {
        if (!isMonitoring) {
            // Register the sensor listeners
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);

            isMonitoring = true;
            sensorDataText.setText("Monitoring Started. Please wait for sensor values...");
        }
    }

    private void resetMonitoring() {
        if (isMonitoring) {
            // Unregister the sensor listeners
            sensorManager.unregisterListener(this);

            isMonitoring = false;
            sensorDataText.setText("Monitoring Stopped. Click Start to begin.");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!isMonitoring) return;  // If not monitoring, do nothing

        float[] values = event.values;

        // Detect sensor value crosses threshold and show values on the screen
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            detectAccidentFromAccelerometer(values);
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            detectAccidentFromGyroscope(values);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            detectAccidentFromMagnetometer(values);
        } else if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            detectLightChange(values);
        } else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            detectProximity(values);
        }
    }

    private void detectAccidentFromAccelerometer(float[] values) {
        float accelerationMagnitude = (float) Math.sqrt(values[0] * values[0] + values[1] * values[1] + values[2] * values[2]);
        if (accelerationMagnitude > THRESHOLD_ACCELERATION) {
            // Show the sensor value on the TextView when the threshold is exceeded
            sensorDataText.setText("High Acceleration Detected: " + accelerationMagnitude + " m/s²");
        }
    }

    private void detectAccidentFromGyroscope(float[] values) {
        float rotationMagnitude = (float) Math.sqrt(values[0] * values[0] + values[1] * values[1] + values[2] * values[2]);
        if (rotationMagnitude > THRESHOLD_ROTATION) {
            // Show the sensor value on the TextView when the threshold is exceeded
            sensorDataText.setText("High Rotation Detected: " + rotationMagnitude + " rad/s");
        }
    }

    private void detectAccidentFromMagnetometer(float[] values) {
        float magneticFieldStrength = (float) Math.sqrt(values[0] * values[0] + values[1] * values[1] + values[2] * values[2]);
        if (magneticFieldStrength > THRESHOLD_MAGNETIC_FIELD) {
            // Show the sensor value on the TextView when the threshold is exceeded
            sensorDataText.setText("High Magnetic Field Detected: " + magneticFieldStrength + " uT");
        }
    }

    private void detectLightChange(float[] values) {
        float lightIntensity = values[0];
        if (lightIntensity < THRESHOLD_LIGHT) {
            // Show the sensor value on the TextView when the threshold is exceeded
            sensorDataText.setText("Low Light Detected: " + lightIntensity + " lux");
        }
    }

    private void detectProximity(float[] values) {
        float proximityDistance = values[0];
        if (proximityDistance < THRESHOLD_PROXIMITY) {
            // Show the sensor value on the TextView when the threshold is exceeded
            sensorDataText.setText("Proximity Detected: " + proximityDistance + " cm");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // You can handle sensor accuracy changes if necessary
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister listeners when the activity is destroyed
        if (isMonitoring) {
            sensorManager.unregisterListener(this);
        }
    }
}
