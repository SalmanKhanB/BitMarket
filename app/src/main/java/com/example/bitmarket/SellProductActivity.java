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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

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
        private List<Uri> imageUris;
        private final Uri[] slotUris = new Uri[3];
        private int selectedSlot = 0;

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
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

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

            databaseReference = FirebaseDatabase.getInstance().getReference().child("Products");

            imageView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedSlot = 0;
                    openImagePicker();
                }
            });

            imageView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedSlot = 1;
                    openImagePicker();
                }
            });

            imageView3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedSlot = 2;
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

        private void dismissProgressDialog() {
            if (!isFinishing() && !isDestroyed() && progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

        private void saveProductDetails() {
            String productName = editTextProductName.getText().toString().trim();
            String productCategory = spinnerProductCategory.getSelectedItem() != null ? spinnerProductCategory.getSelectedItem().toString() : "";
            String productDescription = editTextProductDescription.getText().toString().trim();
            String brand = editTextBrand.getText().toString().trim();
            String startPrice = editTextStartPrice.getText().toString().trim();
            String bidEndTime = textViewBidEndTime.getText().toString().trim();

            // 1. Validate fields before showing progress dialog
            if (productName.isEmpty() || productDescription.isEmpty() || brand.isEmpty() || startPrice.isEmpty()
                    || bidEndTime.isEmpty() || bidEndTime.equalsIgnoreCase("Select bid end time")) {
                Toast.makeText(this, "Please fill in all fields including bid end time", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2. Gather selected images
            imageUris.clear();
            for (Uri uri : slotUris) {
                if (uri != null) {
                    imageUris.add(uri);
                }
            }

            if (imageUris.isEmpty()) {
                Toast.makeText(this, "Please select at least one image", Toast.LENGTH_SHORT).show();
                return;
            }

            String uid = FirebaseAuth.getInstance().getUid();
            if (uid == null || uid.isEmpty()) {
                uid = AppConst.getUid();
            }
            if (uid == null || uid.isEmpty()) {
                Toast.makeText(this, "User not authenticated. Please log in again.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 3. Show progress dialog only after passing validations
            progressDialog.show();

            // Create a new product object
            Product product = new Product();
            product.setProductName(productName);
            product.setProductCategory(productCategory);
            product.setProductDescription(productDescription);
            product.setBrand(brand);
            product.setStartPrice(startPrice);
            product.setBidEndTime(bidEndTime);
            product.setUid(uid);

            // Generate a new unique key for the product in the database
            String productId = databaseReference.push().getKey();
            if (productId == null) {
                dismissProgressDialog();
                Toast.makeText(this, "Failed to generate unique product key", Toast.LENGTH_SHORT).show();
                return;
            }
            product.setKey(productId);

            // Convert images to Base64 and save to Realtime Database directly (no Cloud Storage needed)
            saveProductWithBase64Images(productId, product);
        }

        private String uriToBase64(Uri uri) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                if (inputStream != null) {
                    inputStream.close();
                }
                if (bitmap == null) {
                    return null;
                }

                // Resize bitmap to max 800px dimension to keep database size optimal
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                int maxDim = 800;
                if (width > maxDim || height > maxDim) {
                    float ratio = Math.min((float) maxDim / width, (float) maxDim / height);
                    int newWidth = Math.round(ratio * width);
                    int newHeight = Math.round(ratio * height);
                    bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
                }

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream);
                byte[] bytes = outputStream.toByteArray();
                return Base64.encodeToString(bytes, Base64.NO_WRAP);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        private void saveProductWithBase64Images(final String productId, final Product product) {
            final List<Uri> urisToConvert = new ArrayList<>(imageUris);

            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    final List<String> base64Images = new ArrayList<>();
                    for (Uri uri : urisToConvert) {
                        String base64 = uriToBase64(uri);
                        if (base64 != null) {
                            base64Images.add(base64);
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (base64Images.isEmpty()) {
                                dismissProgressDialog();
                                Toast.makeText(SellProductActivity.this, "Failed to process selected images", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            product.setImageUrls(base64Images);

                            databaseReference.child(productId).setValue(product)
                                    .addOnCompleteListener(SellProductActivity.this, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> dbTask) {
                                            dismissProgressDialog();
                                            if (dbTask.isSuccessful()) {
                                                Toast.makeText(SellProductActivity.this, "Product details saved", Toast.LENGTH_SHORT).show();
                                                finish();
                                            } else {
                                                String errorMsg = dbTask.getException() != null ? dbTask.getException().getMessage() : "Failed to save product details";
                                                Toast.makeText(SellProductActivity.this, "Database Error: " + errorMsg, Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                    });
                }
            });
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

                    for (int i = 0; i < 3; i++) {
                        if (i < count) {
                            slotUris[i] = data.getClipData().getItemAt(i).getUri();
                        }
                    }

                    if (slotUris[0] != null) imageView1.setImageURI(slotUris[0]);
                    if (slotUris[1] != null) imageView2.setImageURI(slotUris[1]);
                    if (slotUris[2] != null) imageView3.setImageURI(slotUris[2]);
                } else if (data.getData() != null) {
                    Uri imageUri = data.getData();
                    slotUris[selectedSlot] = imageUri;

                    if (selectedSlot == 0) imageView1.setImageURI(imageUri);
                    else if (selectedSlot == 1) imageView2.setImageURI(imageUri);
                    else if (selectedSlot == 2) imageView3.setImageURI(imageUri);
                }
            }
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            // Selected category handled by spinner
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // Do nothing
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