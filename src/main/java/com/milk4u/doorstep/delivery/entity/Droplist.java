package com.milk4u.doorstep.delivery.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Droplist {
	//Fields
	@Id
	@Column(name="DROPLIST_ID")
	private int droplistId;
	
	@Column(name="DRIVER_ID")
	private int driverId;
	
	@Column(name="CUSTOMER_ID")
	private int customerId;
	
	//Getters and Setters
	public int getDroplistId() {
		return droplistId;
	}

	public void setDroplistId(int droplistId) {
		this.droplistId = droplistId;
	}

	public int getDriverId() {
		return driverId;
	}

	public void setDriverId(int driverId) {
		this.driverId = driverId;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}
	
	
}
