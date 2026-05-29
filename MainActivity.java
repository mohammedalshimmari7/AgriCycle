package com.software.agricycle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.software.agricycle.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    // ViewBinding for activity_main.xml
    private ActivityMainBinding binding;

    // Firebase
    private FirebaseAuth auth;
    private DatabaseReference userRef;
    private ValueEventListener userListener;

    private String uid;
    private String role = "buyer";   // "buyer" or "supplier"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        // If user not logged in, go back to SignActivity
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, SignActivity.class));
            finish();
            return;
        }

        // Reference to current user in Realtime Database
        uid = auth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("users")
                .child(uid);

        setupClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        attachUserListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        detachUserListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        detachUserListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detachUserListener();
    }

    /**
     * Attach a ValueEventListener to load user info and update dashboard UI.
     */
    private void attachUserListener() {
        if (userRef == null) return;

        if (userListener == null) {
            userListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);

                    if (user == null) {
                        Toast.makeText(MainActivity.this,
                                "User data not found.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Welcome text
                    if (user.getFullName() != null) {
                        binding.txtWelcome.setText("Welcome, " + user.getFullName());
                    } else {
                        binding.txtWelcome.setText("Welcome");
                    }

                    // Role-based UI
                    role = user.getRole() != null ? user.getRole().toLowerCase() : "buyer";


                    if (role.equals("supplier")) {
                        binding.txtRoleInfo.setText("You are signed in as Supplier");

                        // Supplier can add listings, doesn't see Rate Service
                        binding.cardAddListing.setVisibility(View.VISIBLE);

                    } else if (role.equals("buyer")) {
                        binding.txtRoleInfo.setText("You are signed in as Buyer");

                        // Buyer cannot add listings
                        binding.cardAddListing.setVisibility(View.GONE);

                    } else {
                        binding.txtRoleInfo.setText("Role: " + role);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this,
                            "Failed to load user: " + error.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            };
        }

        userRef.addValueEventListener(userListener);
    }

    /**
     * Detach the ValueEventListener to avoid memory leaks.
     */
    private void detachUserListener() {
        if (userRef != null && userListener != null) {
            userRef.removeEventListener(userListener);
        }
    }

    /**
     * Setup click listeners for all the dashboard cards.
     */
    private void setupClickListeners() {

        // View Waste List
        binding.cardViewList.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ViewWasteListActivity.class);
            intent.putExtra("role", role);   // pass "buyer" or "supplier"
            intent.putExtra("button", "ViewWasteList");
            intent.putExtra("uid", uid);
            startActivity(intent);
        });

        // Check Offers
        binding.cardOffers.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ViewWasteListActivity.class);
            intent.putExtra("role", role);   // pass "buyer" or "supplier"
            intent.putExtra("button", "CheckOffers");
            intent.putExtra("uid", uid);
            startActivity(intent);
        });

        // Accepted Offers
        binding.cardAcceptedOffers.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ViewWasteListActivity.class);
            intent.putExtra("role", role);   // pass "buyer" or "supplier"
            intent.putExtra("button", "AcceptedOffers");
            intent.putExtra("uid", uid);
            startActivity(intent);
        });

        // Add Waste Listing (Supplier only in UI logic)
        binding.cardAddListing.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddListingActivity.class);
            startActivity(intent);
        });

        // Profile
        binding.cardProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            intent.putExtra("role", role);   // pass "buyer" or "supplier"
            intent.putExtra("button", "Profile");
            intent.putExtra("uid", uid);
            startActivity(intent);
        });

        // Sign out
        binding.cardSignOut.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(MainActivity.this, SignActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
