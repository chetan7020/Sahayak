package com.example.saftytracking.Login;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.saftytracking.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegistrationActivity extends AppCompatActivity {

    private EditText emailField, passwordField, userNameField, latField, langField, altitudeField, adminIdField;
    private Button registerButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        userNameField = findViewById(R.id.userNameField);
        latField = findViewById(R.id.latField);
        langField = findViewById(R.id.langField);
        altitudeField = findViewById(R.id.altitudeField);
        adminIdField = findViewById(R.id.adminIdField);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> {
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();
            String userName = userNameField.getText().toString();
            double lat = Double.parseDouble(latField.getText().toString());
            double lang = Double.parseDouble(langField.getText().toString());
            double altitude = Double.parseDouble(altitudeField.getText().toString());
            String adminId = adminIdField.getText().toString();

            if (!email.isEmpty() && !password.isEmpty() && !userName.isEmpty()) {
                registerUser(email, password, userName, lat, lang, altitude, adminId);
            } else {
                Toast.makeText(RegistrationActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerUser(String email, String password, String userName, double lat, double lang, double altitude, String adminId) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        UserModel user = new UserModel();

                        // Save user data to Firestore
                        db.collection("users")
                                .document(mAuth.getCurrentUser().getUid())  // Use user UID as document ID
                                .set(user)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(RegistrationActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(RegistrationActivity.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(RegistrationActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
