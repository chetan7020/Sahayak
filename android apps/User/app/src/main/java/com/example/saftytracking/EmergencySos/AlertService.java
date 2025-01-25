package com.example.saftytracking.EmergencySos;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import android.Manifest;

public class AlertService extends Service {

    private String emergencyNumber = "7387579912";  // Add emergency number here (from user input or settings)
    private String alertSenderNumber = "+917387579912"; // The number that triggers the alert
    private Vibrator vibrator;
    private boolean isVibrating = false; // To track whether the phone is currently vibrating

    public AlertService() {}

    @Override
    public void onCreate() {
        super.onCreate();

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        // Register SMS Receiver to listen for incoming messages
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsReceiver, filter);

        // Register BroadcastReceiver for screen-off event
        IntentFilter screenOffFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenOffReceiver, screenOffFilter);
    }

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
            // Pattern for continuous vibration (vibrate for 1000ms, pause for 1000ms)
            long[] vibrationPattern = {0, 1000, 200, 600, 200, 1000, 200, 600};

            // For API level 26 (Oreo) and above, use VibrationEffect
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                VibrationEffect effect = VibrationEffect.createWaveform(vibrationPattern, VibrationEffect.DEFAULT_AMPLITUDE);
//                vibrator.vibrate(effect); // Vibrates indefinitely
//            } else {
                vibrator.vibrate(vibrationPattern, 0); // Vibrates indefinitely (on older devices)
//            }

            // Show a toast to indicate the alert
            Toast.makeText(this, "ALERT! Check your phone for emergency.", Toast.LENGTH_SHORT).show();
        }
    }

    // Function to send an SOS message to the emergency contact
    private void sendSosMessage() {
        if (emergencyNumber != null) {
//            SmsManager smsManager = SmsManager.getDefault();
//            smsManager.sendTextMessage(emergencyNumber, null, "SOS! ALERT message received!", null, null);
//            Toast.makeText(this, "SOS Message Sent", Toast.LENGTH_SHORT).show();
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
    public IBinder onBind(Intent intent) {
        // Not used in this case
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister the receivers to avoid memory leaks
        unregisterReceiver(smsReceiver);
        unregisterReceiver(screenOffReceiver);
    }
}
