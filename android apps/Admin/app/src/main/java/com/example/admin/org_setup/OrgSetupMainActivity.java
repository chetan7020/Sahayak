package com.example.admin.org_setup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admin.adapters.FloorAdapter;
import com.example.admin.databinding.ActivityOrgSetupMainBinding;
import com.example.admin.models.CornerModel;
import com.example.admin.models.FloorModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrgSetupMainActivity extends AppCompatActivity {

    private ActivityOrgSetupMainBinding binding;

    private RecyclerView recyclerView;
    private FloorAdapter floorAdapter;
    private List<FloorModel> floorList;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityOrgSetupMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        floorList = new ArrayList<>();
        floorAdapter = new FloorAdapter(floorList);
        binding.recyclerView.setAdapter(floorAdapter);

        // Fetch data from Firestore
        fetchDataFromFirestore();

        // Add new floor button functionality
        binding.btnAddFloor.setOnClickListener(v -> {
            startActivity(new Intent(OrgSetupMainActivity.this, AddFloorActivity.class));
            finish();
        });
    }

    private void fetchDataFromFirestore() {
        String currentUserEmail = firebaseAuth.getCurrentUser().getEmail();

        if (currentUserEmail != null) {
            firebaseFirestore
                    .collection("admins")
                    .document(currentUserEmail)
                    .collection("floors")  // Assuming floors are stored under this sub-collection
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                floorList.clear(); // Clear the list to avoid duplication
                                Toast.makeText(OrgSetupMainActivity.this, "" + task.getResult().size(), Toast.LENGTH_SHORT).show();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("TAG", "onComplete: " + document.toString());
                                    // Convert Firestore document to FloorModel
                                    FloorModel floorModel = document.toObject(FloorModel.class);
                                    floorList.add(floorModel);
                                }
                                // Notify the adapter about data changes
                                floorAdapter.notifyDataSetChanged();
                            } else {
                                // Handle error
                                String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                                Log.e("Error fetching data", error);
                                Toast.makeText(OrgSetupMainActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
