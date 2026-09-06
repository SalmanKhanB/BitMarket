package com.example.bitmarket;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.AnimationTypes;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.bitmarket.adapter.BidAdapter;
import com.example.bitmarket.models.Bid;
import com.example.bitmarket.models.Product;
import com.example.bitmarket.utils.AppConst;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import android.net.Uri;
import android.util.Base64;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProductDetailsActivity extends AppCompatActivity {
    Product product;
     private TextView productNameTextView;
    private TextView productCategoryTextView;
    private TextView productDescriptionTextView, brandName, endtime;
    private Button bidButton, deleteBid;
    private RecyclerView recyclerViewBids;
    private BidAdapter bidAdapter;
    private List<Bid> bidList;

     @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        setTitle("Product Details");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize views
         ImageSlider imageSlider = findViewById(R.id.image_slider);
        productNameTextView = findViewById(R.id.product_name);
        productCategoryTextView = findViewById(R.id.product_category);
        productDescriptionTextView = findViewById(R.id.product_description);
         bidButton = findViewById(R.id.bid_button);
         deleteBid = findViewById(R.id.bid_delete_button);
        endtime = findViewById(R.id.bidEndTime);
        brandName = findViewById(R.id.brandName);
         recyclerViewBids = findViewById(R.id.bids_rv);
         bidList = new ArrayList<>();
         bidAdapter = new BidAdapter(bidList, ProductDetailsActivity.this);
         recyclerViewBids.setAdapter(bidAdapter);
         recyclerViewBids.setLayoutManager(new LinearLayoutManager(this));
         // Get product details from intent
         product = getIntent().getParcelableExtra("product");

         // Fetch and display bid data
         fetchAndDisplayBids();

//         Display product details
        if (product != null) {
            System.out.println(product.toString());
            productNameTextView.setText(product.getProductName());
            productCategoryTextView.setText(product.getProductCategory());
            productDescriptionTextView.setText(product.getProductDescription());
            brandName.setText(product.getBrand());
            endtime.setText(product.getBidEndTime());

            try {
 //    Set properties programmatically
//    imageSlider.setAutoHandwritingEnabled(true);  // Enable auto-cycling of images
//    imageSlider.setPeriod(1000);      // Time interval between image changes in milliseconds
//    imageSlider.setDelay(1000);       // Delay before auto-cycling starts in milliseconds
//    imageSlider.setTextAlign(ImageSlider.TextAlign.CENTER);  // Text alignment for image descriptions
    imageSlider.setSlideAnimation(AnimationTypes.ZOOM_OUT);

// Set image URLs for the slider
    List<SlideModel> slideModels = new ArrayList<>();
    if (product.getImageUrls() != null) {
        int imgIndex = 0;
        File cacheDir = new File(getCacheDir(), "slides");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        for (String url : product.getImageUrls()) {
            if (url != null && !url.trim().isEmpty()) {
                String trimmed = url.trim();
                if (trimmed.startsWith("http://") || trimmed.startsWith("https://") || trimmed.startsWith("file://") || trimmed.startsWith("content://")) {
                    slideModels.add(new SlideModel(trimmed, product.getStartPrice() + " Rs", ScaleTypes.CENTER_INSIDE));
                } else {
                    try {
                        String cleanBase64 = trimmed.contains(",") ? trimmed.substring(trimmed.indexOf(",") + 1) : trimmed;
                        byte[] decoded = Base64.decode(cleanBase64, Base64.DEFAULT);
                        File tempFile = new File(cacheDir, "slide_" + (product.getKey() != null ? product.getKey() : "temp") + "_" + (imgIndex++) + ".jpg");
                        FileOutputStream fos = new FileOutputStream(tempFile);
                        fos.write(decoded);
                        fos.flush();
                        fos.close();
                        slideModels.add(new SlideModel(Uri.fromFile(tempFile).toString(), product.getStartPrice() + " Rs", ScaleTypes.CENTER_INSIDE));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

// Add the slides to the image slider
    if (!slideModels.isEmpty()) {
        imageSlider.setImageList(slideModels);
    }
}catch (Exception ex){
    Toast.makeText(this, "error"+ex.getMessage(), Toast.LENGTH_SHORT).show();
}


            deleteBid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products").child(product.getKey());
                    reference.setValue("");
                    reference.removeValue();
                    onBackPressed();
                }
            });

            try {
                String myUid = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "";
                String productOwnerUid = product != null && product.getUid() != null ? product.getUid() : "";
                if (!myUid.isEmpty() && myUid.equals(productOwnerUid)) {
                    bidButton.setVisibility(View.GONE);
                    deleteBid.setVisibility(View.VISIBLE);
                } else {
                    bidButton.setVisibility(View.VISIBLE);
                    deleteBid.setVisibility(View.GONE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            // Add click listener to bid button for implementing bidding logic
            bidButton.setOnClickListener(v -> {
                showBidBottomSheet();
                // Implement bidding logic here
                // You can start a bidding activity or show a dialog for placing bids
            });
        }
    }
    private void showBidBottomSheet() {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_place_bid, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheetView);
        EditText editBidAmount = bottomSheetView.findViewById(R.id.edit_bid_amount);
        Button buttonPlaceBid = bottomSheetView.findViewById(R.id.button_place_bid);
        String currentUid = AppConst.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Bids").child(product.getProductCategory()).child(product.getKey()).child(currentUid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    editBidAmount.setText(snapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        // Set a click listener for the "Place Bid" button inside the bottom sheet
        buttonPlaceBid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bidAmount = editBidAmount.getText().toString().trim();
                if (!bidAmount.isEmpty()) {
//                    int bidValue = Integer.parseInt(bidAmount);
                    // Assuming you have the user's bid value in the 'bidValue' variable

// Create a reference to the Firebase Realtime Database
                    DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();

// Define the path to store the bid
                    String category = product.getProductCategory();
                    String productKey = product.getKey();
                    String uid = AppConst.getUid();

// Construct the path to store the bid data
                    String bidPath = "Bids/" + category + "/" + productKey + "/" + uid;

// Set the bid value under the constructed path
                    firebaseDatabase.child(bidPath).setValue(bidAmount);

                    // You can now send the bidValue to your server or perform other actions
                    // For example, display a confirmation message
                    Toast.makeText(ProductDetailsActivity.this, "Bid placed: " + bidAmount, Toast.LENGTH_SHORT).show();

                    // Close the bottom sheet dialog
                    bottomSheetDialog.dismiss();
                } else {
                    Toast.makeText(ProductDetailsActivity.this, "Please enter a valid bid amount", Toast.LENGTH_SHORT).show();
                }
            }
        });
        bottomSheetDialog.show();
    }
    private void fetchAndDisplayBids() {
        // Assuming you have the DatabaseReference to the bid data
        System.out.println("DDDDDDDDDDDDDDDDDD"+ product.toString());
        DatabaseReference bidsRef = FirebaseDatabase.getInstance().getReference().child("Bids")
                .child(product.getProductCategory())
                .child(product.getKey());

        bidsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bidList.clear();
                for (DataSnapshot bidSnapshot : snapshot.getChildren()) {
                    Bid bid = new Bid(bidSnapshot.getKey(),bidSnapshot.getValue().toString());
                    bidList.add(bid);
                }
                sortBids(bidList);
                 bidAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
    public void sortBids(List<Bid> bidList) {
        // Define a custom Comparator to compare bids based on bidValue
        Comparator<Bid> bidComparator = new Comparator<Bid>() {
            @Override
            public int compare(Bid bid1, Bid bid2) {
                // Parse bidValue strings to integers for comparison
                int bidValue1 = Integer.parseInt(bid1.getBidValue());
                int bidValue2 = Integer.parseInt(bid2.getBidValue());

                // Compare based on bidValue
                return Integer.compare(bidValue1, bidValue2);
            }
        };

        // Use the custom Comparator to sort the bidList
        Collections.sort(bidList, bidComparator);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}