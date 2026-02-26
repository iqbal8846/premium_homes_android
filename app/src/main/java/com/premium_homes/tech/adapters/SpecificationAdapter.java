package com.premium_homes.tech.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import com.premium_homes.tech.R;
import com.premium_homes.tech.models.SpecificationItem;

public class SpecificationAdapter extends RecyclerView.Adapter<SpecificationAdapter.ViewHolder> {

    private List<SpecificationItem> specificationList;

    public SpecificationAdapter(List<SpecificationItem> specificationList) {
        this.specificationList = specificationList;
    }

    // Add this updateList method
    public void updateList(List<SpecificationItem> newList) {
        this.specificationList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_specification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SpecificationItem item = specificationList.get(position);
        holder.keyTextView.setText(item.getKey());
        holder.valueTextView.setText(item.getValue());
    }

    @Override
    public int getItemCount() {
        return specificationList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView keyTextView;
        TextView valueTextView;

        ViewHolder(View itemView) {
            super(itemView);
            keyTextView = itemView.findViewById(R.id.specKey);
            valueTextView = itemView.findViewById(R.id.specValue);
        }
    }
}