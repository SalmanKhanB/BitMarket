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
        // Initialize views
        textViewUserName = findViewById(R.id.textViewUserName);
        textViewUserEmail = findViewById(R.id.textViewUserEmail);
        textViewUserPhone = findViewById(R.id.textViewUserPhone);
        textViewUserStatus = findViewById(R.id.textViewUserStatus);
        imageViewProfile = findViewById(R.id.imageViewProfile);

        String key = getIntent().getStringExtra("uid");
        if (!key.equals(FirebaseAuth.getInstance().getUid())){
            textViewUserStatus.setVisibility(View.GONE);
            findViewById(R.id.textViewStatus).setVisibility(View.GONE);
            findViewById(R.id.edit).setVisibility(View.GONE);
        }
        // Get user data (Assuming you have a Users object)
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Profiles").child(key);
         databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = new Users();

                if (snapshot.exists()){
                    user = snapshot.getValue(Users.class);
                    if (user != null) {
                        textViewUserName.setText(user.getName());
                        textViewUserEmail.setText(user.getEmail());
                        textViewUserPhone.setText(user.getPhone());
                        textViewUserStatus.setText(user.getStatus());

                        String profileImg = user.getProfileImage();
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

        // Display user data in TextViews

    }

    public void back(View view) {
        onBackPressed();
    }

    public void edit(View view) {
        startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
    }
}
