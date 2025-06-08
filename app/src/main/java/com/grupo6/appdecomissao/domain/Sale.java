package com.grupo6.appdecomissao.domain;

public class Sale {
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
}