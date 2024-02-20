package com.example.bitmarket.utils;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AppConst {

    public static String uid = FirebaseAuth.getInstance().getUid();
    public static Users getUsersData(String uid){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Profiles").child(uid);
        final Users[] users = {new Users()};
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    users[0] = snapshot.getValue(Users.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Users users1 = users[0];
        return users1;

    }

}
