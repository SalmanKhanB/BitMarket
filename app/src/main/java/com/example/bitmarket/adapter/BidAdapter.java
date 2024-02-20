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

import com.example.bitmarket.ProductDetailsActivity;
import com.example.bitmarket.ProfileActivity;
import com.example.bitmarket.R;
import com.example.bitmarket.models.Bid;
import com.example.bitmarket.utils.AppConst;

import java.util.List;

public class BidAdapter extends RecyclerView.Adapter<BidAdapter.BidViewHolder> {
    private List<Bid> bidList;
    Context context;

    public BidAdapter(List<Bid> bidList, Context productDetailsActivity) {
        this.bidList = bidList;
        context = productDetailsActivity;
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
        LinearLayout layout;

        public BidViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewBidderName = itemView.findViewById(R.id.textViewBidderName);
            textViewBidValue = itemView.findViewById(R.id.textViewBidValue);
            layout = itemView.findViewById(R.id.ll);
        }

        public void bind(Bid bid) {
            // Bind bid data to the views
            textViewBidderName.setText("Bidder: " + bid.getUid());
            textViewBidValue.setText("Bid: " + bid.getBidValue());
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle the click event and show a toast message\
                    Intent intent = new Intent(context, ProfileActivity.class);
                    String data = bid.getUid();
                    intent.putExtra("uid",data);
                    context.startActivity(intent);

                }
            });

        }
    }
}
