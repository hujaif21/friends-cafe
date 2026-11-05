package com.example.demo.repository;

import com.example.demo.model.Combo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComboRepository extends JpaRepository<Combo, Long> {
    
    // Spring Boot Magic: Automatically finds combos assigned to a specific day!
    List<Combo> findByAssignedDay(String assignedDay);
}