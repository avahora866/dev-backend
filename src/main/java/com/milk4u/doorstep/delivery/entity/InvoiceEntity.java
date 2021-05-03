package com.milk4u.doorstep.delivery.entity;

import javax.persistence.*;

@Entity
@Table(name = "invoice")
public class InvoiceEntity {
    //Fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="INVOICE_ID")
    private int invoiceId;

    @Column(name="CUSTOMER_ID")
    private int customerId;

    @Column(name="PRODUCT_ID")
    private int productId;

    @Column(name="QUANTITY")
    private int quantity;

    //Getters and Setters


    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
