package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // Links to your login User

    @ManyToOne
    @JoinColumn(name = "menu_id")
    private Menu menu; // Links to your existing Menu model
 // 🔥 ADD THIS NEW COMBO RELATION 🔥
    @ManyToOne
    @JoinColumn(name = "combo_id", nullable = true)
    private Combo combo;

    // Add the Getters and Setters for combo!
    public Combo getCombo() { return combo; }
    public void setCombo(Combo combo) { this.combo = combo; }

    private Integer quantity;

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Menu getMenu() { return menu; }
    public void setMenu(Menu menu) { this.menu = menu; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}