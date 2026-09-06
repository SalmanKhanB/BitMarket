package com.example.bitmarket.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bitmarket.ProfileActivity;
import com.example.bitmarket.R;
import com.example.bitmarket.models.Bid;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BidAdapter extends RecyclerView.Adapter<BidAdapter.BidViewHolder> {
    private List<Bid> bidList;
    private Context context;
    private final Map<String, String> nameCache = new HashMap<>();

    public BidAdapter(List<Bid> bidList, Context productDetailsActivity) {
        this.bidList = bidList;
        this.context = productDetailsActivity;
    }

    @NonNull
    @Override
    public BidViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bid, parent, false);
        return new BidViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BidViewHolder holder, int position) {
        Bid bid = bidList.get(position);
        holder.bind(bid);
    }

    @Override
    public int getItemCount() {
        return bidList.size();
    }

    public class BidViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewBidderName;
        private TextView textViewBidValue;
        private View layout;

        public BidViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewBidderName = itemView.findViewById(R.id.textViewBidderName);
            textViewBidValue = itemView.findViewById(R.id.textViewBidValue);
            layout = itemView.findViewById(R.id.ll);
        }

        public void bind(Bid bid) {
            String uid = bid.getUid();
            textViewBidValue.setText(bid.getBidValue() + " Rs");

            if (uid == null || uid.trim().isEmpty()) {
                textViewBidderName.setText("Bidder: Anonymous");
            } else if (nameCache.containsKey(uid)) {
                textViewBidderName.setText("Bidder: " + nameCache.get(uid));
            } else {
                textViewBidderName.setText("Bidder: Loading...");
                textViewBidderName.setTag(uid);

                FirebaseDatabase.getInstance().getReference("Profiles")
                        .child(uid)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String name = null;
                                if (snapshot.exists()) {
                                    if (snapshot.hasChild("name") && snapshot.child("name").getValue() != null) {
                                        name = String.valueOf(snapshot.child("name").getValue()).trim();
                                    } else if (snapshot.hasChild("email") && snapshot.child("email").getValue() != null) {
                                        name = String.valueOf(snapshot.child("email").getValue()).trim();
                                    }
                                }

                                if (name == null || name.isEmpty()) {
                                    name = "User (" + (uid.length() > 6 ? uid.substring(0, 6) : uid) + ")";
                                }

                                nameCache.put(uid, name);

                                if (uid.equals(textViewBidderName.getTag())) {
                                    textViewBidderName.setText("Bidder: " + name);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                if (uid.equals(textViewBidderName.getTag())) {
                                    textViewBidderName.setText("Bidder: User");
                                }
                            }
                        });
            }

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra("uid", bid.getUid());
                    context.startActivity(intent);
                }
            });
        }
    }
}
