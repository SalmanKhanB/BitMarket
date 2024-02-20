package com.example.bitmarket.models;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class Product implements Parcelable {
    private String uid;
    private String key;
    private String productName;
    private String productCategory;
    private String productDescription;
    private String brand;
    private String startPrice;
    private String bidEndTime;
    private List<String> imageUrls;

    public Product() {
        // Empty constructor needed for Firebase
    }


    protected Product(Parcel in) {
        uid = in.readString();
        key = in.readString();
        productName = in.readString();
        productCategory = in.readString();
        productDescription = in.readString();
        brand = in.readString();
        startPrice = in.readString();
        bidEndTime = in.readString();
        imageUrls = in.createStringArrayList();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUid() {
        return uid;
    }

    public String getKey() {
        return key;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(String startPrice) {
        this.startPrice = startPrice;
    }

    public String getBidEndTime() {
        return bidEndTime;
    }

    public void setBidEndTime(String bidEndTime) {
        this.bidEndTime = bidEndTime;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(uid);
        parcel.writeString(key);
        parcel.writeString(productName);
        parcel.writeString(productCategory);
        parcel.writeString(productDescription);
        parcel.writeString(brand);
        parcel.writeString(startPrice);
        parcel.writeString(bidEndTime);
        parcel.writeStringList(imageUrls);
    }

    @Override
    public String toString() {
        return "Product{" +
                "uid='" + uid + '\'' +
                ", key='" + key + '\'' +
                ", productName='" + productName + '\'' +
                ", productCategory='" + productCategory + '\'' +
                ", productDescription='" + productDescription + '\'' +
                ", brand='" + brand + '\'' +
                ", startPrice='" + startPrice + '\'' +
                ", bidEndTime='" + bidEndTime + '\'' +
                ", imageUrls=" + imageUrls +
                '}';
    }
}
