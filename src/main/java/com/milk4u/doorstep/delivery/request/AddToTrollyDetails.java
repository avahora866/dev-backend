package com.milk4u.doorstep.delivery.request;

public class AddToTrollyDetails {
    private int cstId;
    private int prodId;
    private int qty;

    public int getCstId() {
        return cstId;
    }

    public void setCstId(int cstId) {
        this.cstId = cstId;
    }

    public int getProdId() {
        return prodId;
    }

    public void setProdId(int prodId) {
        this.prodId = prodId;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
