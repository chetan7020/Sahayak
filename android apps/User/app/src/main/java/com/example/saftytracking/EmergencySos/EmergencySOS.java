package com.example.saftytracking.EmergencySos;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.saftytracking.R;
import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
public class EmergencySOS  extends AppCompatActivity {

    private Button sosButton, addEmergencyButton;
    private String emergencyNumber = "7387579912";
    private long lastPressTime = 0;
    private long pressInterval = 5000;
    private boolean volumeUpPressed = false;
    private boolean volumeDownPressed = false;
    private long volumeUpPressStartTime = 0;
    private long volumeDownPressStartTime = 0;
    private long longPressThreshold = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_sos);

        sosButton = findViewById(R.id.sosButton);
        addEmergencyButton = findViewById(R.id.addEmergencyButton);

        // Request SMS permissions
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.SEND_SMS
        }, 1);

        // SOS button press - send SMS
        sosButton.setOnClickListener(v -> sendSosMessage());

        // Add Emergency Number (for demonstration purposes, this can be extended to allow user input)
        addEmergencyButton.setOnClickListener(v -> {
            emergencyNumber = "7387579912"; // Example for demonstration
            Toast.makeText(this, "Emergency Number Added", Toast.LENGTH_SHORT).show();
        });
    }

    // Function to send SOS message
    private void sendSosMessage() {
        if (emergencyNumber != null) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(emergencyNumber, null, "SOS! I need help!", null, null);
            Toast.makeText(this, "SOS Message Sent", Toast.LENGTH_SHORT).show();
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
}