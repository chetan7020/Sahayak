package com.example.saftytracking.notification;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saftytracking.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<NotificationModel> notificationList;
    Context context;
    public NotificationAdapter(List<NotificationModel> notificationList, Context context) {
        this.notificationList = notificationList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationModel notification = notificationList.get(position);

        holder.nameText.setText("Name: " + notification.getName());
        holder.mobileText.setText("Mobile: " + notification.getMobile());
        String a = getAddress(notification.getLat(),notification.getLang());
        if(a!=null)
        holder.addressText.setText("Address: " + a);
//        holder.latText.setText("Lat: " + notification.getLat());
//        holder.langText.setText("Lang: " + notification.getLang());
//        holder.altitudeText.setText("Altitude: " + notification.getAltitude());
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, mobileText, addressText, latText, langText, altitudeText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.userNameText);
            mobileText = itemView.findViewById(R.id.phoneNumberText);
            addressText = itemView.findViewById(R.id.addressText);
//            latText = itemView.findViewById(R.id.latText);
//            langText = itemView.findViewById(R.id.langText);
//            altitudeText = itemView.findViewById(R.id.altitudeText);
        }
    }


    String getAddress(double latitude,double longitude){
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return address.getAddressLine(0); // Full address as a single string
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Unable to fetch address.";
    }
}
