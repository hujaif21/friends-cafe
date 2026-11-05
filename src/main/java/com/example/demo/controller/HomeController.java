package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;
import com.example.demo.model.User;

@Controller
public class HomeController { // 🌟 YOU WERE MISSING THIS LINE! 🌟

    @GetMapping("/home")
    public String showHomePage(HttpSession session, Model model) {
        
        // Check if someone is logged in
        User loggedUser = (User) session.getAttribute("loggedUser");
        
        if (loggedUser != null) {
            // If they are logged in, send their username to the HTML!
            model.addAttribute("currentUsername", loggedUser.getUsername());
        }
        
        return "home";
    }

} // 🌟 AND YOU NEEDED THIS CLOSING BRACKET 🌟