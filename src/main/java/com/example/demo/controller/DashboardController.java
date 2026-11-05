package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class DashboardController {
	@GetMapping("/admin/dashboard")
    public String showDashboardPage() {
        // Looks for templates/dashboard.html
        return "admin/dashboard"; 
    }
}
