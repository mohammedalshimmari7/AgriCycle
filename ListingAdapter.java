package com.software.agricycle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.software.agricycle.R;
import com.software.agricycle.Listing;

import java.util.ArrayList;

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ViewHolder> {

    ArrayList<Listing> list;
    Context context;

    String  uid, role, button;

    public ListingAdapter(ArrayList<Listing> list, Context context,String uid,String role,String button) {
        this.list = list;
        this.context = context;
        this.uid=uid;
        this.role=role;
        this.button=button;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_listing, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListingAdapter.ViewHolder holder, int position) {

        Listing listing = list.get(position);

        // Set display values
        holder.txtWasteName.setText("Waste: " + listing.getWasteName());
        holder.txtSupplierName.setText("Supplier: " + listing.getSupplierName());
        holder.txtType.setText("Type: " + listing.getType());
        holder.txtPrice.setText("Price: " + String.valueOf(listing.getPrice()) + " AED");

        holder.imgWaste.setImageResource(R.drawable.type1);
        if (role.equals("buyer") && button.equals("ViewWasteList")) {
            // BUTTON: MAKE OFFER
            holder.btnMakeOffer.setOnClickListener(v -> {
                String enteredAmount = holder.edtAmount.getText().toString().trim();

                if (TextUtils.isEmpty(enteredAmount)) {
                    Toast.makeText(context, "Enter amount first", Toast.LENGTH_SHORT).show();
                    return;
                }



                FirebaseDatabase.getInstance()
                        .getReference("listing")
                        .child(listing.getId())
                        .child("buyer_id")
                        .setValue(uid)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseDatabase.getInstance()
                                        .getReference("listing")
                                        .child(listing.getId())
                                        .child("status")
                                        .setValue("pending")
                                        .addOnCompleteListener(task2 -> {
                                            if (task2.isSuccessful()) {
                                                FirebaseDatabase.getInstance()
                                                        .getReference("listing")
                                                        .child(listing.getId())
                                                        .child("offer")
                                                        .setValue(Integer.valueOf(holder.edtAmount.getText().toString()))
                                                        .addOnCompleteListener(task3 -> {
                                                            if (task3.isSuccessful()) {
                                                                Toast.makeText(context, "Offer submitted!", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        });
                            }
                        });
            });

            // BUTTON: BUY DIRECTLY
            holder.btnBuyDirect.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Confirm Purchase")
                        .setMessage("Buy this item directly for " + listing.getPrice() + " AED?")
                        .setPositiveButton("Yes", (dialog, which) -> {


                            FirebaseDatabase.getInstance()
                                    .getReference("listing")
                                    .child(listing.getId())
                                    .child("buyer_id")
                                    .setValue(uid)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            FirebaseDatabase.getInstance()
                                                    .getReference("listing")
                                                    .child(listing.getId())
                                                    .child("status")
                                                    .setValue("sold")
                                                    .addOnCompleteListener(task2 -> {
                                                        if (task2.isSuccessful()) {
                                                            FirebaseDatabase.getInstance()
                                                                    .getReference("listing")
                                                                    .child(listing.getId())
                                                                    .child("offer")
                                                                    .setValue(listing.getPrice())
                                                                    .addOnCompleteListener(task3 -> {
                                                                        if (task3.isSuccessful()) {
                                                                            Toast.makeText(context, "Offer submitted!", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                        }
                                                    });
                                        }
                                    });

                        })
                        .setNegativeButton("No", null)
                        .show();
            });


        }
        else if (role.equals("supplier") && button.equals("ViewWasteList")) {

            holder.btnBuyDirect.setVisibility(View.GONE);
            holder.btnMakeOffer.setVisibility(View.GONE);
            holder.edtAmount.setVisibility(View.GONE);
            holder.edtAmount.setFocusable(false);
            holder.edtAmount.setFocusableInTouchMode(false);
            holder.edtAmount.setCursorVisible(false);
            holder.edtAmount.setKeyListener(null);   // no keyboard input
        }else if (role.equals("supplier") && button.equals("CheckOffers")) {

            holder.btnBuyDirect.setText("Reject");
            holder.btnMakeOffer.setText("Accept");
            holder.edtAmount.setText("Offer: "+String.valueOf(listing.getOffer())+" AED");
            holder.edtAmount.setFocusable(false);
            holder.edtAmount.setFocusableInTouchMode(false);
            holder.edtAmount.setCursorVisible(false);
            holder.edtAmount.setKeyListener(null);   // no keyboard input


            holder.btnMakeOffer.setOnClickListener(v -> {
                String enteredAmount = holder.edtAmount.getText().toString().trim();

                if (TextUtils.isEmpty(enteredAmount)) {
                    Toast.makeText(context, "Enter amount first", Toast.LENGTH_SHORT).show();
                    return;
                }



                FirebaseDatabase.getInstance()
                        .getReference("listing")
                        .child(listing.getId())
                        .child("status")
                        .setValue("sold")
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Deal!", Toast.LENGTH_SHORT).show();
                            }
                        });
            });

            // BUTTON: BUY DIRECTLY
            holder.btnBuyDirect.setOnClickListener(v -> {



                            FirebaseDatabase.getInstance()
                                    .getReference("listing")
                                    .child(listing.getId())
                                    .child("buyer_id")
                                    .setValue("")
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            FirebaseDatabase.getInstance()
                                                    .getReference("listing")
                                                    .child(listing.getId())
                                                    .child("status")
                                                    .setValue("available")
                                                    .addOnCompleteListener(task2 -> {
                                                        if (task2.isSuccessful()) {
                                                            FirebaseDatabase.getInstance()
                                                                    .getReference("listing")
                                                                    .child(listing.getId())
                                                                    .child("offer")
                                                                    .setValue(0)
                                                                    .addOnCompleteListener(task3 -> {
                                                                        if (task3.isSuccessful()) {
                                                                            Toast.makeText(context, "Offer Rejected!", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                        }
                                                    });
                                        }
                                    });


            });

        }else if (role.equals("buyer") && button.equals("CheckOffers")) {

            holder.btnBuyDirect.setVisibility(View.GONE);
            holder.btnMakeOffer.setVisibility(View.GONE);
            holder.edtAmount.setText("Offer: "+String.valueOf(listing.getOffer())+" AED");
            holder.edtAmount.setFocusable(false);
            holder.edtAmount.setFocusableInTouchMode(false);
            holder.edtAmount.setCursorVisible(false);
            holder.edtAmount.setKeyListener(null);   // no keyboard input
        }else if (role.equals("supplier") && button.equals("AcceptedOffers")) {
            holder.txtPrice.setText("Deal: " + listing.getOffer() + " AED");
            holder.btnBuyDirect.setVisibility(View.GONE);
            holder.btnMakeOffer.setVisibility(View.GONE);
            holder.edtAmount.setVisibility(View.GONE);
        }else if (role.equals("buyer") && button.equals("AcceptedOffers")) {
            holder.txtPrice.setText("Deal: " + listing.getOffer() + " AED");
            holder.btnBuyDirect.setVisibility(View.GONE);
            holder.btnMakeOffer.setVisibility(View.GONE);
            holder.edtAmount.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra("role", role);   // pass "buyer" or "supplier"
                    intent.putExtra("button", "Rate");
                    intent.putExtra("uid", listing.getSupplier_id());
                    context.startActivity(intent);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgWaste;
        TextView txtWasteName, txtSupplierName, txtType, txtPrice;
        EditText edtAmount;
        Button btnMakeOffer, btnBuyDirect;
        LinearLayout box_main;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgWaste = itemView.findViewById(R.id.imgWaste);
            txtWasteName = itemView.findViewById(R.id.txtWasteName);
            txtSupplierName = itemView.findViewById(R.id.txtSupplierName);
            txtType = itemView.findViewById(R.id.txtType);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            edtAmount = itemView.findViewById(R.id.edtAmount);
            btnMakeOffer = itemView.findViewById(R.id.btnMakeOffer);
            btnBuyDirect = itemView.findViewById(R.id.btnBuyDirect);
            box_main = itemView.findViewById(R.id.box_main);
        }
    }
}
