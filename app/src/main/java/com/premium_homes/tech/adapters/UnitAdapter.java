package com.premium_homes.tech.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import com.premium_homes.tech.R;
import com.premium_homes.tech.models.UnitItem;

public class UnitAdapter extends RecyclerView.Adapter<UnitAdapter.ViewHolder> {

    private List<UnitItem> unitList;

    public UnitAdapter(List<UnitItem> unitList) {
        this.unitList = unitList;
    }

    // Add this updateList method
    public void updateList(List<UnitItem> newList) {
        this.unitList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_unit, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UnitItem item = unitList.get(position);
        holder.unitName.setText(item.getUnitName());
        holder.unitArea.setText(item.getArea());
        holder.bedrooms.setText(item.getBedrooms());
        holder.bathrooms.setText(item.getBathrooms());
    }

    @Override
    public int getItemCount() {
        return unitList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView unitName, unitArea, bedrooms, bathrooms;

        ViewHolder(View itemView) {
            super(itemView);
            unitName = itemView.findViewById(R.id.unitName);
            unitArea = itemView.findViewById(R.id.unitArea);
            bedrooms = itemView.findViewById(R.id.bedRooms);
            bathrooms = itemView.findViewById(R.id.bathRoom);
        }
    }
}