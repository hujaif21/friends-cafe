package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "spacific-order")
public class Specific_order {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;
    private String itemName;
    private Integer quantity;
    private String size;
    private String paymentMethod;
    private String extras;
    private String specialNotes;
    private Integer totalAmount;
    private String promoCode;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public String getExtras() {
		return extras;
	}
	public void setExtras(String extras) {
		this.extras = extras;
	}
	public String getSpecialNotes() {
		return specialNotes;
	}
	public void setSpecialNotes(String specialNotes) {
		this.specialNotes = specialNotes;
	}
	public Integer getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(Integer totalAmount) {
		this.totalAmount = totalAmount;
	}
	// Add this to Specific_order.java so we can track it!
    private String status = "Received"; 

    // Add these Getters and Setters at the bottom:
    public String getStatus() 
    {
    	return status; 
    	}
    public void setStatus(String status) 
    {
    	this.status = status; 
    	}

    // Add these Getters and Setters at the bottom
    public String getPromoCode() 
    { 
    	return promoCode; 
    }
    public void setPromoCode(String promoCode)
    {
    	this.promoCode = promoCode; 
    }

    
}
