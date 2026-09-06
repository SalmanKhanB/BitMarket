package com.example.bitmarket.Fragments;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bitmarket.R;
import com.example.bitmarket.adapter.ProductAdapter;
import com.example.bitmarket.models.Product;
import com.example.bitmarket.utils.AppConst;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RunningFragment extends Fragment {
    List<Product> listMainDatabase;
    DatabaseReference databaseReference;
    ProductAdapter productAdapter;
    public RunningFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_running, container, false);
        listMainDatabase = new ArrayList<>();
        databaseReference  = FirebaseDatabase.getInstance().getReference().child("Products");
        RecyclerView recyclerViewVerticle = v.findViewById(R.id.rv);
        productAdapter = new ProductAdapter(listMainDatabase, getContext());
        recyclerViewVerticle.setAdapter(productAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2);
        recyclerViewVerticle.setLayoutManager(gridLayoutManager);
        dataloading("");
        return v;

    }

    private void dataloading(String all) {
        if (all.equals(""))
        {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String myUid = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser() != null ? com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid() : "";
                    if (snapshot.exists()) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            try {
                                String uid = snapshot1.child("uid").getValue() != null ? snapshot1.child("uid").getValue().toString() : "";
                                if (!myUid.isEmpty() && myUid.equals(uid)) {
                                    Product product = snapshot1.getValue(Product.class);
                                    if (product != null) {
                                        String dateString = product.getBidEndTime();
                                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                        Date date;

                                        try {
                                            date = sdf.parse(dateString);
                                            if (date != null) {
                                                LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                                LocalDate currentDate = LocalDate.now();

                                                if (!localDate.isBefore(currentDate)) {
                                                    listMainDatabase.add(product);
                                                }
                                            }
                                        } catch (ParseException e) {
                                            listMainDatabase.add(product);
                                        }
                                    }
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
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
//        else {
//
//            databaseReference.addValueEventListener(new ValueEventListener() {
//                @SuppressLint("NotifyDataSetChanged")
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    listMainDatabase.clear();
//                    if (snapshot.exists()){
//                        for (DataSnapshot snapshot1: snapshot.getChildren()){
//                            Product  product =snapshot1.getValue(Product.class);
//                            if (product.getProductCategory().equals(all)){
//                                listMainDatabase.add(product);
//                            }
//                        }
//                        productAdapter.notifyDataSetChanged();
//                    }else {
//                        Toast.makeText(MainActivity.this, "Not Found", Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
//        }
    }
}
