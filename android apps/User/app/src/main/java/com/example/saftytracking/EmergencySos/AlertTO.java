package com.example.saftytracking.EmergencySos;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.saftytracking.R;

public class AlertTO extends AppCompatActivity {

    private String emergencyNumber = "7387579912";  // Add emergency number here (from user input or settings)
    private String alertSenderNumber = "+917387579912"; // The number that triggers the alert
    private Vibrator vibrator;
    private static final int SMS_PERMISSION_CODE = 101;


    private boolean isVibrating = false; // To track whether the phone is currently vibrating
    private static final int MY_PERMISSIONS_REQUEST_RECEIVE_SMS = 1;
    private static final int MY_PERMISSIONS_REQUEST_VIBRATE = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_to);





        requestIgnoreBatteryOptimizations();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS}, SMS_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show();
                // Permission granted
            } else {
                Toast.makeText(this, "Denaid", Toast.LENGTH_SHORT).show();
                // Permission denied
            }
        }
    }

    private void requestIgnoreBatteryOptimizations() {
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (powerManager != null && !powerManager.isIgnoringBatteryOptimizations(getPackageName())) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        }
    }



//        ActivityCompat.requestPermissions(this, new String[] {
//                Manifest.permission.SEND_SMS,
//                Manifest.permission.RECEIVE_SMS,
//                Manifest.permission.VIBRATE
//        }, 1);
//
//        Intent alertServiceIntent = new Intent(this, AlertService.class);
//        startService(alertServiceIntent);

        // Request SMS and vibrate permissions
//        ActivityCompat.requestPermissions(this, new String[] {
//                Manifest.permission.SEND_SMS,
//                Manifest.permission.RECEIVE_SMS,
//                Manifest.permission.VIBRATE
//        }, 1);

//        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//
//        // Register SMS Receiver
//        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
//        registerReceiver(smsReceiver, filter);
//
//        // Register BroadcastReceiver for screen-off event
//        IntentFilter screenOffFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
//        registerReceiver(screenOffReceiver, screenOffFilter);
//    }

    // BroadcastReceiver to listen for incoming SMS
    private final BroadcastReceiver smsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            if (pdus != null) {
                for (Object pdu : pdus) {
                    SmsMessage message = SmsMessage.createFromPdu((byte[]) pdu);
                    String messageBody = message.getMessageBody();
                    String senderNumber = message.getDisplayOriginatingAddress(); // Get the sender's number

                    // Display sender info for debugging
                    Toast.makeText(context, "Message received from: " + senderNumber + "\n" + "Message: " + messageBody, Toast.LENGTH_SHORT).show();

                    // Check if message contains the word "ALERT" and is from the specific number
                    if (messageBody != null && messageBody.contains("ALERT") && senderNumber.equals(alertSenderNumber)) {
                        // Trigger custom rhythmic vibration and send SOS message
                        triggerContinuousVibration();

                        // Send SOS message to emergency number
                        sendSosMessage();
                    }
                }
            }
        }
    };

    // Function to trigger continuous phone vibration
    private void triggerContinuousVibration() {
        if (vibrator != null) {
            if (!isVibrating) {
                // Pattern for continuous vibration (vibrate for 1000ms, pause for 1000ms)
                long[] vibrationPattern = {0, 1000, 1000}; // Keep vibrating continuously in 1s cycles
                vibrator.vibrate(vibrationPattern, 0); // The '0' here means it will repeat indefinitely
                isVibrating = true;
                Toast.makeText(this, "ALERT! Check your phone for emergency.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Function to send an SOS message to the emergency contact
    private void sendSosMessage() {
        if (emergencyNumber != null) {
            SmsManager smsManager = SmsManager.getDefault();
//            smsManager.sendTextMessage(emergencyNumber, null, "SOS! ALERT message received!", null, null);
            Toast.makeText(this, "SOS Message Sent", Toast.LENGTH_SHORT).show();
        }
    }

    // BroadcastReceiver to listen for screen-off event (power button pressed)
    private final BroadcastReceiver screenOffReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Stop vibration when screen is off (i.e., power button is pressed)
            if (isVibrating) {
                vibrator.cancel(); // Stop the continuous vibration
                isVibrating = false;
                Toast.makeText(context, "Vibration stopped due to power button press.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the receivers to avoid memory leaks
        unregisterReceiver(smsReceiver);
        unregisterReceiver(screenOffReceiver);
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        PhoneCallHelper.onRequestPermissionsResult(requestCode, permissions, grantResults, this, phoneNumber);
//    }
}
