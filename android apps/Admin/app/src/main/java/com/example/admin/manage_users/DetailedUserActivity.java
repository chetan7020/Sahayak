package com.example.admin.manage_users;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.admin.R;
import com.example.admin.databinding.ActivityDetailedUserBinding;
import com.example.admin.models.FloorModel;
import com.example.admin.models.UserModel;
import com.example.admin.org_setup.OrgSetupMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

public class DetailedUserActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private ActivityDetailedUserBinding binding;

    private List<FloorModel> floorList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        floorList = new ArrayList<>();

        binding = ActivityDetailedUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        UserModel userModel = (UserModel) getIntent().getSerializableExtra("user");

        // Set the user details in the views via binding
        if (userModel != null) {
            binding.userNameTextView.setText(userModel.getUserName());
            binding.userEmailTextView.setText(userModel.getUserEmail());
            binding.adminEmailTextView.setText(userModel.getAdminEmail());
            binding.isVolunteerTextView.setText(userModel.isVolunteer() ? "Yes" : "No");

            if (userModel.getCallFlag() == 1) {
                binding.swCallFlag.setChecked(true);
            } else {
                binding.swCallFlag.setChecked(false);
            }

            String location = "Lat: " + userModel.getLat() + ", Lang: " + userModel.getLang() + ", Alt: " + userModel.getAltitude();
            binding.locationDetailsTextView.setText(location);

            String floorName = getFloor(userModel);
            binding.currentFloor.setText(floorName);

            binding.swCallFlag.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    // When switch is ON
//                    Toast.makeText(MainActivity.this, "Switch is ON", Toast.LENGTH_SHORT).show();
                    userModel.setCallFlag(1);
                } else {
                    // When switch is OFF
//                    Toast.makeText(MainActivity.this, "Switch is OFF", Toast.LENGTH_SHORT).show();
                    userModel.setCallFlag(1);
                }
            });
        }

        binding.btnUpdate.setOnClickListener(v -> {
            firebaseFirestore
                    .collection("admins")
                    .document(firebaseAuth.getCurrentUser().getEmail())
                    .collection("users")
                    .document(userModel.getUserEmail())
                    .set(userModel)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(DetailedUserActivity.this, "Successfully Updated", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(DetailedUserActivity.this, "Failed to Updated :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }


    private void fetchDataFromFirestore() {
        String currentUserEmail = firebaseAuth.getCurrentUser().getEmail();

        if (currentUserEmail != null) {
            firebaseFirestore
                    .collection("admins")
                    .document(currentUserEmail)
                    .collection("floors")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                floorList.clear(); // Clear the list to avoid duplication
                                Toast.makeText(DetailedUserActivity.this, "" + task.getResult().size(), Toast.LENGTH_SHORT).show();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("TAG", "onComplete: " + document.toString());
                                    // Convert Firestore document to FloorModel
                                    FloorModel floorModel = document.toObject(FloorModel.class);
                                    floorList.add(floorModel);
                                }
                            } else {
                                // Handle error
                                String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                                Log.e("Error fetching data", error);
                                Toast.makeText(DetailedUserActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }


    private String getFloor(UserModel userModel) {

        fetchDataFromFirestore();

        String floorName = "";

        for (FloorModel floorModel : floorList) {
            if (check(userModel, floorModel) == true) {
                floorName = floorModel.getFloorName();
                break;
            }
        }

        return floorName;
    }

    private boolean check(UserModel userModel, FloorModel floorModel) {

        double minLat = Math.min(Math.min(floorModel.getCornerModel1().getLat(), floorModel.getCornerModel2().getLat()), Math.min(Math.min(floorModel.getCornerModel3().getLat(), floorModel.getCornerModel4().getLat()), Math.min(Math.min(floorModel.getCornerModel5().getLat(), floorModel.getCornerModel6().getLat()), Math.min(floorModel.getCornerModel7().getLat(), floorModel.getCornerModel8().getLat()))));
        double maxLat = Math.max(Math.max(floorModel.getCornerModel1().getLat(), floorModel.getCornerModel2().getLat()), Math.max(Math.max(floorModel.getCornerModel3().getLat(), floorModel.getCornerModel4().getLat()), Math.max(Math.max(floorModel.getCornerModel5().getLat(), floorModel.getCornerModel6().getLat()), Math.max(floorModel.getCornerModel7().getLat(), floorModel.getCornerModel8().getLat()))));

        double minLng = Math.min(Math.min(floorModel.getCornerModel1().getLang(), floorModel.getCornerModel2().getLang()), Math.min(Math.min(floorModel.getCornerModel3().getLang(), floorModel.getCornerModel4().getLang()), Math.min(Math.min(floorModel.getCornerModel5().getLang(), floorModel.getCornerModel6().getLang()), Math.min(floorModel.getCornerModel7().getLang(), floorModel.getCornerModel8().getLang()))));
        double maxLng = Math.max(Math.max(floorModel.getCornerModel1().getLang(), floorModel.getCornerModel2().getLang()), Math.max(Math.max(floorModel.getCornerModel3().getLang(), floorModel.getCornerModel4().getLang()), Math.max(Math.max(floorModel.getCornerModel5().getLang(), floorModel.getCornerModel6().getLang()), Math.max(floorModel.getCornerModel7().getLang(), floorModel.getCornerModel8().getLang()))));

        double minAltitude = Math.min(Math.min(floorModel.getCornerModel1().getAltitude(), floorModel.getCornerModel2().getAltitude()), Math.min(Math.min(floorModel.getCornerModel3().getAltitude(), floorModel.getCornerModel4().getAltitude()), Math.min(Math.min(floorModel.getCornerModel5().getAltitude(), floorModel.getCornerModel6().getAltitude()), Math.min(floorModel.getCornerModel7().getAltitude(), floorModel.getCornerModel8().getAltitude()))));
        double maxAltitude = Math.max(Math.max(floorModel.getCornerModel1().getAltitude(), floorModel.getCornerModel2().getAltitude()), Math.max(Math.max(floorModel.getCornerModel3().getAltitude(), floorModel.getCornerModel4().getAltitude()), Math.max(Math.max(floorModel.getCornerModel5().getAltitude(), floorModel.getCornerModel6().getAltitude()), Math.max(floorModel.getCornerModel7().getAltitude(), floorModel.getCornerModel8().getAltitude()))));

        return (userModel.getLat() >= minLat && userModel.getLat() <= maxLat) &&
                (userModel.getLang() >= minLng && userModel.getLang() <= maxLng) &&
                (userModel.getAltitude() >= minAltitude && userModel.getAltitude() <= maxAltitude);
    }
}