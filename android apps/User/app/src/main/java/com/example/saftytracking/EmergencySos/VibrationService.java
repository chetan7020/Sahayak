package com.example.saftytracking.EmergencySos;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;

import com.example.saftytracking.R;

public class VibrationService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        String channelId = "VibrationServiceChannel";
        createNotificationChannel(channelId);

        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this, channelId)
                    .setContentTitle("Vibration Alert")
                    .setContentText("Vibration ongoing...")
                    .build();
        }

        startForeground(1, notification);

        triggerInfiniteVibration();
    }

    private void triggerInfiniteVibration() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null) {
            long[] pattern = {0, 1000, 1000}; // Vibrate for 1 second, pause for 1 second
            vibrator.vibrate(pattern, 0); // Repeat indefinitely
        }
    }

    private void createNotificationChannel(String channelId) {
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(
                    channelId,
                    "Vibration Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
        }
        NotificationManager manager = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager = getSystemService(NotificationManager.class);
        }
        if (manager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
