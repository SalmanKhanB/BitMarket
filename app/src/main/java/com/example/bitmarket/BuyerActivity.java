package com.example.bitmarket;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bitmarket.adapter.ProductAdapter;
import com.example.bitmarket.models.Product;
import com.example.bitmarket.start.SignInActivity;
import com.example.bitmarket.utils.AppConst;
import com.example.bitmarket.utils.UserStatusManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BuyerActivity extends AppCompatActivity {
    List<Product> listMainDatabase;
    DatabaseReference databaseReference;
    ProductAdapter productAdapter;
    EditText etSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         listMainDatabase = new ArrayList<>();
        setTitle("Buyer BitMarket");


        String[] originalCategories = getResources().getStringArray(R.array.product_categories);
        String[] categories = new String[originalCategories.length + 1];
        categories[0] = "All";

        for (int i = 0; i < originalCategories.length; i++) {
            categories[i + 1] = originalCategories[i];
        }

        RecyclerView recyclerView = findViewById(R.id.list_View);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        etSearch = findViewById(R.id.etSearch);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Filter the products based on the search input
                dataloading("All",s.toString());

            }
        });

        CategoryAdapter adapter = new CategoryAdapter(categories, this);
        recyclerView.setAdapter(adapter);


        databaseReference = FirebaseDatabase.getInstance().getReference("Products");

        RecyclerView recyclerViewVerticle = findViewById(R.id.rv_vertical);
        productAdapter = new ProductAdapter(listMainDatabase, BuyerActivity.this);
        recyclerViewVerticle.setAdapter(productAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        recyclerViewVerticle.setLayoutManager(gridLayoutManager);
        dataloading("All","");

    }


//            for (Product list: listMainDatabase ) {
//        if (list.getProductCategory().equals(all))
//            listTemp.add(list);
//    }

    private void dataloading(String all, String search) {
        if (all.equals("All")){
            databaseReference.addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    listMainDatabase.clear();
                     if (snapshot.exists()){
                        for (DataSnapshot snapshot1: snapshot.getChildren()){
                           try {
                               Product product = snapshot1.getValue(Product.class);
                               String dateString = product.getBidEndTime();
                               SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                               System.out.println("Date: "+ dateString);
                               Date date;
                               date = sdf.parse(dateString);
                                if (date != null) {
                                    LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                    LocalDate currentDate = LocalDate.now();

                                    if (!localDate.isBefore(currentDate)) {
                                        // Date is today or in the future, show a message
                                        if (product.getProductName().toLowerCase().contains(search)||product.getBrand().toLowerCase().contains(search)) {
                                            listMainDatabase.add(product);
                                        }
                                    } else {
                                        // Date is in the past
                                        // You can add your logic for handling past dates
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                        productAdapter.notifyDataSetChanged();
                    }else {
                        System.out.println("DATA1213221elsee"+snapshot.getKey());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    listMainDatabase.clear();
                    if (snapshot.exists()){
                        for (DataSnapshot snapshot1: snapshot.getChildren()){

                                try {
                                    Product  product =snapshot1.getValue(Product.class);
                                    if (product.getProductCategory().equals(all)){
                                        String dateString = product.getBidEndTime();
                                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                        Date date;
                                        date = sdf.parse(dateString);
                                    if (date != null) {
                                        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                        LocalDate currentDate = LocalDate.now();

                                        if (!localDate.isBefore(currentDate)) {
                                            // Date is today or in the future, show a message
                                            if (product.getProductName().toLowerCase().contains(search)||product.getBrand().toLowerCase().contains(search)) {
                                                listMainDatabase.add(product);
                                            }
                                        } else {
                                            // Date is in the past
                                            // You can add your logic for handling past dates
                                        }
                                    }
                                }} catch (Exception e) {
                                    e.printStackTrace();
                                }
                        }
                        productAdapter.notifyDataSetChanged();
                    }else {
                        Toast.makeText(BuyerActivity.this, "Not Found", Toast.LENGTH_SHORT).show();
                     }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
      }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_logout) {
            // Handle logout option
            showLogoutConfirmationDialog();

            return true;
        }
        else   if (id == R.id.menu_profile) {
             Intent intent = new Intent(this, ProfileActivity.class);
            String data = AppConst.uid;
            intent.putExtra("uid",data);
            startActivity(intent);
            return true;

        }
        else   if (id == R.id.cmplntBox) {
             Intent intent = new Intent(this, ComplaintActivity.class);
            startActivity(intent);
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        // Sign out the current user
        FirebaseAuth.getInstance().signOut();
        UserStatusManager.removeUserStatus(this);
        Toast.makeText(this, "SignOut Successfully", Toast.LENGTH_SHORT).show();
         Intent intent = new Intent(BuyerActivity.this, SignInActivity.class);
         intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
         startActivity(intent);
         finish();
    }


    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the logout action here, for example:
                logout();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

        private String[] categories;
        private Context context;
        private int selectedItem = 0; // Initially select the first item

        public CategoryAdapter(String[] categories, Context context) {
            this.categories = categories;
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_category, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            String category = categories[position];
            holder.textCategoryName.setText(category);

            if (position == selectedItem) {
                holder.textCategoryName.setBackgroundColor(ContextCompat.getColor(context, R.color.select_card));
            } else {
                holder.textCategoryName.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
            }

            holder.textCategoryName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int previousSelectedItem = selectedItem;
                    selectedItem = position;
                    notifyItemChanged(previousSelectedItem);
                    notifyItemChanged(selectedItem);
                    Toast.makeText(context, "Select"+ category, Toast.LENGTH_SHORT).show();
                    String seacrh=etSearch.getText().toString();
                    if (seacrh.equals("")|| seacrh.isEmpty()){
                        seacrh="";
                    }
                    dataloading(category,seacrh);
                }
            });
        }

        @Override
        public int getItemCount() {
            return categories.length;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView textCategoryName;

            public ViewHolder(View itemView) {
                super(itemView);
                textCategoryName = itemView.findViewById(R.id.textCategoryName);
            }
        }
    }
    @Override
    public void onBackPressed() {
        showExitConfirmationDialog();
    }

    private void showExitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Perform the exit action, e.g., finish the activity
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
