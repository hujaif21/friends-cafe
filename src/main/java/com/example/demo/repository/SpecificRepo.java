package com.example.demo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.Specific_order;

public interface SpecificRepo extends JpaRepository<Specific_order, Long> {
    
    // 1. Used for your Promo Code "Wow Factor" API
    boolean existsByCustomerNameIgnoreCaseAndPromoCode(String customerName, String promoCode);

    // 2. THE FIX: This ensures your Admin List and User Tracking are ALWAYS in sync
    // It tells the database: "Give me everything, but keep them in order by their ID"
    List<Specific_order> findAllByOrderByIdAsc();
    List<Specific_order> findByCustomerNameContaining(String keyword);

}