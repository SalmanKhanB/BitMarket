package com.example.bitmarket;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.bitmarket.models.Product;
import com.example.bitmarket.utils.AppConst;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SellProductActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

        private EditText editTextProductName;
        private Spinner spinnerProductCategory;
        private EditText editTextProductDescription;
        private EditText editTextBrand;
        private EditText editTextStartPrice;
        private TextView textViewBidEndTime;
        private ImageView imageView1;
        private ImageView imageView2;
        private ImageView imageView3;

        private DatabaseReference databaseReference;
        private StorageReference storageReference;
        private List<Uri> imageUris;

        private static final int PICK_IMAGE_REQUEST_CODE = 1;
    private ProgressDialog progressDialog;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_sell_product);
            setTitle("Sell your Product");

            editTextProductName = findViewById(R.id.editTextProductName);
            spinnerProductCategory = findViewById(R.id.spinnerProductCategory);
            editTextProductDescription = findViewById(R.id.editTextProductDescription);
            editTextBrand = findViewById(R.id.editTextBrand);
            editTextStartPrice = findViewById(R.id.editTextStartPrice);
            textViewBidEndTime = findViewById(R.id.editTextBidEndTime);
            imageView1 = findViewById(R.id.imageView1);
            imageView2 = findViewById(R.id.imageView2);
            imageView3 = findViewById(R.id.imageView3);
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Please Wait.");
            progressDialog.setMessage("Saving product details...");
            progressDialog.setCancelable(false);
            setTitle("Sell Product");

            // Spinner setup
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.product_categories, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerProductCategory.setAdapter(adapter);
            spinnerProductCategory.setOnItemSelectedListener(this);

            Button buttonSubmit = findViewById(R.id.buttonSubmit);
            buttonSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveProductDetails();
                }
            });

            imageUris = new ArrayList<>();

            storageReference = FirebaseStorage.getInstance().getReference();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Products");

            imageView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openImagePicker();
                }
            });

            imageView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openImagePicker();
                }
            });

            imageView3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openImagePicker();
                }
            });
        }

//        public void showTimePickerDialog(View v) {
//            final Calendar calendar = Calendar.getInstance();
//            int hour = calendar.get(Calendar.HOUR_OF_DAY);
//            int minute = calendar.get(Calendar.MINUTE);
//
//            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
//                    new TimePickerDialog.OnTimeSetListener() {
//                        @Override
//                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                            String formattedTime = String.format("%02d:%02d", hourOfDay, minute);
//                            textViewBidEndTime.setText(formattedTime);
//                        }
//                    }, hour, minute, false);
//
//            timePickerDialog.show();
//        }
public void showTimePickerDialog(View v) {
    final Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

    DatePickerDialog datePickerDialog = new DatePickerDialog(this,
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    // Check if the selected date is in the future
                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.set(year, monthOfYear, dayOfMonth);

                    if (selectedCalendar.after(calendar)) {
                        // Date is in the future, you can proceed
                        String formattedDate = String.format("%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year);
                        textViewBidEndTime.setText(formattedDate);
                    } else {
                        // Date is today or in the past, show an error message or handle it accordingly
                        // For example, display a Toast message
                        Toast.makeText(getApplicationContext(), "Please select a future date", Toast.LENGTH_SHORT).show();
                    }
                }
            }, year, month, dayOfMonth);

    datePickerDialog.show();
}

        private void saveProductDetails() {
            progressDialog.show();

            String productName = editTextProductName.getText().toString().trim();
            String productCategory = spinnerProductCategory.getSelectedItem().toString();
            String productDescription = editTextProductDescription.getText().toString().trim();
            String brand = editTextBrand.getText().toString().trim();
            String startPrice = editTextStartPrice.getText().toString().trim();
            String bidEndTime = textViewBidEndTime.getText().toString().trim();

            // Check for empty fields
            if (productName.isEmpty() || productDescription.isEmpty() || brand.isEmpty() || startPrice.isEmpty() || bidEndTime.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if at least one image is selected
            if (imageUris.isEmpty()) {
                Toast.makeText(this, "Please select at least one image", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a new product object
            Product product = new Product();
            product.setProductName(productName);
            product.setProductCategory(productCategory);
            product.setProductDescription(productDescription);
            product.setBrand(brand);
            product.setStartPrice(startPrice);
            product.setBidEndTime(bidEndTime);
            product.setUid(AppConst.uid);

            // Generate a new unique key for the product in the database
            String productId = databaseReference.push().getKey();
            product.setKey(productId);

            // Upload images to Firebase Storage
            uploadImages(productId, product);
        }

        private void uploadImages(final String productId, final Product product) {
            final int totalImages = imageUris.size();
            final List<String> downloadUrls = new ArrayList<>();

            for (int i = 0; i < totalImages; i++) {
                final StorageReference imageRef = storageReference.child("product_images").child(productId).child("image_" + i);
                UploadTask uploadTask = imageRef.putFile(imageUris.get(i));
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        return imageRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            if (downloadUri != null) {
                                downloadUrls.add(downloadUri.toString());

                                if (downloadUrls.size() == totalImages) {
                                    // All images uploaded successfully
                                    product.setImageUrls(downloadUrls);

                                    // Save the product object to the database using the generated key
                                    databaseReference.child(productId).setValue(product)
                                            .addOnCompleteListener(SellProductActivity.this, new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    progressDialog.dismiss();
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(SellProductActivity.this, "Product details saved", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    } else {
                                                        Toast.makeText(SellProductActivity.this, "Failed to save product details", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    }
                });
            }
        }

        private void openImagePicker() {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(Intent.createChooser(intent, "Select Images"), PICK_IMAGE_REQUEST_CODE);
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();

                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        imageUris.add(imageUri);

                        // Display selected images in the ImageView placeholders
                        if (i == 0) {
                            imageView1.setImageURI(imageUri);
                        } else if (i == 1) {
                            imageView2.setImageURI(imageUri);
                        } else if (i == 2) {
                            imageView3.setImageURI(imageUri);
                        }
                    }
                } else if (data.getData() != null) {
                    Uri imageUri = data.getData();
                    imageUris.add(imageUri);

                    // Display selected image in the first ImageView placeholder
                    imageView1.setImageURI(imageUri);
                }
            }
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String selectedCategory = parent.getItemAtPosition(position).toString();
            Toast.makeText(this, "Selected Category: " + selectedCategory, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // Do nothing
        }
    }