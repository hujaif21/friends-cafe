package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.example.demo.model.User;
import com.example.demo.repository.CartRepository;
import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private CartRepository cartRepo;

    @ModelAttribute
    public void addCartCount(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user != null) {
            model.addAttribute("cartCount", cartRepo.countByUser(user));
        } else {
            model.addAttribute("cartCount", 0);
        }
    }
}