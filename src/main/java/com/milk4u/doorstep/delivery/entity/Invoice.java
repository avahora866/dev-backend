package com.milk4u.doorstep.delivery.entity;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Invoice {
	//Fields
	@Id
	@Column(name="InvoiceID")
	private int invoiceId;
	
	@Column(name="CustomerID")
	private int customerId;
	
	@Column(name="TotalPrice")
	private double totalPrice;
	
	@Column(name="DriverID")
	private int driverId;
	
	@Column(name="DeliveryDate")
	private Date deliveryDate;
	
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

	public double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public int getDriverId() {
		return driverId;
	}

	public void setDriverId(int driverId) {
		this.driverId = driverId;
	}

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
	
	
}
