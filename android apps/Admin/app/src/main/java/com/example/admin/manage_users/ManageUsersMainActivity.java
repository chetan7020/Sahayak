package com.example.admin.manage_users;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admin.adapters.UserAdapter;
import com.example.admin.databinding.ActivityManageUsersMainBinding;
import com.example.admin.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ManageUsersMainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private ActivityManageUsersMainBinding binding;

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<UserModel> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageUsersMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(this, userList);
        binding.recyclerView.setAdapter(userAdapter);

        // Fetch data from Firestore
        fetchDataFromFirestore();

        // Add new user button functionality
        binding.btnAddUser.setOnClickListener(v -> {
            startActivity(new Intent(ManageUsersMainActivity.this, AddUserActivity.class));
            finish();
        });
    }

    private void fetchDataFromFirestore() {
        String currentUserEmail = firebaseAuth.getCurrentUser().getEmail();

        if (currentUserEmail != null) {
            firebaseFirestore
                    .collection("admins")
                    .document(currentUserEmail)
                    .collection("users")  // Assuming users are stored under this sub-collection
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                userList.clear(); // Clear the list to avoid duplication
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("TAG", "onComplete: " + document.toString());
                                    UserModel userModel = document.toObject(UserModel.class);
                                    if (userModel.isVolunteer()) {
                                        continue;
                                    }
                                    userList.add(userModel);
                                }
                                userAdapter.notifyDataSetChanged();
                            } else {
                                // Handle error
                                String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                                Log.e("Error fetching data", error);
                                Toast.makeText(ManageUsersMainActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
