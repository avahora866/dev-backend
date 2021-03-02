package com.milk4u.doorstep.delivery.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "trolly")
public class TrollyEntity {
	//Fields
	@Id
	@Column(name="TROLLY_ID")
	private int trollyId;
	
	@Column(name="CUSTOMER_ID")
	private int customerId;
	
	@Column(name="PRODUCT_ID")
	private int productId;
	
	@Column(name="QUANTITY")
	private int quantity;
	
	//Getters and Setters
	public int getTrollyId() {
		return trollyId;
	}

	public void setTrollyId(int trollyId) {
		this.trollyId = trollyId;
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
