package com.example.bitmarket;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bitmarket.start.SignInActivity;
import com.example.bitmarket.start.SplashActivity;
import com.example.bitmarket.utils.UserStatusManager;
import com.example.bitmarket.utils.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextPhone;
    RadioGroup radioGroup;
    private Button saveButton;
    RadioButton radioSeller,radioBuyer;
    private FirebaseAuth auth;
    Users users;
    private ImageView editProfileImageView;
    private Uri selectedImageUri = null;
    private static final int PICK_PROFILE_IMAGE = 101;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        users = new Users();
        setTitle("Edit Profile");

        editProfileImageView = findViewById(R.id.editProfileImageView);
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        saveButton = findViewById(R.id.saveButton);
        radioGroup = findViewById(R.id.radioGroup);
        radioSeller = findViewById(R.id.radioSeller);
        radioBuyer = findViewById(R.id.radioBuyer);
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Saving Profile");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        View.OnClickListener pickImageListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), PICK_PROFILE_IMAGE);
            }
        };

        if (editProfileImageView != null) {
            editProfileImageView.setOnClickListener(pickImageListener);
        }
        View card = findViewById(R.id.cardProfileImage);
        if (card != null) card.setOnClickListener(pickImageListener);
        View txt = findViewById(R.id.textViewChangePhoto);
        if (txt != null) txt.setOnClickListener(pickImageListener);

        if (currentUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Profiles").child(currentUser.getUid());
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        users = dataSnapshot.getValue(Users.class);
                        if (users != null) {
                            editTextName.setText(users.getName());
                            editTextEmail.setText(users.getEmail());
                            editTextPhone.setText(users.getPhone());
                            if ("Buyer".equals(users.getStatus())){
                                radioBuyer.setChecked(true);
                                radioSeller.setChecked(false);
                            } else {
                                radioBuyer.setChecked(false);
                                radioSeller.setChecked(true);
                            }

                            if (selectedImageUri == null && users.getProfileImage() != null && !users.getProfileImage().isEmpty()) {
                                String profileImg = users.getProfileImage();
                                if (profileImg.startsWith("http://") || profileImg.startsWith("https://")) {
                                    Glide.with(EditProfileActivity.this).load(profileImg).into(editProfileImageView);
                                } else {
                                    try {
                                        String clean = profileImg.contains(",") ? profileImg.substring(profileImg.indexOf(",") + 1) : profileImg;
                                        byte[] bytes = Base64.decode(clean, Base64.DEFAULT);
                                        Glide.with(EditProfileActivity.this).asBitmap().load(bytes).into(editProfileImageView);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle errors
                }
            });
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add code to save the updated profile data
                String newName = editTextName.getText().toString();
                String newPhone = editTextPhone.getText().toString();
                String newEmail = editTextEmail.getText().toString();
                String status = "";
                if (radioGroup.getCheckedRadioButtonId() == R.id.radioSeller){
                    status = "Seller";
                } else {
                    status = "Buyer";
                }

                if (isInputValid(newName, newPhone, newEmail, status)){
                    users.setName(newName);
                    users.setEmail(newEmail);
                    users.setPhone(newPhone);
                    users.setStatus(status);

                    progressDialog.show();
                    if (selectedImageUri != null) {
                        Executors.newSingleThreadExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                final String base64 = uriToBase64(selectedImageUri);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (base64 != null) {
                                            users.setProfileImage(base64);
                                        }
                                        updateProfileData();
                                    }
                                });
                            }
                        });
                    } else {
                        updateProfileData();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PROFILE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            editProfileImageView.setImageURI(selectedImageUri);
        }
    }

    private String uriToBase64(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (inputStream != null) {
                inputStream.close();
            }
            if (bitmap == null) return null;

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int maxDim = 400;
            if (width > maxDim || height > maxDim) {
                float ratio = Math.min((float) maxDim / width, (float) maxDim / height);
                int newWidth = Math.round(ratio * width);
                int newHeight = Math.round(ratio * height);
                bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
            byte[] bytes = outputStream.toByteArray();
            return Base64.encodeToString(bytes, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void updateProfileData() {
        // Get the current user's UID or a unique identifier
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            if (progressDialog.isShowing()) progressDialog.dismiss();
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Create a reference to the "Users" node in your database
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Profiles");

        // Create a reference to the specific user's data using their UID
        DatabaseReference userRef = usersRef.child(userId);

        // Update the user's data in the database
        userRef.updateChildren(users.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (progressDialog.isShowing()) progressDialog.dismiss();
                if (task.isSuccessful()) {
                    UserStatusManager.setUserStatus(EditProfileActivity.this, users.getStatus());
                    Intent intent = new Intent(EditProfileActivity.this, SplashActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    // Failed to update data
                    String errorMsg = task.getException() != null ? task.getException().getMessage() : "Failed to update profile";
                    Toast.makeText(EditProfileActivity.this, "Error: " + errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void onback(View view) {
        onBackPressed();
    }
    private boolean isInputValid(String name, String phone, String email, String status) {
        if (name.isEmpty()) {
            editTextName.setError("Name is required.");
            editTextName.requestFocus();
            return false;
        }

        if (phone.isEmpty()) {
            editTextPhone.setError("Phone is required.");
            editTextPhone.requestFocus();
            return false;
        }

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required.");
            editTextEmail.requestFocus();
            return false;
        }

        if (status.isEmpty()) {
            Toast.makeText(this, "Select the Status", Toast.LENGTH_SHORT).show();return false;
        }

        return true;
    }

}

