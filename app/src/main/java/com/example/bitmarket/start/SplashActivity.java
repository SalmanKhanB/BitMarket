package com.example.bitmarket.start;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.example.bitmarket.BuyerActivity;
import com.example.bitmarket.R;
import com.example.bitmarket.SellerActivity;
import com.example.bitmarket.utils.UserStatusManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (user == null) {
                    Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    String status = UserStatusManager.getUserStatus(SplashActivity.this);
                    if (status != null && !status.isEmpty()) {
                        if ("Buyer".equalsIgnoreCase(status)) {
                            Intent intent = new Intent(SplashActivity.this, BuyerActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(SplashActivity.this, SellerActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                        finish();
                    } else {
                        // Query Firebase to get actual status
                        com.google.firebase.database.FirebaseDatabase.getInstance().getReference("Profiles")
                                .child(user.getUid()).child("status")
                                .addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                                    @Override
                                    public void onDataChange(com.google.firebase.database.DataSnapshot snapshot) {
                                        String fbStatus = snapshot.exists() && snapshot.getValue() != null ? snapshot.getValue().toString() : "Buyer";
                                        UserStatusManager.setUserStatus(SplashActivity.this, fbStatus);
                                        if ("Buyer".equalsIgnoreCase(fbStatus)) {
                                            Intent intent = new Intent(SplashActivity.this, BuyerActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        } else {
                                            Intent intent = new Intent(SplashActivity.this, SellerActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                        finish();
                                    }

                                    @Override
                                    public void onCancelled(com.google.firebase.database.DatabaseError error) {
                                        Intent intent = new Intent(SplashActivity.this, BuyerActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                    }
                }
            }
        }, SPLASH_TIME_OUT);

    }
}