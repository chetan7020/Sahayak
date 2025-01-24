package com.example.admin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.admin.R;
import com.example.admin.models.RestrictedModel;

import java.util.List;

public class RestrictedAdapter extends RecyclerView.Adapter<RestrictedAdapter.RestrictedViewHolder> {

    private List<RestrictedModel> restrictedList;

    public RestrictedAdapter(List<RestrictedModel> restrictedList) {
        this.restrictedList = restrictedList;
    }

    @Override
    public RestrictedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_restricted, parent, false);
        return new RestrictedViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RestrictedViewHolder holder, int position) {
        RestrictedModel restricted = restrictedList.get(position);
        holder.restrictedName.setText(restricted.getRestrictedName());
        holder.restrictedId.setText(restricted.getRestrictedId());
    }

    @Override
    public int getItemCount() {
        return restrictedList.size();
    }

    public static class RestrictedViewHolder extends RecyclerView.ViewHolder {

        public TextView restrictedName;
        public TextView restrictedId;

        public RestrictedViewHolder(View itemView) {
            super(itemView);
            restrictedName = itemView.findViewById(R.id.restrictedName);
            restrictedId = itemView.findViewById(R.id.restrictedId);
        }
    }
}

