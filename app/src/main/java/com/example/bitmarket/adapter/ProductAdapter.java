package com.example.bitmarket.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.bitmarket.ProductDetailsActivity;
import com.example.bitmarket.R;
import com.example.bitmarket.models.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private List<Product> productList;
    private Context context;

    public ProductAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout XML file
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the product at the current position
        Product product = productList.get(position);

        // Bind product data to views in your item layout.
        holder.productNameTextView.setText(product.getProductName());
        holder.productCategoryTextView.setText(product.getProductCategory());
        holder.price.setText(product.getStartPrice());

        // Load and display the first image using Glide
        if (!product.getImageUrls().isEmpty()) {
            String imageUrl = product.getImageUrls().get(0);
            Glide.with(context).load(imageUrl).into(holder.productImageView);
        }
        holder.itemView.setOnClickListener(view -> {
            Product product1 = productList.get(position);
            Intent intent = new Intent(context, ProductDetailsActivity.class);
            intent.putExtra("product", product1);
            context.startActivity(intent);
        });




        // Add more bindings for other views as needed
        // Example: holder.productDescriptionTextView.setText(product.getProductDescription());
        // ...

        // Set click listeners or other interactions here
        // Example: holder.itemView.setOnClickListener(new View.OnClickListener() {
        //     @Override
        //     public void onClick(View view) {
        //         // Handle item click
        //     }
        // });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Declare your item views here (e.g., TextViews, ImageViews).
        // Example:
         public TextView productNameTextView;
         public TextView productCategoryTextView,price;
         public ImageView productImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize your item views here.
            // Example:
             productNameTextView = itemView.findViewById(R.id.product_name);
             productCategoryTextView = itemView.findViewById(R.id.product_description);
            productImageView = itemView.findViewById(R.id.product_image);
            price = itemView.findViewById(R.id.product_price);
        }
    }
}
