package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller
public class LoginController {

    @Autowired
    private UserRepository repo;

    @Autowired
    private BCryptPasswordEncoder passEncoder;

    // ==========================================
    // 1. REGISTRATION LOGIC (Mam's Automatic Setup)
    // ==========================================
    @GetMapping("/")
    public String showRegister(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user) {
        // Encrypt the password before saving it to MySQL
        user.setPassword(passEncoder.encode(user.getPassword()));
        
        // 🌟 MAM'S LOGIC: Check how many people are in the database! 🌟
        if (repo.count() == 0) {
            user.setRole("ADMIN");
        } else {
            user.setRole("USER");
        }
        
        // Everyone starts with 0 logins
        user.setLoginCount(0);
        
        // 🔥 NEW: Make sure new users are active! 🔥
        user.setActive(true);
        
        // Save the brand new user to the database!
        repo.save(user);
        
        return "redirect:/login"; 
    }
    // ==========================================
    // 2. LOGIN LOGIC (DB Roles & Login Count Tracker)
    // ==========================================
    @GetMapping("/login")
    public String showLogin(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }
    @PostMapping("/login")
    public String processLogin(@ModelAttribute("user") User user, Model model, HttpSession session) {
        
        String typedUsername = user.getUsername();
        String typedPassword = user.getPassword();

        // 1. Find the user in the database
        User dbUser = repo.findByUsername(typedUsername);

        // 2. Check if they exist AND if the typed password matches the database
        if (dbUser == null || !passEncoder.matches(typedPassword, dbUser.getPassword())) {
            model.addAttribute("error", "Invalid username or password");
            model.addAttribute("user", user); // Keep their typed info
            return "login"; // Reload the page to show the error
        }

        // 🔥 3. NEW SECURITY CHECK: Are they banned? 🔥
        if (!dbUser.isActive()) {
            model.addAttribute("error", "Your account has been deactivated/banned. Please contact the cafe.");
            model.addAttribute("user", user);
            return "login"; // Kick them back to the login page!
        }

        // 4. SUCCESS! Update their login count (Teacher's Requirement)
        dbUser.setLoginCount(dbUser.getLoginCount() + 1);
        repo.save(dbUser); // Save the new count to MySQL

        // 5. Save data to the session so the HTML Navigation Bar can see it
        session.setAttribute("loggedUser", dbUser); 
        session.setAttribute("currentUsername", dbUser.getUsername());
        session.setAttribute("role", dbUser.getRole());

        // 6. Traffic Controller: Send Admin and User to different places
        if ("ADMIN".equals(dbUser.getRole())) {
            return "redirect:/admin/dashboard"; 
        } else {
            return "redirect:/home"; 
        }
    }
 // Unban / Reactivate a user
    @GetMapping("/unbanUser/{id}")
    public String unbanUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) { 
        
        // 1. Find the user
        User user = repo.findById(id).orElse(null);
        
        if (user != null) {
            // 2. Turn their account back on!
            user.setActive(true); 
            repo.save(user); // Save to database
            
            // Optional: Send a nice success message
            redirectAttributes.addFlashAttribute("errorMessage", "User account has been successfully restored!");
        }
        
        return "redirect:/loginData";
    }
    // Quick logout route
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Destroy the session memory
        return "redirect:/login";
    }
 // ==========================================
    // USER CRUD LOGIC
    // ==========================================
    
    @GetMapping("/loginData")
    public String loginData(String keyword, Model model) {
        List<User> listUsers;
        if (keyword != null && !keyword.isEmpty()) {
            // Searches the database for matching usernames
            listUsers = repo.findByUsernameContaining(keyword);
        } else {
            listUsers = repo.findAll();
        }
        
        model.addAttribute("data", listUsers);
        return "login_list";
    }

    @GetMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) { 
        
        // 1. Find the user
        User user = repo.findById(id).orElse(null);
        
        if (user != null) {
            // 2. Security Check: Are they the Admin?
            if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Security Alert: You cannot deactivate the Master Admin account!");
                return "redirect:/loginData";
            }

            // 3. If they are a normal user, deactivate them!
            user.setActive(false); 
            repo.save(user); // Save the updated status to the database
        }
        
        return "redirect:/loginData";
    }
    // Show edit form
    @GetMapping("/editUser/{id}")
    public String editUser(@PathVariable("id") Long id, Model model) { // ✨ Changed int to Long here
        // .orElse(null) handles the Optional returned by findById
        model.addAttribute("user", repo.findById(id).orElse(null)); 
        return "edit_login";
    }

    // Process update
    @PostMapping("/updateUser")
    public String updateUser(@ModelAttribute("user") User user) {
        // Note: If you want to re-hash the password on update, 
        // you would do user.setPassword(passEncoder.encode(user.getPassword())); here.
        repo.save(user);
        return "redirect:/loginData";
    }
}