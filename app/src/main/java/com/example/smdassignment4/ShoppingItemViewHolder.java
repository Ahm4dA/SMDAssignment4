package com.example.smdassignment4;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ShoppingItemViewHolder extends RecyclerView.ViewHolder {

    public TextView itemNameTextView, quantityTextView, priceTextView;
    public Button deleteButton;

    public ShoppingItemViewHolder(@NonNull View itemView) {
        super(itemView);

        // Link UI components
        itemNameTextView = itemView.findViewById(R.id.itemNameTextView);
        quantityTextView = itemView.findViewById(R.id.quantityTextView);
        priceTextView = itemView.findViewById(R.id.priceTextView);
        deleteButton = itemView.findViewById(R.id.deleteButton);
    }

    public void bind(ShoppingItem item) {
        // Bind data to the UI components
        itemNameTextView.setText(item.getName());
        quantityTextView.setText(item.getQuantity() + " pcs");
        priceTextView.setText("$" + item.getPrice());
    }
}
