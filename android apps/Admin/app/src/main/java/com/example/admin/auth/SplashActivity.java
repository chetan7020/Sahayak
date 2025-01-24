package com.example.admin.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.admin.MainActivity;
import com.example.admin.R;
import com.example.admin.databinding.ActivityRegisterBinding;
import com.example.admin.databinding.ActivitySplashBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private ActivitySplashBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

//        new Handler().postDelayed(new Runnable() {
//            public void run() {
//                if (firebaseAuth.getCurrentUser() != null) {
//                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
//                    finish();
//                } else {
//                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
//                    finish();
//                }
//            }
//        }, 1000);
    }
}