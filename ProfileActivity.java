package com.software.agricycle;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.software.agricycle.databinding.ActivityProfileBinding;

public class ProfileActivity extends AppCompatActivity {

    String role = "buyer";          // buyer or supplier
    String button = "ViewWasteList"; // from where this screen was opened
    String uid = "";               // uid of the user whose profile we show

    private ActivityProfileBinding binding;

    private DatabaseReference userRef;
    private ValueEventListener userListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Read intent extras
        uid = getIntent().getStringExtra("uid");
        role = getIntent().getStringExtra("role");
        button = getIntent().getStringExtra("button");



        if (uid == null || uid.isEmpty()) {
            Toast.makeText(this, "No user id provided.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (role == null) role = "buyer";
        if (button == null) button = "ViewWasteList";

        userRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid);

        // Optional: if you have a back arrow in XML (imgBack), you can enable this:
        // binding.imgBack.setOnClickListener(v -> finish());
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
    protected void onDestroy() {
        super.onDestroy();
        detachUserListener();
    }

    private void attachUserListener() {
        if (userRef == null) return;

        if (userListener == null) {
            userListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if (user == null) {
                        Toast.makeText(ProfileActivity.this,
                                "User data not found.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Fill common info: name, email, phone, role
                    fillCommonUserInfo(user);

                    // Case 1: supplier seeing their own profile (from Profile button) -> read-only rating
                    if ("supplier".equalsIgnoreCase(role) && "Profile".equals(button)) {
                        showRatingReadOnly(user);

                        // Case 2: buyer profile -> no rating UI
                    } else if ("buyer".equalsIgnoreCase(role) && !"Rate".equals(button)) {
                        binding.ratingBar.setVisibility(View.GONE);
                        binding.txtRatingLabel.setVisibility(View.GONE);
                        binding.txtRatingValue.setVisibility(View.GONE);
                        binding.btnSubmitRating.setVisibility(View.GONE);

                        // Case 3: visiting supplier profile (e.g. from listing) -> can rate
                    } else {
                        showRatingForRating(user);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ProfileActivity.this,
                            "Failed to load profile: " + error.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            };
        }

        userRef.addValueEventListener(userListener);
    }

    private void detachUserListener() {
        if (userRef != null && userListener != null) {
            userRef.removeEventListener(userListener);
        }
    }

    // ----------------- Helpers -----------------

    private void fillCommonUserInfo(User user) {
        // Name
        if (user.getFullName() != null && !user.getFullName().isEmpty()) {
            binding.txtNameValue.setText(user.getFullName());
        } else {
            binding.txtNameValue.setText("-");
        }

        // Email
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            binding.txtEmailValue.setText(user.getEmail());
        } else {
            binding.txtEmailValue.setText("-");
        }

        // Phone
        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            binding.txtPhoneValue.setText(user.getPhone());
        } else {
            binding.txtPhoneValue.setText("-");
        }

        // Role (capitalize first letter)
        String r = user.getRole();
        if (r != null && !r.isEmpty()) {
            String formattedRole =
                    r.substring(0, 1).toUpperCase() + r.substring(1).toLowerCase();
            binding.txtRoleValue.setText(formattedRole);
        } else {
            binding.txtRoleValue.setText("-");
        }
    }

    /**
     * Read-only rating (for supplier viewing themself).
     * Uses average = totalStars / numbers.
     */
    private void showRatingReadOnly(User user) {
        binding.ratingBar.setVisibility(View.VISIBLE);
        binding.txtRatingLabel.setVisibility(View.VISIBLE);
        binding.txtRatingValue.setVisibility(View.VISIBLE);
        binding.btnSubmitRating.setVisibility(View.GONE);

        binding.ratingBar.setIsIndicator(true);

        long totalStars = user.getStar();
        long count = user.getNumbers();

        if (count <= 0 || totalStars <= 0) {
            binding.ratingBar.setRating(0f);
            binding.txtRatingValue.setText("No rating yet");
            return;
        }

        double avg = (double) totalStars / count; // 0..5
        if (avg > 5.0) avg = 5.0;

        binding.ratingBar.setRating((float) avg);
        binding.txtRatingValue.setText(String.format("%.1f / 5", avg));
    }

    /**
     * Visiting a supplier profile from somewhere else (e.g. listing) and can rate.
     * RatingBar is interactive. When clicking Submit, we update star and numbers in Firebase.
     */
    private void showRatingForRating(User user) {
        binding.ratingBar.setVisibility(View.VISIBLE);
        binding.txtRatingLabel.setVisibility(View.VISIBLE);
        binding.txtRatingValue.setVisibility(View.VISIBLE);
        binding.btnSubmitRating.setVisibility(View.VISIBLE);

        binding.ratingBar.setIsIndicator(false); // user can change rating

        long totalStars = user.getStar();
        long count = user.getNumbers();

        // Show current average as starting rating (if any)
        if (count > 0 && totalStars > 0) {
            double avg = (double) totalStars / count;
            if (avg > 5.0) avg = 5.0;
            binding.ratingBar.setRating((float) avg);
            binding.txtRatingValue.setText(String.format("Current: %.1f / 5", avg));
        } else {
            binding.ratingBar.setRating(0f);
            binding.txtRatingValue.setText("No rating yet");
        }

        // Submit rating
        binding.btnSubmitRating.setEnabled(true);
        binding.btnSubmitRating.setText("Submit rating");

        binding.btnSubmitRating.setOnClickListener(v -> {
            float rating = binding.ratingBar.getRating(); // 0..5

            if (rating <= 0f) {
                Toast.makeText(ProfileActivity.this,
                        "Please select a rating first.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            long oldTotal = user.getStar();
            long oldCount = user.getNumbers();

            // You can change this logic if you want half-stars more precise.
            // Here we store total as integer sum of rounded ratings.
            long add = Math.round(rating);
            long newTotal = oldTotal + add;
            long newCount = oldCount + 1;

            // Update in Firebase
            userRef.child("star").setValue(newTotal);
            userRef.child("numbers").setValue(newCount)
                    .addOnCompleteListener((Task<Void> task) -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProfileActivity.this,
                                    "Thank you for your rating!",
                                    Toast.LENGTH_SHORT).show();

                            // Lock rating after submit
                            binding.ratingBar.setIsIndicator(true);
                            binding.btnSubmitRating.setEnabled(false);
                            binding.btnSubmitRating.setText("Rated");

                            // Optionally update the label with new avg
                            double avg = (double) newTotal / newCount;
                            if (avg > 5.0) avg = 5.0;
                            binding.txtRatingValue.setText(String.format("%.1f / 5", avg));

                        } else {
                            Toast.makeText(ProfileActivity.this,
                                    "Failed to submit rating.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
