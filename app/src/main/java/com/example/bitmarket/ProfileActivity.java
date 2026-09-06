package com.example.bitmarket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bitmarket.utils.AppConst;
import com.example.bitmarket.utils.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import android.widget.ImageView;
import android.util.Base64;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {


    private TextView textViewUserName;
    private TextView textViewUserEmail;
    private TextView textViewUserPhone;
    private TextView textViewUserStatus;
    private ImageView imageViewProfile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setTitle("My Profile");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        // Initialize views
        textViewUserName = findViewById(R.id.textViewUserName);
        textViewUserEmail = findViewById(R.id.textViewUserEmail);
        textViewUserPhone = findViewById(R.id.textViewUserPhone);
        textViewUserStatus = findViewById(R.id.textViewUserStatus);
        imageViewProfile = findViewById(R.id.imageViewProfile);

        String currentUid = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "";
        String key = getIntent().getStringExtra("uid");
        if (key == null || key.trim().isEmpty()) {
            key = currentUid;
        }

        boolean isSelf = !currentUid.isEmpty() && currentUid.equals(key);
        if (!isSelf) {
            textViewUserStatus.setVisibility(View.GONE);
            View tvStatus = findViewById(R.id.textViewStatus);
            if (tvStatus != null) tvStatus.setVisibility(View.GONE);
            View btnEdit = findViewById(R.id.edit);
            if (btnEdit != null) btnEdit.setVisibility(View.GONE);
        } else {
            textViewUserStatus.setVisibility(View.VISIBLE);
            View tvStatus = findViewById(R.id.textViewStatus);
            if (tvStatus != null) tvStatus.setVisibility(View.VISIBLE);
            View btnEdit = findViewById(R.id.edit);
            if (btnEdit != null) btnEdit.setVisibility(View.VISIBLE);
        }

        if (!key.isEmpty()) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Profiles").child(key);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Users user = snapshot.getValue(Users.class);
                        if (user != null) {
                            if (user.getName() != null && !user.getName().isEmpty()) {
                                textViewUserName.setText(user.getName());
                            } else if (snapshot.hasChild("name")) {
                                textViewUserName.setText(String.valueOf(snapshot.child("name").getValue()));
                            }

                            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                                textViewUserEmail.setText(user.getEmail());
                            } else if (snapshot.hasChild("email")) {
                                textViewUserEmail.setText(String.valueOf(snapshot.child("email").getValue()));
                            }

                            if (user.getPhone() != null && !user.getPhone().isEmpty()) {
                                textViewUserPhone.setText(user.getPhone());
                            } else if (snapshot.hasChild("phone")) {
                                textViewUserPhone.setText(String.valueOf(snapshot.child("phone").getValue()));
                            }

                            if (user.getStatus() != null && !user.getStatus().isEmpty()) {
                                textViewUserStatus.setText(user.getStatus());
                            } else if (snapshot.hasChild("status")) {
                                textViewUserStatus.setText(String.valueOf(snapshot.child("status").getValue()));
                            }

                            String profileImg = user.getProfileImage();
                            if ((profileImg == null || profileImg.isEmpty()) && snapshot.hasChild("profileImage")) {
                                profileImg = String.valueOf(snapshot.child("profileImage").getValue());
                            }
                            if (profileImg != null && !profileImg.isEmpty()) {
                                if (profileImg.startsWith("http://") || profileImg.startsWith("https://")) {
                                    Glide.with(ProfileActivity.this).load(profileImg).into(imageViewProfile);
                                } else {
                                    try {
                                        String clean = profileImg.contains(",") ? profileImg.substring(profileImg.indexOf(",") + 1) : profileImg;
                                        byte[] bytes = Base64.decode(clean, Base64.DEFAULT);
                                        Glide.with(ProfileActivity.this).asBitmap().load(bytes).into(imageViewProfile);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

        // Display user data in TextViews

    }

    public void back(View view) {
        onBackPressed();
    }

    public void edit(View view) {
        startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
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
