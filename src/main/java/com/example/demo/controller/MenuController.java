package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.repository.MenuRepository;

@Controller
public class MenuController {

    @Autowired
    private MenuRepository menuRepository;

    @GetMapping("/menu")
    public String menu(Model model) {
        
        // Fetch items from the database based on the exact category names from your admin form
        model.addAttribute("coffeeList", menuRepository.findByCategory("Coffee"));
        model.addAttribute("beverageList", menuRepository.findByCategory("Beverages"));
        model.addAttribute("dessertList", menuRepository.findByCategory("Desserts"));

        return "menu"; // returns menu.html
    }
}