package com.example.admin.restricted_setup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.admin.adapters.RestrictedAdapter;
import com.example.admin.databinding.ActivityRestrictedSetupMainBinding;
import com.example.admin.models.RestrictedModel;
import com.example.admin.org_setup.AddFloorActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class RestrictedSetupMainActivity extends AppCompatActivity {
    private ActivityRestrictedSetupMainBinding binding;

    private RestrictedAdapter restrictedAdapter;
    private List<RestrictedModel> restrictedList;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRestrictedSetupMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        restrictedList = new ArrayList<>();
        restrictedAdapter = new RestrictedAdapter(restrictedList);
        binding.recyclerView.setAdapter(restrictedAdapter);

        // Fetch data from Firestore
        fetchDataFromFirestore();

        // Add new restricted button functionality
        binding.btnAddRestricted.setOnClickListener(v -> {
            startActivity(new Intent(RestrictedSetupMainActivity.this, AddRestrictedActivity.class));
            finish();
        });
    }

    private void fetchDataFromFirestore() {
        String currentUserEmail = firebaseAuth.getCurrentUser().getEmail();

        if (currentUserEmail != null) {
            firebaseFirestore
                    .collection("admins")
                    .document(currentUserEmail)
                    .collection("restricted")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
//                                Toast.makeText(RestrictedSetupMainActivity.this, "HW" + task.getResult().size(), Toast.LENGTH_SHORT).show();
                                restrictedList.clear(); // Clear the list to avoid duplication
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("TAG", "onComplete: " + document.toString());
                                    // Convert Firestore document to RestrictedModel
                                    
                                    RestrictedModel restrictedModel = document.toObject(RestrictedModel.class);
                                    restrictedList.add(restrictedModel);
                                }
                                // Notify the adapter about data changes
                                restrictedAdapter.notifyDataSetChanged();
                            } else {
                                // Handle error
                                String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                                System.out.println("Error fetching data: " + error);
                            }
                        }
                    });
        }
    }
}
