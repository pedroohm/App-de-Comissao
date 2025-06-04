package com.grupo6.appdecomissao;

public class Sale {
    private Integer id;
    private Integer consultantId;
    private String product;
    private double price;
    private String saleDate;
    private double commission;
    private Integer recordId;

    public Sale(Integer id, Integer consultantId, String product, double price, String saleDate, double commission, Integer recordId){
        this.id = id;
        this.consultantId = consultantId;
        this.product = product;
        this.price = price;
        this.saleDate = saleDate;
        this.commission = commission;
        this.recordId = recordId;
    }

    public Integer getId() { return id; }
    public Integer getConsultantId() { return consultantId; }
    public String getProduct() { return product; }
    public double getPrice() { return price; }
    public String getSaleDate() { return saleDate; }
    public double getCommission() { return commission; }
    public Integer getRecordId() { return recordId; }
}