package com.example.bitmarket.start;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bitmarket.BuyerActivity;
import com.example.bitmarket.R;
import com.example.bitmarket.SellerActivity;
import com.example.bitmarket.utils.UserStatusManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.ProcessingInstruction;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private EditText name_txt,email_txt,phone_txt,password_txt,conformPas_txt;
    private Button submitSigUp;
    private TextView signIn_txt;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        progressDialog  = new ProgressDialog(this);
        progressDialog.setTitle("Sign Up");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        setTitle("Sign Up");


        name_txt = findViewById(R.id.txt_name);
        email_txt = findViewById(R.id.txt_email);
        phone_txt = findViewById(R.id.txt_mobile);
        password_txt = findViewById(R.id.txt_password);
        conformPas_txt = findViewById(R.id.txt_conform_password);
        submitSigUp = findViewById(R.id.sign_up_btn);
        signIn_txt = findViewById(R.id.click_sign_in);

        submitSigUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = name_txt.getText().toString();
                String email = email_txt.getText().toString();
                String phone = phone_txt.getText().toString();
                String password = password_txt.getText().toString();
                String conPassword = conformPas_txt.getText().toString();
                RadioGroup radioGroup = findViewById(R.id.radioGroup);
                RadioButton radioSeller = findViewById(R.id.radioSeller);
                RadioButton radioBuyer = findViewById(R.id.radioBuyer);


                if (TextUtils.isEmpty(name)){
                    name_txt.requestFocus();
                    name_txt.setError("Required...");
                }else if(TextUtils.isEmpty(email)){
                    email_txt.requestFocus();
                    email_txt.setError("Required...");
                }else if(TextUtils.isEmpty(phone)){
                    phone_txt.requestFocus();
                    phone_txt.setError("Required...");
                }else if(TextUtils.isEmpty(password)){
                    password_txt.requestFocus();
                    password_txt.setError("Required...");
                }else if(TextUtils.isEmpty(conPassword)){
                    conformPas_txt.requestFocus();
                    conformPas_txt.setError("Required...");
                }else if(!password.equals(conPassword)){
                    conformPas_txt.requestFocus();
                    conformPas_txt.setError("Not matched...");
                }else{
                    String status = "";
                    if (radioGroup.getCheckedRadioButtonId() == R.id.radioSeller){

                        status = "Seller";
                    }else {

                        status = "Buyer";
                    }
                    progressDialog.show();
                    signupFunction(name,email,phone,password,status);
                    Toast.makeText(SignUpActivity.this, "Sign Up Successfully", Toast.LENGTH_SHORT).show();

                }
            }
        });

        signIn_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
            }
        });
    }
    private void signupFunction(final String name, String email, String phone, String password, String status)
    {


        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            //Getting a user with Unique key
                            FirebaseAuth auth = FirebaseAuth.getInstance();
                            String key = auth.getCurrentUser().getUid();

                            //To save the user data with unique key
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                                    .getReference().child("Profiles").child(key);

                            Map<String,Object> map = new HashMap<>();
                            map.put("name",name);
                            map.put("phone",phone);
                            map.put("password",password);
                            map.put("email",email);
                            map.put("status",status);

                            databaseReference.setValue(map);
                            Toast.makeText(SignUpActivity.this, "Successfully Added...", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            progressDialog.cancel();
                            if (status.equals("Buyer")){
                                UserStatusManager.setUserStatus(SignUpActivity.this, "Buyer"); // Replace "buyer" with the actual status

                                Intent intent =  new Intent(SignUpActivity.this, BuyerActivity.class);
                                startActivity(intent);

                            }else{
                                UserStatusManager.setUserStatus(SignUpActivity.this, "Seller");
                                Intent intent =  new Intent(SignUpActivity.this, SellerActivity.class);
                                startActivity(intent);
                            }
                            finish();
                        }else{
                            Toast.makeText(SignUpActivity.this, "Error.."+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            progressDialog.cancel();
                        }
                    }
                });
    }


}
