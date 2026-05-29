package com.software.agricycle;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.software.agricycle.databinding.ActivityViewWasteListBinding;
import com.software.agricycle.Listing;

import java.util.ArrayList;

public class ViewWasteListActivity extends AppCompatActivity {
    String role="buyer";
    String button="ViewWasteList";
    String uid="";
    private ActivityViewWasteListBinding binding;

    private DatabaseReference listingRef;
    private ValueEventListener listingListener;

    private final ArrayList<Listing> listingArray = new ArrayList<>();
    private ListingAdapter listingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewWasteListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        uid= getIntent().getStringExtra("uid");
        role= getIntent().getStringExtra("role");
        button= getIntent().getStringExtra("button");
        // Setup RecyclerView
        listingAdapter = new ListingAdapter(listingArray, this,uid,role,button);
        binding.recyclerListings.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerListings.setAdapter(listingAdapter);



        // Firebase: listing >> ID >> {wasteName, supplierName, price, type?}
        listingRef = FirebaseDatabase.getInstance().getReference().child("listing");
    }

    @Override
    protected void onResume() {
        super.onResume();
        attachListingListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        detachListingListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        detachListingListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detachListingListener();
    }

    private void attachListingListener() {
        if(button.equals("ViewWasteList")) {
            if (listingListener == null) {
                listingListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listingArray.clear();

                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Listing listing = snap.getValue(Listing.class);
                            if (listing != null && listing.getStatus().equals("available")) {
                                try {
                                    listing.setId(snap.getKey());
                                } catch (Exception ignored) {
                                }
                                listingArray.add(listing);
                            }
                        }

                        listingAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ViewWasteListActivity.this,
                                "Failed to load listings: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                };
            }

            listingRef.addValueEventListener(listingListener);
        }else if(button.equals("CheckOffers")) {
            binding.txtTitle.setText("Pending Offers");
            if (listingListener == null) {
                listingListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listingArray.clear();

                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Listing listing = snap.getValue(Listing.class);
                            if (listing != null && (listing.getStatus().equals("pending") && listing.getSupplier_id().equals(uid)) || listing.getStatus().equals("pending") && listing.getBuyer_id().equals(uid)) {
                                try {
                                    listing.setId(snap.getKey());
                                } catch (Exception ignored) {
                                }
                                listingArray.add(listing);
                            }
                        }

                        listingAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ViewWasteListActivity.this,
                                "Failed to load listings: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                };
            }

            listingRef.addValueEventListener(listingListener);
        }
        else if(button.equals("AcceptedOffers")) {
            binding.txtTitle.setText("Accepted Offers");
            if (listingListener == null) {
                listingListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listingArray.clear();

                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Listing listing = snap.getValue(Listing.class);
                            if (listing != null && (listing.getStatus().equals("sold") && listing.getSupplier_id().equals(uid)) || listing.getStatus().equals("sold") && listing.getBuyer_id().equals(uid)) {
                                try {
                                    listing.setId(snap.getKey());
                                } catch (Exception ignored) {
                                }
                                listingArray.add(listing);
                            }
                        }

                        listingAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ViewWasteListActivity.this,
                                "Failed to load listings: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                };
            }

            listingRef.addValueEventListener(listingListener);
        }
    }

    private void detachListingListener() {
        if (listingRef != null && listingListener != null) {
            listingRef.removeEventListener(listingListener);
        }
    }
}
