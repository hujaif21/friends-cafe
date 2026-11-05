package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

@Entity
public class Combo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long combo_id;

    private String name;           
    
    @Column(length = 500)
    private String description;    
    
    private double new_price;     
    private double old_price;     
    
    private String image_url;      
    
    private String category;       
    private String tag;         
    private String assignedDay;
	public Long getCombo_id() {
		return combo_id;
	}
	public void setCombo_id(Long combo_id) {
		this.combo_id = combo_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public double getNew_price() {
		return new_price;
	}
	public void setNew_price(double new_price) {
		this.new_price = new_price;
	}
	public double getOld_price() {
		return old_price;
	}
	public void setOld_price(double old_price) {
		this.old_price = old_price;
	}
	public String getImage_url() {
		return image_url;
	}
	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getAssignedDay() {
		return assignedDay;
	}
	public void setAssignedDay(String assignedDay) {
		this.assignedDay = assignedDay;
	}    

    // --- Add your standard Getters and Setters below ---
    
   
    // ... generate the rest of the getters/setters ...
}