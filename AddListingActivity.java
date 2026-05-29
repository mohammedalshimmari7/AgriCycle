package com.software.agricycle;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.software.agricycle.databinding.ActivityAddListingBinding;

public class AddListingActivity extends AppCompatActivity {

    private ActivityAddListingBinding binding;

    private FirebaseAuth auth;
    private DatabaseReference usersRef;
    private DatabaseReference listingRef;

    private String supplierId = "";
    private String supplierName = "-";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddListingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        supplierId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : "";

        if (TextUtils.isEmpty(supplierId)) {
            Toast.makeText(this, "Not logged in.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        usersRef = db.getReference("users").child(supplierId);
        listingRef = db.getReference("listing");

        // Load supplier name from /users/uid/fullName
        loadSupplierName();

        // Save listing
        binding.btnSaveListing.setOnClickListener(v -> saveListing());
    }

    private void loadSupplierName() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null && user.getFullName() != null) {
                    supplierName = user.getFullName();
                    binding.txtSupplierValue.setText(supplierName);
                } else {
                    supplierName = "-";
                    binding.txtSupplierValue.setText(supplierName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddListingActivity.this,
                        "Failed to load supplier info: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveListing() {
        // Read fields
        String wasteName = binding.edtWasteName.getText().toString().trim();
        String type = binding.edtType.getText().toString().trim();
        String priceStr = binding.edtPrice.getText().toString().trim();

        // Basic validation
        if (TextUtils.isEmpty(wasteName)) {
            binding.inputLayoutWasteName.setError("Waste name is required");
            return;
        } else {
            binding.inputLayoutWasteName.setError(null);
        }

        if (TextUtils.isEmpty(type)) {
            binding.inputLayoutType.setError("Type is required");
            return;
        } else {
            binding.inputLayoutType.setError(null);
        }

        if (TextUtils.isEmpty(priceStr)) {
            binding.inputLayoutPrice.setError("Price is required");
            return;
        } else {
            binding.inputLayoutPrice.setError(null);
        }

        int price;
        try {
            price = Integer.parseInt(priceStr);
        } catch (NumberFormatException e) {
            binding.inputLayoutPrice.setError("Price must be a number");
            return;
        }

        // Disable button to avoid double-click
        binding.btnSaveListing.setEnabled(false);

        // Generate random key under /listing
        String key = listingRef.push().getKey();
        if (key == null) {
            Toast.makeText(this, "Failed to generate listing key.", Toast.LENGTH_SHORT).show();
            binding.btnSaveListing.setEnabled(true);
            return;
        }

        // Build Listing object
        Listing listing = new Listing();
        listing.setId(key);
        listing.setWasteName(wasteName);
        listing.setSupplierName(supplierName);
        listing.setPrice(price);          // assuming price is int in Listing
        listing.setType(type);
        listing.setStatus("available");
        listing.setBuyer_id("");          // no buyer initially
        listing.setSupplier_id(supplierId);
        listing.setOffer(0);              // default offer 0

        // Save to Firebase
        listingRef.child(key)
                .setValue(listing)
                .addOnCompleteListener((Task<Void> task) -> {
                    binding.btnSaveListing.setEnabled(true);

                    if (task.isSuccessful()) {
                        Toast.makeText(AddListingActivity.this,
                                "Listing saved successfully.",
                                Toast.LENGTH_SHORT).show();
                        finish(); // go back to dashboard or list
                    } else {
                        Toast.makeText(AddListingActivity.this,
                                "Failed to save listing.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
