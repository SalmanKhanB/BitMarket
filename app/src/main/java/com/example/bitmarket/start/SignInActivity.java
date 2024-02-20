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
import com.example.bitmarket.EditProfileActivity;
import com.example.bitmarket.R;
import com.example.bitmarket.SellerActivity;
import com.example.bitmarket.utils.UserStatusManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private TextView signUP_txt;
    private EditText email_txt,password_txt;
    private Button submitSigIn;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        setTitle("SignIn");

        progressDialog  = new ProgressDialog(this);
        progressDialog.setTitle("Sign Up");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        signUP_txt = findViewById(R.id.click_sign_up);
        email_txt = findViewById(R.id.txt_email_signIn);
        password_txt = findViewById(R.id.txt_password_sign_in);
        submitSigIn = findViewById(R.id.sign_In_btn);
        auth = FirebaseAuth.getInstance();
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        RadioButton radioSeller = findViewById(R.id.radioSeller);
        RadioButton radioBuyer = findViewById(R.id.radioBuyer);

        submitSigIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_txt.getText().toString();
                String password = password_txt.getText().toString();
                if (TextUtils.isEmpty(email)){
                    email_txt.requestFocus();
                    email_txt.setError("Required...");
                }else if(TextUtils.isEmpty(password)){
                    password_txt.requestFocus();
                    password_txt.setError("Required...");
                }else{
                    progressDialog.show();
                    String status = "";
                    if (radioGroup.getCheckedRadioButtonId() == R.id.radioSeller){

                        status = "Seller";
                    }else {

                        status = "Buyer";
                    }
                    signInFunc(email,password,status);
                }
            }
        });

        signUP_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this,SignUpActivity.class));

            }
        });
    }

    private void signInFunc(String email, String password, String status){


        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SignInActivity.this, "Sign In Successfully..", Toast.LENGTH_SHORT).show();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Profiles").child(task.getResult().getUser().getUid()).child("status");
                    reference.setValue(status);
                    progressDialog.dismiss();
                    progressDialog.cancel();
                    if (status.equals("Buyer")){
                        UserStatusManager.setUserStatus(SignInActivity.this, "Buyer"); // Replace "buyer" with the actual status

                        Intent intent =  new Intent(SignInActivity.this, BuyerActivity.class);
                        startActivity(intent);

                    }else{
                        UserStatusManager.setUserStatus(SignInActivity.this, "Seller");
                        Intent intent =  new Intent(SignInActivity.this, SellerActivity.class);
                        startActivity(intent);
                    }
//                    intent.putExtra("admin","user");
                    finish();
                }else{
                    progressDialog.dismiss();
                    progressDialog.cancel();
                    Toast.makeText(SignInActivity.this,"Error..\n"+task.getException().getMessage() , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void gotoForget(View view) {
        Intent intent = new Intent(SignInActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);

    }
}