package com.example.saftytracking.EmergencySos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;

public class PhoneCallHelper {

    private static final int REQUEST_CALL_PERMISSION = 1;

    // Method to make a phone call
    public static void makePhoneCall(Context context, String phoneNumber) {
        // Check if the app has permission to make calls
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
            // If permission is granted, make the call
            initiateCall(context, phoneNumber);
        } else {
            // If permission is not granted, request permission
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE},
                    REQUEST_CALL_PERMISSION);
        }
    }

    // Method to handle permission result
    public static void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults, Context context, String phoneNumber) {
        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // If permission is granted, initiate the call
                initiateCall(context, phoneNumber);
            } else {
                // If permission is denied, show a message
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Helper method to initiate the phone call
    public static void initiateCall(Context context, String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        context.startActivity(callIntent);
    }


}
