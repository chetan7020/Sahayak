package com.example.saftytracking.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.saftytracking.Dashboard;
import com.example.saftytracking.EmergencySos.AlertTO;
import com.example.saftytracking.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText emailField, passwordField;
    private Button loginButton;
    private FirebaseAuth mAuth;
    private static final String PREFS_NAME = "UserPrefs";
    private static final String PREF_EMAIL_KEY = "userEmail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailField = findViewById(R.id.etEmail);
        loginButton = findViewById(R.id.btnSignUp);

        // Get SharedPreferences to check if the email is already stored
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedEmail = prefs.getString(PREF_EMAIL_KEY, null);

        // If the email is found, set it in the email field and proceed (auto login)
        if (savedEmail != null) {
            emailField.setText(savedEmail);
            Intent intent = new Intent(LoginActivity.this,Dashboard.class);
            intent.putExtra("email",savedEmail);
            startActivity(intent);
            Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show();
        }

        // Set onClickListener for the login button
        loginButton.setOnClickListener(v -> {
            String email = emailField.getText().toString();

            // Check if email is entered
            if (email.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
            } else {
                // Save the email to SharedPreferences
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(PREF_EMAIL_KEY, email);
                editor.apply();  // Apply the changes

                // Simulate login process (for simplicity, we just show a message)
                Toast.makeText(LoginActivity.this, "Login successful for: " + email, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this,Dashboard.class);
                intent.putExtra("email",email);
                startActivity(intent);
                // You can navigate to another screen after this if needed
                // Example: startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            }
        });
    }
}