package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.CartItem;
import com.example.demo.model.Specific_order;
import com.example.demo.model.User;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.SpecificRepo;

import jakarta.servlet.http.HttpSession;

@Controller
public class SpecificOrderController {
    
    @Autowired
    private SpecificRepo repo;
    @Autowired
    private CartRepository cartRepo;
    
    @GetMapping("/specific")
    public String specific(
            @RequestParam(required = false, defaultValue = "Classic Espresso") String item,
            @RequestParam(required = false, defaultValue = "150.0") Double price, 
            @RequestParam(required = false, defaultValue = "https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=800") String image,
            Model model){
        
        model.addAttribute("s_orders", new Specific_order());
        
        // These are required so your Thymeleaf Javascript doesn't break!
        model.addAttribute("item", item);
        model.addAttribute("price", price);
        model.addAttribute("image", image);
        
        // Returns your exact HTML file name: specific-order.html
        return "specific-order"; 
    }
    

    @GetMapping("/orders")
    public String viewAll(@RequestParam(name = "keyword", required = false) String keyword, Model model) {
        List<Specific_order> allOrders;

        // 1. Fetch the orders based on search or full list
        if (keyword != null && !keyword.isEmpty()) {
            allOrders = repo.findByCustomerNameContaining(keyword);
        } else {
            allOrders = repo.findAll();
        }

        // 2. Create two lists to categorize the orders
        List<Specific_order> comboOrders = new ArrayList<>();
        List<Specific_order> singleOrders = new ArrayList<>();

        // 3. Separate them based on the [COMBO] prefix
        for (Specific_order order : allOrders) {
            if (order.getItemName() != null && order.getItemName().startsWith("[COMBO]")) {
                comboOrders.add(order);
            } else {
                singleOrders.add(order);
            }
        }

        // 4. Add both lists to the model
        model.addAttribute("comboOrders", comboOrders);
        model.addAttribute("singleOrders", singleOrders);
        model.addAttribute("keyword", keyword);

        return "specific_order-list";
    }
    
    @PostMapping("/specific/process")
    public String processOrder(@ModelAttribute Specific_order order, HttpSession session, RedirectAttributes redirectAttributes) {
        
        // 1. Saves to phpMyAdmin
        repo.save(order); 
        
        // 🔥 2. THE UPGRADED HACK: Clear ONLY the Menu Items! 🔥
        User user = (User) session.getAttribute("loggedUser");
        if (user != null) {
            List<CartItem> items = cartRepo.findByUser(user);
            for (CartItem item : items) {
                // If it is a regular coffee, delete it. (Leaves Combos untouched!)
                if (item.getMenu() != null) { 
                    cartRepo.delete(item);
                }
            }
        }
        
        // 3. This is the professional way! It hides the data inside the server session.
        redirectAttributes.addFlashAttribute("orderId", "FCB-" + order.getId());
        redirectAttributes.addFlashAttribute("customer", order.getCustomerName());
        redirectAttributes.addFlashAttribute("item", order.getItemName());
        redirectAttributes.addFlashAttribute("qty", order.getQuantity());
        redirectAttributes.addFlashAttribute("size", order.getSize());
        redirectAttributes.addFlashAttribute("payment", order.getPaymentMethod());
        redirectAttributes.addFlashAttribute("extras", order.getExtras());
        redirectAttributes.addFlashAttribute("inst", order.getSpecialNotes());
        redirectAttributes.addFlashAttribute("total", order.getTotalAmount());
        
        if (order.getItemName() != null && order.getItemName().startsWith("[COMBO]")) {
            return "redirect:/order/track?id=" + order.getId();
        }
        
        return "redirect:/confirm"; 
    }
    
    // 4. SHOW EDIT FORM
    @GetMapping("/specific/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("s_orders", repo.findById(id).orElse(null));
        return "specific_order-edit";
    }
    
    @PostMapping("/specific/update")
    public String updateOrder(@ModelAttribute Specific_order order) {
        repo.save(order); // Updates the database
        
        // Redirects back to the main orders list!
        return "redirect:/orders"; 
    }
    // 5. DELETE ORDER
    @GetMapping("/specific/delete/{id}")
    public String deleteOrder(@PathVariable Long id) {
        repo.deleteById(id);
        return "redirect:/orders";
    }

    // ========== WOW FACTOR: LIVE ORDER TRACKING ==========
    
    // 1. Shows the tracking page
    @GetMapping("/track")
    public String trackOrderPage() {
        return "track-order";
    }
    
    // 2. Processes the search and fetches from the database
 // Change @PostMapping to @RequestMapping so both Button and QR Code work!
 // ========== MASTERPIECE FEATURE: SEQUENTIAL TRACKING ==========
    // This method perfectly matches the 1, 2, 3... count on your admin table.
    
    @GetMapping("/track-order/search")
    public String searchOrder(@RequestParam("displayId") int displayId, Model model) {
        
        // 1. Fetch all orders from the database sorted by ID
        // This ensures the list is in the exact same order as your admin screen.
        List<Specific_order> allOrders = repo.findAllByOrderByIdAsc(); 

        // 2. Check if the "Display ID" typed by the user exists in the current list
        if (displayId > 0 && displayId <= allOrders.size()) {
            
            // 3. Subtract 1 because Java Lists start at 0, but your UI starts at 1
            Specific_order foundOrder = allOrders.get(displayId - 1);
            
            // 4. Pass the found order to your tracking status page
            model.addAttribute("order", foundOrder);
            return "track-order"; 
            
        } else {
            // 5. If the number is too high or doesn't exist, show an error
            return "redirect:/track?error=InvalidID";
        }
    }
 // ========== WOW FACTOR: PROMO CODE API ==========
    @GetMapping("/api/check-promo")
    @org.springframework.web.bind.annotation.ResponseBody
    public boolean checkPromo(@RequestParam String name, @RequestParam String code) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        // Returns TRUE if they already used it, FALSE if they haven't!
        return repo.existsByCustomerNameIgnoreCaseAndPromoCode(name.trim(), code.toUpperCase());
    }
 // ========== WOW FACTOR: UPDATE ORDER STATUS FROM ADMIN ==========
    @GetMapping("/specific/update-status/{id}")
    public String updateOrderStatus(@PathVariable Long id, @RequestParam("status") String newStatus) {
        Specific_order order = repo.findById(id).orElse(null);
        if (order != null) {
            order.setStatus(newStatus);
            repo.save(order); // Instantly updates phpMyAdmin!
        }
        return "redirect:/orders"; // Refreshes the admin page
    }
}