package com.example.bitmarket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ComplaintActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);
        setTitle("Complaint Box");

        EditText complaint_txt = findViewById(R.id.complaint_txt);
        Button btn = findViewById(R.id.submitComplaint);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String body = complaint_txt.getText().toString();
                if(TextUtils.isEmpty(body)){
                    complaint_txt.setError("Required...");
                    complaint_txt.requestFocus();
                }else {
                    String to = "maroofafridi4u@gmail.com";
                    String subject = "Complaint_from_BitMarket_User";
                    String mailTo = "mailto:" + to +
                            "?&subject=" + Uri.encode(subject) +
                            "&body=" + Uri.encode(body);
                    Intent emailIntent = new Intent(Intent.ACTION_VIEW);
                    emailIntent.setData(Uri.parse(mailTo));
                    startActivity(emailIntent);
                    finish();

                }
            }
        });
    }
}