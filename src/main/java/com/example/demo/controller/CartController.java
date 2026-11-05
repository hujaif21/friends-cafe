package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
public class CartController {

    @Autowired
    private CartRepository cartRepo;

    @Autowired
    private MenuRepository menuRepo;
    
    @Autowired
    private ComboRepository comboRepo;

    // ==========================================
    // 1. ADD ITEMS TO CART
    // ==========================================

    // ADD A LA CARTE MENU ITEM
    @PostMapping("/cart/add/{id}")
    public String addToCart(@PathVariable("id") Integer menuId, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");

        if (user == null) {
            return "redirect:/login?error=auth_required";
        }

        Menu item = menuRepo.findById(menuId).orElse(null);
        if (item != null) {
            CartItem cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setMenu(item);
            cartItem.setQuantity(1);
            cartRepo.save(cartItem);
        }

        return "redirect:/menu"; 
    }
    
    // ADD VIP COMBO ITEM
    @PostMapping("/cart/add-combo/{id}")
    public String addComboToCart(@PathVariable("id") Long comboId, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        
        if (user == null) {
            return "redirect:/login?error=auth_required";
        }

        Combo combo = comboRepo.findById(comboId).orElse(null);
        if (combo != null) {
            CartItem cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setCombo(combo); // We set the Combo instead of Menu
            cartItem.setQuantity(1);
            cartRepo.save(cartItem);
        }
        return "redirect:/combos"; 
    }

    // ==========================================
    // 2. VIEW SPLIT CART PAGE
    // ==========================================
    
    @GetMapping("/cart")
    public String viewCart(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/login";

        List<CartItem> allItems = cartRepo.findByUser(user);
        
        // Create completely separate lists for the UI tabs
        List<CartItem> menuItems = new ArrayList<>();
        List<CartItem> comboItems = new ArrayList<>();
        
        double menuTotal = 0;
        double comboTotal = 0;

        // Sort items and calculate separate totals safely
        for(CartItem item : allItems) {
            if (item.getMenu() != null) {
                menuItems.add(item);
                menuTotal += item.getMenu().getPrice() * item.getQuantity();
            } else if (item.getCombo() != null) {
                comboItems.add(item);
                comboTotal += item.getCombo().getNew_price() * item.getQuantity();
            }
        }

        // Send the perfectly separated data to the HTML
        model.addAttribute("menuItems", menuItems);
        model.addAttribute("comboItems", comboItems);
        model.addAttribute("menuTotal", menuTotal);
        model.addAttribute("comboTotal", comboTotal);
        
        return "cart-view"; 
    }

    // ==========================================
    // 3. THE SPLIT CHECKOUT ROUTERS
    // ==========================================

    // ROUTER FOR REGULAR MENU ITEMS ONLY
 // ROUTER FOR REGULAR MENU ITEMS ONLY
 // ROUTER FOR REGULAR MENU ITEMS ONLY
    @GetMapping("/cart/checkout-menu")
    public String checkoutMenu(HttpSession session) throws Exception {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/login";

        List<CartItem> menuItemsOnly = cartRepo.findByUser(user).stream()
                .filter(i -> i.getMenu() != null).toList();

        String finalName = (menuItemsOnly.size() == 1) 
            ? menuItemsOnly.get(0).getMenu().getName() 
            : "Boutique Selection (" + menuItemsOnly.size() + " Items)";
        
        // 🔥 NEW LOGIC: Pull up to 3 images
        List<String> imagePaths = menuItemsOnly.stream()
                .limit(3) // Only take first 3 so the URL doesn't get too long
                .map(i -> "images/" + i.getMenu().getImage_url())
                .toList();
        
        // Join them like: "images/coffee1.jpg,images/coffee2.jpg"
        String finalImageString = String.join(",", imagePaths);

        double total = menuItemsOnly.stream().mapToDouble(i -> i.getMenu().getPrice() * i.getQuantity()).sum();

        return "redirect:/specific?item=" + java.net.URLEncoder.encode(finalName, "UTF-8") 
                + "&price=" + total 
                + "&image=" + java.net.URLEncoder.encode(finalImageString, "UTF-8");
    }

    // ROUTER FOR VIP COMBOS ONLY
 // ROUTER FOR VIP COMBOS ONLY
 // ROUTER FOR VIP COMBOS ONLY
    @GetMapping("/cart/checkout-combo")
    public String checkoutCombo(HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/login";

        // Safely redirect to your custom Combo Checkout page, 
        // and tell it we are coming from the Cart!
        return "redirect:/checkout/combo?cart=true";
    }

    // ==========================================
    // 4. REMOVE ITEM FROM CART
    // ==========================================
    
    @GetMapping("/cart/remove/{id}")
    public String removeItem(@PathVariable Long id) {
        cartRepo.deleteById(id);
        return "redirect:/cart";
    }
}