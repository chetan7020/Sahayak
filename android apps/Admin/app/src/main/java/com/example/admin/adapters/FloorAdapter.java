package com.example.admin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.admin.R;
import com.example.admin.models.FloorModel;

import java.util.List;

public class FloorAdapter extends RecyclerView.Adapter<FloorAdapter.FloorViewHolder> {

    private List<FloorModel> floorList;

    public FloorAdapter(List<FloorModel> floorList) {
        this.floorList = floorList;
    }

    @Override
    public FloorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_floor, parent, false);
        return new FloorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FloorViewHolder holder, int position) {
        FloorModel floor = floorList.get(position);
        holder.floorName.setText(floor.getFloorName());
        holder.floorId.setText(floor.getFloorId());
    }

    @Override
    public int getItemCount() {
        return floorList.size();
    }

    public static class FloorViewHolder extends RecyclerView.ViewHolder {

        public TextView floorName;
        public TextView floorId;

        public FloorViewHolder(View itemView) {
            super(itemView);
            floorName = itemView.findViewById(R.id.floorName);
            floorId = itemView.findViewById(R.id.floorId);
        }
    }
}
