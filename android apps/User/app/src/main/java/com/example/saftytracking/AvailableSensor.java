package com.example.saftytracking;


//import android.hardware.Sensor;
//import android.hardware.SensorManager;
//import android.os.Bundle;
//import android.widget.TextView;
//import androidx.appcompat.app.AppCompatActivity;
//import java.util.List;
//import android.hardware.Sensor;
//import android.hardware.SensorEvent;
//import android.hardware.SensorEventListener;
//import android.hardware.SensorManager;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//import androidx.appcompat.app.AppCompatActivity;
//public class AvailableSensor extends AppCompatActivity implements SensorEventListener {
//
//    private SensorManager sensorManager;
//    private Sensor accelerometer, gyroscope, magnetometer, lightSensor, proximitySensor;
//
//    private TextView accelerationText, rotationText, magneticFieldText, lightText, proximityText;
//    private Button startButton, resetButton;
//
//    // Threshold values for detecting accidents (can be adjusted)
//    private static final float THRESHOLD_ACCELERATION = 15.0f;
//    private static final float THRESHOLD_ROTATION = 3.0f;
//    private static final float THRESHOLD_MAGNETIC_FIELD = 40.0f;
//    private static final float THRESHOLD_LIGHT = 10.0f;
//    private static final float THRESHOLD_PROXIMITY = 5.0f;
//
//    private boolean isMonitoring = false;
//
//    // Track max values for each sensor
//    private float maxAcceleration = 0;
//    private float maxRotation = 0;
//    private float maxMagneticField = 0;
//    private float maxLight = 0;
//    private double maxProximity = Double.MIN_VALUE;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_available_sensor);
//
//        // Initialize UI elements
//        accelerationText = findViewById(R.id.accelerationText);
//        rotationText = findViewById(R.id.rotationText);
//        magneticFieldText = findViewById(R.id.magneticFieldText);
//        lightText = findViewById(R.id.lightText);
//        proximityText = findViewById(R.id.proximityText);
//
//        startButton = findViewById(R.id.startButton);
//        resetButton = findViewById(R.id.resetButton);
//
//        // Set click listeners for buttons
//        startButton.setOnClickListener(view -> startMonitoring());
//        resetButton.setOnClickListener(view -> resetMonitoring());
//
//        // Initialize SensorManager and sensors
//        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
//        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
//        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
//        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
//    }
//
//    private void startMonitoring() {
//        if (!isMonitoring) {
//            // Register the sensor listeners
//            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
//            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
//            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
//            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
//            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
//
//            isMonitoring = true;
//        }
//    }
//
//    private void resetMonitoring() {
//        if (isMonitoring) {
//            // Unregister the sensor listeners
//            sensorManager.unregisterListener(this);
//
//            // Reset max values and update the TextViews to 0
//            maxAcceleration = 0;
//            maxRotation = 0;
//            maxMagneticField = 0;
//            maxLight = 0;
//            maxProximity = 0;
//
//            accelerationText.setText("Acceleration: 0");
//            rotationText.setText("Rotation: 0");
//            magneticFieldText.setText("Magnetic Field: 0");
//            lightText.setText("Light: 0");
//            proximityText.setText("Proximity: 0");
//
//            isMonitoring = false;
//        }
//    }
//
//    @Override
//    public void onSensorChanged(SensorEvent event) {
//        if (!isMonitoring) return;  // If not monitoring, do nothing
//
//        float[] values = event.values;
//
//        // Detect sensor value crosses threshold and show max value on the screen
//        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//            detectMaxAcceleration(values);
//        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
//            detectMaxRotation(values);
//        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
//            detectMaxMagneticField(values);
//        } else if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
//            detectMaxLight(values);
//        } else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
//            detectMaxProximity(values);
//        }
//    }
//
//    private void detectMaxAcceleration(float[] values) {
//        float accelerationMagnitude = (float) Math.sqrt(values[0] * values[0] + values[1] * values[1] + values[2] * values[2]);
//        if (accelerationMagnitude > maxAcceleration) {
//            maxAcceleration = accelerationMagnitude;
//            accelerationText.setText("Acceleration: " + maxAcceleration + " m/s²");
//        }
//    }
//
//    private void detectMaxRotation(float[] values) {
//        float rotationMagnitude = (float) Math.sqrt(values[0] * values[0] + values[1] * values[1] + values[2] * values[2]);
//        if (rotationMagnitude > maxRotation) {
//            maxRotation = rotationMagnitude;
//            rotationText.setText("Rotation: " + maxRotation + " rad/s");
//        }
//    }
//
//    private void detectMaxMagneticField(float[] values) {
//        float magneticFieldStrength = (float) Math.sqrt(values[0] * values[0] + values[1] * values[1] + values[2] * values[2]);
//        if (magneticFieldStrength > maxMagneticField) {
//            maxMagneticField = magneticFieldStrength;
//            magneticFieldText.setText("Magnetic Field: " + maxMagneticField + " uT");
//        }
//    }
//
//    private void detectMaxLight(float[] values) {
//        float lightIntensity = values[0];
//        if (lightIntensity > maxLight) {
//            maxLight = lightIntensity;
//            lightText.setText("Light: " + maxLight + " lux");
//        }
//    }
//
//    private void  detectMaxProximity(float[] values) {
//        float proximityDistance = values[0];
//        if (proximityDistance < maxProximity) {
//            maxProximity = proximityDistance;
//            proximityText.setText("Proximity: " + maxProximity + " cm");
//        }
//    }
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//        // Handle accuracy changes if necessary
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        // Unregister listeners when the activity is destroyed
//        if (isMonitoring) {
//            sensorManager.unregisterListener(this);
//        }
//    }
//}




















import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
//
//public class AvailableSensor extends AppCompatActivity implements SensorEventListener {
//
//    private SensorManager sensorManager;
//    private Sensor accelerometer, gyroscope, magnetometer, lightSensor, proximitySensor;
//
//    private TextView accelerationText, rotationText, magneticFieldText, lightText, proximityText, frequencyText;
//    private Button startButton, resetButton;
//
//    // Threshold values for detecting accidents (can be adjusted)
//    private static final float THRESHOLD_ACCELERATION = 15.0f;
//    private static final float THRESHOLD_ROTATION = 3.0f;
//    private static final float THRESHOLD_MAGNETIC_FIELD = 40.0f;
//    private static final float THRESHOLD_LIGHT = 10.0f;
//    private static final float THRESHOLD_PROXIMITY = 5.0f;
//    private static final double FREQUENCY_THRESHOLD = 500.0; // Example frequency threshold
//
//    private boolean isMonitoring = false;
//
//    // Track max values for each sensor
//    private float maxAcceleration = 0;
//    private float maxRotation = 0;
//    private float maxMagneticField = 0;
//    private float maxLight = 0;
//    private float maxProximity = 0;
//
//    // Microphone-related variables
//    private AudioRecord audioRecord;
//    private int bufferSize;
//    private short[] buffer;
//    private Handler handler = new Handler();
//    private boolean isRecording = false;
//    private byte[] lastAudioData;
//    private int audioDataIndex = 0;
//    private final int SAMPLE_RATE = 44100; // Sample rate for audio
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_available_sensor);
//
//        // Initialize UI elements
//        accelerationText = findViewById(R.id.accelerationText);
//        rotationText = findViewById(R.id.rotationText);
//        magneticFieldText = findViewById(R.id.magneticFieldText);
//        lightText = findViewById(R.id.lightText);
//        proximityText = findViewById(R.id.proximityText);
//        frequencyText = findViewById(R.id.frequencyText);
//
//        startButton = findViewById(R.id.startButton);
//        resetButton = findViewById(R.id.resetButton);
//
//        // Set click listeners for buttons
//        startButton.setOnClickListener(view -> startMonitoring());
//        resetButton.setOnClickListener(view -> resetMonitoring());
//
//        // Initialize SensorManager and sensors
//        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
//        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
//        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
//        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
//
//        // Initialize microphone recording
//        bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
//        buffer = new short[bufferSize];
//        lastAudioData = new byte[SAMPLE_RATE * 4]; // Store last 4 seconds of audio
//    }
//
//    private void startMonitoring() {
//        if (!isMonitoring) {
//            // Register the sensor listeners
//            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
//            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
//            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
//            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
//            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
//
//            // Start microphone recording
//            if (!isRecording) {
//                audioRecord.startRecording();
//                isRecording = true;
//                handler.post(recordingRunnable);
//            }
//
//            isMonitoring = true;
//        }
//    }
//
//    private void resetMonitoring() {
//        if (isMonitoring) {
//            // Unregister the sensor listeners
//            sensorManager.unregisterListener(this);
//
//            // Stop microphone recording
//            audioRecord.stop();
//            isRecording = false;
//            audioDataIndex = 0;
//
//            // Reset max values and update the TextViews to 0
//            maxAcceleration = 0;
//            maxRotation = 0;
//            maxMagneticField = 0;
//            maxLight = 0;
//            maxProximity = 0;
//
//            accelerationText.setText("Acceleration: 0");
//            rotationText.setText("Rotation: 0");
//            magneticFieldText.setText("Magnetic Field: 0");
//            lightText.setText("Light: 0");
//            proximityText.setText("Proximity: 0");
//            frequencyText.setText("Frequency: 0 Hz");
//
//            isMonitoring = false;
//        }
//    }
//
//    @Override
//    public void onSensorChanged(SensorEvent event) {
//        if (!isMonitoring) return;  // If not monitoring, do nothing
//
//        float[] values = event.values;
//
//        // Detect sensor value crosses threshold and show max value on the screen
//        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//            detectMaxAcceleration(values);
//        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
//            detectMaxRotation(values);
//        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
//            detectMaxMagneticField(values);
//        } else if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
//            detectMaxLight(values);
//        } else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
//            detectMaxProximity(values);
//        }
//    }
//
//    private void detectMaxAcceleration(float[] values) {
//        float accelerationMagnitude = (float) Math.sqrt(values[0] * values[0] + values[1] * values[1] + values[2] * values[2]);
//        if (accelerationMagnitude > maxAcceleration) {
//            maxAcceleration = accelerationMagnitude;
//            accelerationText.setText("Acceleration: " + maxAcceleration + " m/s²");
//        }
//    }
//
//    private void detectMaxRotation(float[] values) {
//        float rotationMagnitude = (float) Math.sqrt(values[0] * values[0] + values[1] * values[1] + values[2] * values[2]);
//        if (rotationMagnitude > maxRotation) {
//            maxRotation = rotationMagnitude;
//            rotationText.setText("Rotation: " + maxRotation + " rad/s");
//        }
//    }
//
//    private void detectMaxMagneticField(float[] values) {
//        float magneticFieldStrength = (float) Math.sqrt(values[0] * values[0] + values[1] * values[1] + values[2] * values[2]);
//        if (magneticFieldStrength > maxMagneticField) {
//            maxMagneticField = magneticFieldStrength;
//            magneticFieldText.setText("Magnetic Field: " + maxMagneticField + " uT");
//        }
//    }
//
//    private void detectMaxLight(float[] values) {
//        float lightIntensity = values[0];
//        if (lightIntensity > maxLight) {
//            maxLight = lightIntensity;
//            lightText.setText("Light: " + maxLight + " lux");
//        }
//    }
//
//    private void detectMaxProximity(float[] values) {
//        float proximityDistance = values[0];
//        if (proximityDistance < maxProximity) {
//            maxProximity = proximityDistance;
//            proximityText.setText("Proximity: " + maxProximity + " cm");
//        }
//    }
//
//    // Runnable to continuously record and process audio
//    // Runnable to continuously record and process audio
//    private Runnable recordingRunnable = new Runnable() {
//        @Override
//        public void run() {
//            if (isRecording) {
//                int readSize = audioRecord.read(buffer, 0, bufferSize);
//
//                if (readSize > 0) {
//                    // Convert short[] to byte[]
//                    for (int i = 0; i < readSize; i++) {
//                        lastAudioData[audioDataIndex + i] = (byte) buffer[i];
//                    }
//
//                    audioDataIndex = (audioDataIndex + readSize) % lastAudioData.length;
//
//                    // Analyze frequency
//                    double frequency = getMaxFrequency(lastAudioData);
////                    if (frequency > FREQUENCY_THRESHOLD) {
//                        frequencyText.setText("Frequency: " + frequency + " Hz");
////                    }
//                }
//
//                handler.postDelayed(this, 100); // Run every 100ms
//            }
//        }
//    };
//
//    // A simple method to calculate max frequency from the audio data (could use FFT for a more precise result)
//    private double getMaxFrequency(byte[] audioData) {
//        int maxFrequency = 0;
//
//        // Basic frequency analysis (this can be optimized or replaced with FFT for more accuracy)
//        for (int i = 0; i < audioData.length; i++) {
//            if (Math.abs(audioData[i]) > maxFrequency) {
//                maxFrequency = Math.abs(audioData[i]);
//            }
//        }
//
//        return maxFrequency;
//    }
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//        // Handle accuracy changes if necessary
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        // Unregister listeners and stop microphone recording when the activity is destroyed
//        if (isMonitoring) {
//            sensorManager.unregisterListener(this);
//        }
//
//        if (audioRecord != null && isRecording) {
//            audioRecord.stop();
//        }
//    }
//}


import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

public class AvailableSensor extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer, gyroscope, magnetometer, lightSensor, proximitySensor;

    private TextView accelerationText, rotationText, magneticFieldText, lightText, proximityText, frequencyText;
    private Button startButton, resetButton;

    // Threshold values for detecting accidents (can be adjusted)
    private static final float THRESHOLD_ACCELERATION = 15.0f;
    private static final float THRESHOLD_ROTATION = 3.0f;
    private static final float THRESHOLD_MAGNETIC_FIELD = 40.0f;
    private static final float THRESHOLD_LIGHT = 10.0f;
    private static final float THRESHOLD_PROXIMITY = 5.0f;
    private static final double FREQUENCY_THRESHOLD = 500.0; // Example frequency threshold

    private boolean isMonitoring = false;

    // Track max values for each sensor
    private float maxAcceleration = 0;
    private float maxRotation = 0;
    private float maxMagneticField = 0;
    private float maxLight = 0;
    private float maxProximity = 0;
    private float maxFreq = 0;

    // Microphone-related variables
    private AudioRecord audioRecord;
    private int bufferSize;
    private short[] buffer;
    private Handler handler = new Handler();
    private boolean isRecording = false;
    private byte[] lastAudioData;
    private int audioDataIndex = 0;
    private final int SAMPLE_RATE = 44100; // Sample rate for audio
    private static final int FFT_SIZE = 1024; // Size of FFT for frequency calculation
//    private static final int SAMPLE_RATE = 44100; // 44.1 kHz, typical for audio
//    private static final int FFT_SIZE = 1024;    // Size of the FFT
//    private AudioRecord audioRecord;
//    private boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_sensor);

        // Initialize UI elements
        accelerationText = findViewById(R.id.accelerationText);
        rotationText = findViewById(R.id.rotationText);
        magneticFieldText = findViewById(R.id.magneticFieldText);
        lightText = findViewById(R.id.lightText);
        proximityText = findViewById(R.id.proximityText);
        frequencyText = findViewById(R.id.frequencyText);

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

        // Initialize microphone recording
        bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        buffer = new short[bufferSize];
        lastAudioData = new byte[SAMPLE_RATE * 4]; // Store last 4 seconds of audio
        startRecording();
    }

    private void startMonitoring() {
        if (!isMonitoring) {
            // Register the sensor listeners
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);

            // Start microphone recording
            if (!isRecording) {
                audioRecord.startRecording();
                isRecording = true;
                handler.post(recordingRunnable);
            }

            isMonitoring = true;
        }
    }

    private void resetMonitoring() {
        if (isMonitoring) {
            // Unregister the sensor listeners
            sensorManager.unregisterListener(this);

            // Stop microphone recording
            audioRecord.stop();
            isRecording = false;
            audioDataIndex = 0;

            // Reset max values and update the TextViews to 0
            maxAcceleration = 0;
            maxRotation = 0;
            maxMagneticField = 0;
            maxLight = 0;
            maxProximity = 0;

            accelerationText.setText("Acceleration: 0");
            rotationText.setText("Rotation: 0");
            magneticFieldText.setText("Magnetic Field: 0");
            lightText.setText("Light: 0");
            proximityText.setText("Proximity: 0");
            frequencyText.setText("Frequency: 0 Hz");

            isMonitoring = false;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (!isMonitoring) return;  // If not monitoring, do nothing

        float[] values = event.values;

        // Detect sensor value crosses threshold and show max value on the screen
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            detectMaxAcceleration(values);
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            detectMaxRotation(values);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            detectMaxMagneticField(values);
        } else if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            detectMaxLight(values);
        } else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            detectMaxProximity(values);
        }
    }

    private void detectMaxAcceleration(float[] values) {
        float accelerationMagnitude = (float) Math.sqrt(values[0] * values[0] + values[1] * values[1] + values[2] * values[2]);
        if (accelerationMagnitude > maxAcceleration) {
            maxAcceleration = accelerationMagnitude;
            accelerationText.setText("Acceleration: " + maxAcceleration + " m/s²");
        }
    }

    private void detectMaxRotation(float[] values) {
        float rotationMagnitude = (float) Math.sqrt(values[0] * values[0] + values[1] * values[1] + values[2] * values[2]);
        if (rotationMagnitude > maxRotation) {
            maxRotation = rotationMagnitude;
            rotationText.setText("Rotation: " + maxRotation + " rad/s");
        }
    }

    private void detectMaxMagneticField(float[] values) {
        float magneticFieldStrength = (float) Math.sqrt(values[0] * values[0] + values[1] * values[1] + values[2] * values[2]);
        if (magneticFieldStrength > maxMagneticField) {
            maxMagneticField = magneticFieldStrength;
            magneticFieldText.setText("Magnetic Field: " + maxMagneticField + " uT");
        }
    }

    private void detectMaxLight(float[] values) {
        float lightIntensity = values[0];
        if (lightIntensity > maxLight) {
            maxLight = lightIntensity;
            lightText.setText("Light: " + maxLight + " lux");
        }
    }

    private void detectMaxProximity(float[] values) {
        float proximityDistance = values[0];
        if (proximityDistance < maxProximity) {
            maxProximity = proximityDistance;
            proximityText.setText("Proximity: " + maxProximity + " cm");
        }
    }

    // Runnable to continuously record and process audio
    private Runnable recordingRunnable = new Runnable() {
        @Override
        public void run() {
            if (isRecording) {
                int readSize = audioRecord.read(buffer, 0, bufferSize);

                if (readSize > 0) {
                    // Convert short[] to byte[]
                    for (int i = 0; i < readSize; i++) {
                        lastAudioData[audioDataIndex + i] = (byte) buffer[i];
                    }

                    audioDataIndex = (audioDataIndex + readSize) % lastAudioData.length;

                    // Analyze frequency
                    double frequency = getMaxFrequency(lastAudioData);
//                    frequencyText.setText("Frequency: " + frequency + " Hz");
                }

                handler.postDelayed(this, 100); // Run every 100ms
            }
        }
    };

    // A simple method to calculate max frequency from the audio data (could use FFT for a more precise result)
    private double getMaxFrequency(byte[] audioData) {
        int maxFrequency = 0;

        // Basic frequency analysis (this can be optimized or replaced with FFT for more accuracy)
        for (int i = 0; i < audioData.length; i++) {
            if (Math.abs(audioData[i]) > maxFrequency) {
                maxFrequency = Math.abs(audioData[i]);
            }
        }

        return maxFrequency;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle accuracy changes if necessary
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister listeners and stop microphone recording when the activity is destroyed
        if (isMonitoring) {
            sensorManager.unregisterListener(this);
        }

        if (audioRecord != null && isRecording) {
            audioRecord.stop();
        }
    }








    //-----------------------------------------
    private void startRecording() {
        isRecording = true;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
                FFT_SIZE * 2); // Buffer size is twice the FFT size

        audioRecord.startRecording();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                while (isRecording) {
                    short[] audioData = new short[FFT_SIZE];
                    int result = audioRecord.read(audioData, 0, FFT_SIZE);

                    if (result > 0) {
                        // Convert short array to float array (normalized)
                        float[] floatData = new float[FFT_SIZE];
                        for (int i = 0; i < FFT_SIZE; i++) {
                            floatData[i] = audioData[i] / 32768.0f; // Normalize to -1.0 to 1.0
                        }

                        // Perform FFT on audio data
                        float[] fftData = performFFT(floatData);
                        // Find the frequency with the highest magnitude

                        final double frequency = findDominantFrequency(fftData);

//                        if(frequency>maxFreq){
//                            runOnUiThread(() -> frequencyText.setText("Frequency: " + frequency + " Hz"));
//                            maxFreq= (float) frequency;

//                        }
                    }
                }
                return null;
            }
        }.execute();
    }

    private void calculateFrequency() {
        // If you're not already recording, start it
        if (!isRecording) {
            startRecording();
        }
    }

    private float[] performFFT(float[] data) {
        // Apply the FFT algorithm
        // Real FFT implementation (simple version for clarity)
        float[] fftData = new float[data.length];
        Arrays.fill(fftData, 0f);

        for (int i = 0; i < data.length; i++) {
            fftData[i] = data[i];
        }

        // Perform the FFT (you could use an external library here)
        for (int i = 0; i < data.length / 2; i++) {
            float real = fftData[i] * (float) Math.cos(2 * Math.PI * i / data.length);
            float imag = fftData[i] * (float) Math.sin(2 * Math.PI * i / data.length);
            fftData[i] = real;
        }

        return fftData;
    }

    private double findDominantFrequency(float[] fftData) {
        int maxIndex = 0;
        float maxMagnitude = 0;

        // Find the frequency with the highest magnitude
        for (int i = 0; i < fftData.length / 2; i++) {
            float magnitude = (float) Math.sqrt(fftData[i] * fftData[i]);
            if (magnitude > maxMagnitude) {
                maxMagnitude = magnitude;
                maxIndex = i;
            }
        }

        // Convert the index to frequency
        return maxIndex * SAMPLE_RATE / (double) FFT_SIZE;
    }










}

