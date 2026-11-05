package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table; // Make sure to import this!

@Entity
@Table(name = "cafe_users") // 🌟 CHANGE THIS to whatever table name you want! 🌟
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String password;

    // 🌟 MAM'S REQUIREMENTS: Role & Login Counter 🌟
    private String role; 
    private int loginCount = 0; 
    private boolean isActive = true;

    // Empty Constructor (Required by Spring Boot)
    public User() {}

    // --- Standard Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    // --- NEW Getters & Setters ---
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public int getLoginCount() { return loginCount; }
    public void setLoginCount(int loginCount) { this.loginCount = loginCount; }

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
    
}