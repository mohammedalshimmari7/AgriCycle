package com.software.agricycle;

public class Listing {

    private String id;           // Firebase key
    private String wasteName;
    private String supplierName;

    private String type;

    private String status;

    private String buyer_id;
    private String supplier_id;

    private long offer,price;

    public Listing() {
        // Empty constructor for Firebase
    }

    public Listing(String id, String wasteName, String supplierName, String type, String status, String buyer_id, String supplier_id, long offer, long price) {
        this.id = id;
        this.wasteName = wasteName;
        this.supplierName = supplierName;
        this.type = type;
        this.status = status;
        this.buyer_id = buyer_id;
        this.supplier_id = supplier_id;
        this.offer = offer;
        this.price = price;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getOffer() {
        return offer;
    }

    public void setOffer(long offer) {
        this.offer = offer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBuyer_id() {
        return buyer_id;
    }

    public void setBuyer_id(String buyer_id) {
        this.buyer_id = buyer_id;
    }

    public String getSupplier_id() {
        return supplier_id;
    }

    public void setSupplier_id(String supplier_id) {
        this.supplier_id = supplier_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    // id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // wasteName
    public String getWasteName() {
        return wasteName;
    }

    public void setWasteName(String wasteName) {
        this.wasteName = wasteName;
    }

    // supplierName
    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }





}
