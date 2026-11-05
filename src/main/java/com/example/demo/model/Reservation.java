package com.example.demo.model;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reservation_id;

    private String customer_name;
    private String phone;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate reservation_date;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime reservation_time;
    private Integer guests;
    private String occasion;
    private String mood;
    private String special_request;
    private String status;
    
    
	public Integer getReservation_id() {
		return reservation_id;
	}
	public void setReservation_id(Integer reservation_id) {
		this.reservation_id = reservation_id;
	}
	public String getCustomer_name() {
		return customer_name;
	}
	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public LocalDate getReservation_date() {
		return reservation_date;
	}
	public void setReservation_date(LocalDate reservation_date) {
		this.reservation_date = reservation_date;
	}
	public LocalTime getReservation_time() {
		return reservation_time;
	}
	public void setReservation_time(LocalTime reservation_time) {
		this.reservation_time = reservation_time;
	}
	public Integer getGuests() {
		return guests;
	}
	public void setGuests(Integer guests) {
		this.guests = guests;
	}
	public String getOccasion() {
		return occasion;
	}
	public void setOccasion(String occasion) {
		this.occasion = occasion;
	}
	public String getMood() {
		return mood;
	}
	public void setMood(String mood) {
		this.mood = mood;
	}
	public String getSpecial_request() {
		return special_request;
	}
	public void setSpecial_request(String special_request) {
		this.special_request = special_request;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

    
    
}