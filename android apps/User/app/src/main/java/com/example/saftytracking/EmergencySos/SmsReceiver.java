//package com.example.saftytracking.EmergencySos;
//
//
//
//
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//import android.os.Vibrator;
//import android.provider.Telephony;
//import android.telephony.SmsMessage;
//import android.widget.Toast;
//
//import androidx.core.app.NotificationCompat;
//
//public class SmsReceiver extends BroadcastReceiver {
//
//    private static final String CHANNEL_ID = "sms_alert_channel";
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        // Check if the received intent action is SMS_RECEIVED
//        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
//            SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
//            for (SmsMessage message : messages) {
//                String sender = message.getDisplayOriginatingAddress();
//                String messageBody = message.getMessageBody();
//
//                // Trigger vibration
//                triggerVibration(context);
//
//                // Show a toast for debugging
//                Toast.makeText(context, "SMS from: " + sender, Toast.LENGTH_LONG).show();
//
//                // Send notification
//                sendNotification(context, sender, messageBody);
//            }
//        }
//    }
//
//    // Trigger the phone vibration
//    private void triggerVibration(Context context) {
//        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
//        if (vibrator != null) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                vibrator.vibrate(android.os.VibrationEffect.createOneShot(2000, android.os.VibrationEffect.DEFAULT_AMPLITUDE));
//            } else {
//                vibrator.vibrate(2000);  // Vibrate for 2 seconds
//            }
//        }
//    }
//
//    // Send notification to the user
//    private void sendNotification(Context context, String sender, String messageBody) {
//        // Create a notification manager
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        // Create a notification channel for Android 8.0 and above
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(
//                    CHANNEL_ID,
//                    "SMS Alerts",
//                    NotificationManager.IMPORTANCE_HIGH
//            );
//            channel.setDescription("Notifications for incoming SMS");
//            channel.enableVibration(true);
//            notificationManager.createNotificationChannel(channel);
//        }
//
//        // Create an intent to open your app when the notification is clicked
//        Intent openAppIntent = new Intent(context, AlertTO.class);
//        openAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(
//                context,
//                0,
//                openAppIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
//        );
//
//        // Build the notification
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
//                .setSmallIcon(android.R.drawable.ic_dialog_info) // Replace with your app's icon
//                .setContentTitle("SMS from: " + sender)
//                .setContentText(messageBody)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setAutoCancel(true)
//                .setContentIntent(pendingIntent); // Set the intent to open the app
//
//        // Show the notification
//        notificationManager.notify(1, builder.build());
//    }
//}
//


package com.example.saftytracking.EmergencySos;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.saftytracking.Dashboard;
import com.example.saftytracking.R;

//import com.example.saftytracking.Manifest;

//import com.example.saftytracking.Manifest;

public class SmsReceiver extends BroadcastReceiver {
    private boolean isVolumeUpPressed = false;
    private boolean isVolumeDownPressed = false;
    private Vibrator vibrator;
    private static final String CHANNEL_ID = "sms_alert_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Check if the received intent action is SMS_RECEIVED
//        Toast.makeText(context, "comeee", Toast.LENGTH_SHORT).show();
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            for (SmsMessage message : messages) {
                String sender = message.getDisplayOriginatingAddress();
                String messageBody = message.getMessageBody();
                Log.d("MyTag", "This is a debug message");

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + "7823859849"));
                callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                Toast.makeText(context, messageBody, Toast.LENGTH_SHORT).show();
                if(messageBody.equals("CALL_BACK")){
                    context.startActivity(callIntent);

                }else if(messageBody.equals("ALERT")){
                    sendNotificationWithVibration(context, sender, messageBody);

                }



                // Optionally, show a toast for debugging
//                Toast.makeText(context, "SMS from: " + sender, Toast.LENGTH_LONG).show();
            }
        }
    }

    // Send notification and trigger long vibration
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
        scheduleVibrationStop(20000);
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
