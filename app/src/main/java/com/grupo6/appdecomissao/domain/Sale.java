package com.grupo6.appdecomissao.domain;

import android.os.Parcel;
import android.os.Parcelable;

public class Sale implements Parcelable {
    private String id;
    private String consultantId;
    private String product;
    private double price;
    private String saleDate;
    private double commission;
    private String recordId;

    public Sale(String id, String consultantId, String product, double price, String saleDate, double commission, String recordId){
        this.id = id;
        this.consultantId = consultantId;
        this.product = product;
        this.price = price;
        this.saleDate = saleDate;
        this.commission = commission;
        this.recordId = recordId;
    }

    public String getId() { return id; }
    public String getConsultantId() { return consultantId; }
    public String getProduct() { return product; }
    public double getPrice() { return price; }
    public String getSaleDate() { return saleDate; }
    public double getCommission() { return commission; }
    public String getRecordId() { return recordId; }

    protected Sale(Parcel in) {
        id = in.readString();
        consultantId = in.readString();
        product = in.readString();
        price = in.readDouble();
        saleDate = in.readString();
        commission = in.readDouble();
        recordId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(consultantId);
        dest.writeString(product);
        dest.writeDouble(price);
        dest.writeString(saleDate);
        dest.writeDouble(commission);
        dest.writeString(recordId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Sale> CREATOR = new Creator<Sale>() {
        @Override
        public Sale createFromParcel(Parcel in) {
            return new Sale(in);
        }

        @Override
        public Sale[] newArray(int size) {
            return new Sale[size];
        }
    };
}