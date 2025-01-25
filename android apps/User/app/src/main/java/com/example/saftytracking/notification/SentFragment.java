package com.example.saftytracking.notification;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.saftytracking.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SentFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<NotificationModel> notificationList;


    public SentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sent, container, false);

        // Initialize RecyclerView and data
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize data and adapter
        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(notificationList,getContext());
        recyclerView.setAdapter(adapter);

        // Load dummy data
        loadNotifications();

        return view;
    }

    private void loadNotifications() {
        SharedPreferences prefs = getContext().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String savedEmail = prefs.getString("userEmail", null);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(savedEmail+"_sent")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            int i= 0;
                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                i++;
                                if(i>5){
                                    continue;
                                }
                                NM sentItem = document.toObject(NM.class);
//                                sentList.add(sentItem); // Add to the ArrayList
                                notificationList.add(new NotificationModel(sentItem.getName(), sentItem.getPhoneNumber(), sentItem.getLat(), sentItem.getLang(), sentItem.getLat(), "-"));
                                adapter.notifyDataSetChanged();



                            }


                        }
                    }
                });

    }
}