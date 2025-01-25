package com.example.saftytracking.GMap;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class SensorHelper implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer, gyroscope, magnetometer, lightSensor, proximitySensor;

    // Current sensor values
    private float[] acceleration = new float[3];
    private float[] rotation = new float[3];
    private float[] magneticField = new float[3];
    private float light = 0;
    private float proximity = 0;

    public SensorHelper(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        // Initialize sensors
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    public void startMonitoring() {
        // Register the sensor listeners
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_UI);
    }

    public void stopMonitoring() {
        // Unregister the sensor listeners
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event == null) return;

        // Store real-time sensor values
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            acceleration = event.values.clone();
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            rotation = event.values.clone();
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magneticField = event.values.clone();
        } else if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            light = event.values[0];
        } else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            proximity = event.values[0];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle sensor accuracy changes (optional)
    }

    // Methods to retrieve real-time sensor values
    public float getAcceleration() {
        return  (float) Math.sqrt(acceleration[0] * acceleration[0] + acceleration[1] * acceleration[1] + acceleration[2] * acceleration[2]);

    }

    public float getRotation() {
        return (float) Math.sqrt(rotation[0] * rotation[0] + rotation[1] * rotation[1] + rotation[2] * rotation[2]);


    }

    public float getMagneticField() {
        return  (float) Math.sqrt(magneticField[0] * magneticField[0] + magneticField[1] * magneticField[1] + magneticField[2] * magneticField[2]);

    }

    public float getLight() {
        return light;
    }

    public float getProximity() {
        return proximity;
    }




}
