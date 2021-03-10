package com.milk4u.doorstep.delivery.entity;

import javax.persistence.*;

@Entity
@Table(name = "current_order")
public class CurrentOrderEntity {
	//Fields
	@Id
	@Column(name="ORDER_ID")
	private int orderId;

	@Column(name="CUSTOMER_ID")
	private int customerId;

	@Column(name="PRODUCT_ID")
	private int productId;
	
	@Column(name="QUANTITY")
	private int quantity;

	//Getters and Setters

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
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
