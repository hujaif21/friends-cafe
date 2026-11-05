package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.demo.repository.SpecificRepo;

@Controller
public class ConfirmController {

    @Autowired
    private SpecificRepo repo;

    @GetMapping("/confirm")
    public String confirm(Model model) {
        
        // 1. Security Check: If no orderId exists in Flash Attributes, they didn't order anything
        if (!model.containsAttribute("orderId")) {
            return "redirect:/menu"; 
        }

        // 2. THE MAM FIX: Calculate the sequential ID for the receipt
        // We count how many orders are currently in the database to give the "latest" number
        long sequentialId = repo.count();
        model.addAttribute("displayId", "FCB-" + sequentialId);

        return "confirm";
    }
}