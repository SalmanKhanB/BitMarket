package com.example.bitmarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.bitmarket.adapter.MyPagerAdapter;
import com.example.bitmarket.start.SignInActivity;
import com.example.bitmarket.utils.AppConst;
import com.example.bitmarket.utils.UserStatusManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class SellerActivity extends AppCompatActivity {
//    List<Product> listMainDatabase;
//    DatabaseReference databaseReference;
//    ProductAdapter productAdapter;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);
        ViewPager viewPager = findViewById(R.id.view_pager);
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        setTitle("Seller BitMarket");

        // Initialize the TabLayout and connect it with the ViewPager
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

//        listMainDatabase = new ArrayList<>();
//        databaseReference  = FirebaseDatabase.getInstance().getReference().child("Products");
//        RecyclerView recyclerViewVerticle = findViewById(R.id.rv);
//        productAdapter = new ProductAdapter(listMainDatabase, MyListActivity.this);
//        recyclerViewVerticle.setAdapter(productAdapter);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
//        recyclerViewVerticle.setLayoutManager(gridLayoutManager);
//        dataloading("");
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent(SellerActivity.this, SellProductActivity.class));
            }
        });


    }

//    private void dataloading(String all) {
//        if (all.equals(""))
//        {
//            databaseReference.addValueEventListener(new ValueEventListener() {
//                @SuppressLint("NotifyDataSetChanged")
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    listMainDatabase.clear();
//                    if (snapshot.exists()){
//                        for (DataSnapshot snapshot1: snapshot.getChildren()){
//                            try{
//                                String uid = snapshot1.child("uid").getValue().toString();
//                                System.out.println("uiddd: "+ uid+" : "+ AppConst.uid);
//                            if (AppConst.uid.equals(uid)){
//                                listMainDatabase.add(snapshot1.getValue(Product.class));
//
//                            }}catch (Exception ex){
//
//                            }
//                        }
//                        if (listMainDatabase.size()<1){
//                            Toast.makeText(MyListActivity.this, "Data Not Found", Toast.LENGTH_SHORT).show();
//                        }
//                        productAdapter.notifyDataSetChanged();
//                    }else {
//                        System.out.println("DATA1213221elsee"+snapshot.getKey());
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
//        }
////        else {
////
////            databaseReference.addValueEventListener(new ValueEventListener() {
////                @SuppressLint("NotifyDataSetChanged")
////                @Override
////                public void onDataChange(@NonNull DataSnapshot snapshot) {
////                    listMainDatabase.clear();
////                    if (snapshot.exists()){
////                        for (DataSnapshot snapshot1: snapshot.getChildren()){
////                            Product  product =snapshot1.getValue(Product.class);
////                            if (product.getProductCategory().equals(all)){
////                                listMainDatabase.add(product);
////                            }
////                        }
////                        productAdapter.notifyDataSetChanged();
////                    }else {
////                        Toast.makeText(MainActivity.this, "Not Found", Toast.LENGTH_SHORT).show();
////                    }
////                }
////
////                @Override
////                public void onCancelled(@NonNull DatabaseError error) {
////
////                }
////            });
////        }
//    }
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
    private void logout() {
        // Sign out the current user
        FirebaseAuth.getInstance().signOut();
        UserStatusManager.removeUserStatus(this);
        Toast.makeText(this, "SignOut Successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SellerActivity.this, SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
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