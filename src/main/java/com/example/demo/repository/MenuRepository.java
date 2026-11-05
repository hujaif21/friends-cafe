package com.example.demo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.Menu;

public interface MenuRepository extends JpaRepository<Menu, Integer> {

    // Find items by category (Coffee, Beverages, Desserts)
    List<Menu> findByCategory(String category);
    List<Menu> findByNameContaining(String keyword);

}