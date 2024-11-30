package com.example.smdassignment4;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ShoppingListActivity extends AppCompatActivity {

    private FloatingActionButton addItemFab;
    private RecyclerView shoppingRecyclerView;
    private DatabaseReference realtimeDatabaseRef;
    private FirebaseRecyclerAdapter<ShoppingItem, ShoppingItemViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shopping_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Realtime Database
        realtimeDatabaseRef = FirebaseDatabase.getInstance().getReference("shoppingItems");

        // Link UI components
        shoppingRecyclerView = findViewById(R.id.shoppingRecyclerView);
        addItemFab = findViewById(R.id.addItemFab);

        // Setup RecyclerView
        setupRecyclerView();

        // Add item FAB click listener
        addItemFab.setOnClickListener(v -> showAddItemDialog());
    }

    private void setupRecyclerView() {
        // Configure FirebaseRecyclerOptions
        FirebaseRecyclerOptions<ShoppingItem> options = new FirebaseRecyclerOptions.Builder<ShoppingItem>()
                .setQuery(realtimeDatabaseRef, ShoppingItem.class)
                .build();

        // Initialize FirebaseRecyclerAdapter
        adapter = new FirebaseRecyclerAdapter<ShoppingItem, ShoppingItemViewHolder>(options) {
            @NonNull
            @Override
            public ShoppingItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Inflate the layout for an individual item
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shopping, parent, false);
                return new ShoppingItemViewHolder(view); // Return your custom ViewHolder
            }

            @Override
            protected void onBindViewHolder(@NonNull ShoppingItemViewHolder holder, int position, @NonNull ShoppingItem model) {
                // Bind the data to the ViewHolder
                holder.bind(model);

                // Handle delete button click
                holder.deleteButton.setOnClickListener(v -> deleteItem(getRef(position).getKey()));
            }
        };

        // Set RecyclerView properties
        shoppingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        shoppingRecyclerView.setHasFixedSize(true);
        shoppingRecyclerView.setAdapter(adapter);
    }


    private void showAddItemDialog() {
        // Create a dialog for adding new items
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_item, null, false);
        AlertDialog.Builder addNote = new AlertDialog.Builder(this)
                .setTitle("Add New Shopping Item")
                .setView(dialogView);

        EditText etName = dialogView.findViewById(R.id.itemNameEditText_dialog);
        EditText etQuantity = dialogView.findViewById(R.id.quantityEditText_dialog);
        EditText etPrice = dialogView.findViewById(R.id.priceEditText_dialog);

        addNote.setPositiveButton("Add", (dialogInterface, i) -> {
            // Validate and add item
            String name = etName.getText().toString().trim();
            String quantityStr = etQuantity.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(quantityStr) || TextUtils.isEmpty(priceStr)) {
                Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
                return;
            }

            int quantity = Integer.parseInt(quantityStr);
            double price = Double.parseDouble(priceStr);

            addItemToDatabase(name, quantity, price);
        });

        addNote.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());

        addNote.show();
    }

    private void addItemToDatabase(String name, int quantity, double price) {
        // Generate a unique key
        String id = realtimeDatabaseRef.push().getKey();
        HashMap<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("name", name);
        data.put("quantity", quantity);
        data.put("price", price);

        // Push data to Firebase
        realtimeDatabaseRef.child(id)
                .setValue(data)
                .addOnSuccessListener(unused -> Toast.makeText(this, "Item added successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to add item: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void deleteItem(String key) {
        // Delete item from Firebase
        realtimeDatabaseRef.child(key)
                .removeValue()
                .addOnSuccessListener(unused -> Toast.makeText(this, "Item deleted successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete item: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
