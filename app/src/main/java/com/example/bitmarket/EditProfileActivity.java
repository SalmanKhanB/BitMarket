package com.example.bitmarket;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

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

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextPhone;
    RadioGroup radioGroup;
    private Button saveButton;
    RadioButton radioSeller,radioBuyer;
    private FirebaseAuth auth;
    Users users;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        users = new Users();
        setTitle("Edit Profile");

        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        saveButton = findViewById(R.id.saveButton);
        radioGroup = findViewById(R.id.radioGroup);
        radioSeller = findViewById(R.id.radioSeller);
          radioBuyer = findViewById(R.id.radioBuyer);
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Profiles").child(currentUser.getUid());
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                         users = dataSnapshot.getValue(Users.class);
                        editTextName.setText(users.getName());
                        editTextEmail.setText(users.getEmail());
                        editTextPhone.setText(users.getPhone());
                        if (users.getStatus().equals("Buyer")){
                            radioBuyer.setChecked(true);
                            radioSeller.setChecked(false);
                        }else {
                            radioBuyer.setChecked(false);
                            radioSeller.setChecked(true);
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
                }else {

                    status = "Buyer";
                }

                if (isInputValid(newName, newPhone, newEmail, status)){
                    users.setName(newName);
                    users.setEmail(newEmail);
                    users.setPhone(newPhone);
                    users.setStatus(status);

                    updateProfileData( ); 
                }
            }
        });
    }
    private void updateProfileData() {
        // Get the current user's UID or a unique identifier
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Create a reference to the "Users" node in your database
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Profiles");

        // Create a reference to the specific user's data using their UID
        DatabaseReference userRef = usersRef.child(userId);

        // Update the user's data in the database
        userRef.updateChildren(users.toMap()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    System.out.println("DTATATATATT: "+ UserStatusManager.getUserStatus(EditProfileActivity.this)+"::"+ users.getStatus());
                    UserStatusManager.setUserStatus(EditProfileActivity.this, users.getStatus()); // Replace "buyer" with the actual status
                    System.out.println("DTATATATATT2: "+ UserStatusManager.getUserStatus(EditProfileActivity.this)+"::"+ users.getStatus());
                    Intent intent = new Intent(EditProfileActivity.this, SplashActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    // Failed to update data
                    Toast.makeText(EditProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
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

