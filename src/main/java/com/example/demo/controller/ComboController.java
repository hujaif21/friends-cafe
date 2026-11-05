package com.example.demo.controller;

import com.example.demo.model.CartItem;
import com.example.demo.model.Combo;
import com.example.demo.model.Specific_order;
import com.example.demo.model.User;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.ComboRepository;
import com.example.demo.repository.SpecificRepo; 
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
public class ComboController {

    private final SpecificOrderController specificOrderController;

    @Autowired
    private ComboRepository comboRepository;
    
    @Autowired 
    private SpecificRepo specificRepo;

    @Autowired
    private CartRepository cartRepo;

    public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/src/main/resources/static/images";

    ComboController(SpecificOrderController specificOrderController) {
        this.specificOrderController = specificOrderController;
    }

    /* =========================================================
       1. PUBLIC PAGE (localhost:8084/combos)
       ========================================================= */
    @GetMapping("/combos")
    public String showCombosPage(Model model) {
        String today = LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        
        List<Combo> todaysSpecials = comboRepository.findByAssignedDay(today);
        Combo featuredSpecial = todaysSpecials.isEmpty() ? null : todaysSpecials.get(0);

        // 🔥 FIX 1: Filter the catalog so it ONLY shows regular combos (Hides daily specials from grid)
        List<Combo> allCombos = comboRepository.findAll();
        List<Combo> regularCatalog = new ArrayList<>();
        for (Combo c : allCombos) {
            if (c.getAssignedDay() == null || c.getAssignedDay().equals("None") || c.getAssignedDay().isEmpty()) {
                regularCatalog.add(c);
            }
        }

        model.addAttribute("todaysSpecial", featuredSpecial);
        model.addAttribute("allCombos", regularCatalog); // Pass the filtered list!
        model.addAttribute("currentDay", today); 

        return "combo"; 
    }

    /* =========================================================
       2. ADMIN CRUD: VIEW ALL (Split into two lists)
       ========================================================= */
    @GetMapping("/admin/combos")
    public String viewCombos(Model model) {
        String today = LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        model.addAttribute("currentDay", today); 
        
        List<Combo> allCombos = comboRepository.findAll();
        
        List<Combo> specialCombos = new ArrayList<>();
        List<Combo> regularCombos = new ArrayList<>();
        
        for (Combo combo : allCombos) {
            if (combo.getAssignedDay() != null && !combo.getAssignedDay().equals("None") && !combo.getAssignedDay().isEmpty()) {
                specialCombos.add(combo);
            } else {
                regularCombos.add(combo);
            }
        }
        
        model.addAttribute("specialCombos", specialCombos);
        model.addAttribute("regularCombos", regularCombos);
        
        return "admin/admin_combos";   
    }

    /* =========================================================
       3. ADMIN CRUD: DELETE / EDIT / ADD
       ========================================================= */
    @GetMapping("/admin/combos/delete/{id}")
    public String deleteCombo(@PathVariable Long id) {
        comboRepository.deleteById(id);
        return "redirect:/admin/combos";
    }

    @GetMapping("/admin/combos/edit/{id}")
    public String editCombo(@PathVariable Long id, Model model) {
        Combo combo = comboRepository.findById(id).orElse(null);
        model.addAttribute("combo", combo);
        return "admin/edit_combo"; 
    }

    @GetMapping("/admin/combos/add")
    public String addCombo(Model model) {
        model.addAttribute("combo", new Combo());
        return "admin/edit_combo"; 
    }

    /* =========================================================
       4. ADMIN CRUD: UPDATE / SAVE (With Image Upload)
       ========================================================= */
    @PostMapping("/admin/combos/update")
    public String updateCombo(@ModelAttribute Combo combo, @RequestParam("imageFile") MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            File saveDir = new File(UPLOAD_DIRECTORY);
            if (!saveDir.exists()) saveDir.mkdirs();
            
            String originalFileName = file.getOriginalFilename();
            Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, originalFileName);
            Files.write(fileNameAndPath, file.getBytes());
            combo.setImage_url(originalFileName);
        } else if (combo.getCombo_id() != null) {
            Combo existingCombo = comboRepository.findById(combo.getCombo_id()).orElse(null);
            if (existingCombo != null) combo.setImage_url(existingCombo.getImage_url());
        }

        comboRepository.save(combo);
        return "redirect:/admin/combos";
    }

    /* =========================================================
       5. PREMIUM USER CHECKOUT FLOW
       ========================================================= */
    
    // 1. Load the Checkout Page
    @GetMapping("/checkout/combo")
    public String showComboCheckout(@RequestParam(value = "id", required = false) Long comboId, 
                                    @RequestParam(value = "cart", required = false) Boolean fromCart,
                                    HttpSession session, Model model) {
        
        // 🔥 FIX 2: GLOBAL SECURITY - User MUST be logged in to checkout!
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) {
            return "redirect:/login";
        }

        // IF COMING FROM THE CART
        if (Boolean.TRUE.equals(fromCart)) {
            List<CartItem> items = cartRepo.findByUser(user);
            double oldTotal = 0;
            double newTotal = 0;
            
            List<String> comboImages = new ArrayList<>();
            for (CartItem item : items) {
                if (item.getCombo() != null) {
                    oldTotal += item.getCombo().getOld_price() * item.getQuantity();
                    newTotal += item.getCombo().getNew_price() * item.getQuantity();
                    
                    String path = "images/" + item.getCombo().getImage_url();
                    if (!comboImages.contains(path)) {
                        comboImages.add(path);
                    }
                }
            }
            
            Combo cartBundle = new Combo();
            cartBundle.setName(comboImages.size() > 1 ? "VIP Master Bundle" : "VIP Selection");
            cartBundle.setDescription("Your exclusive collection of curated cafe pairings.");
            cartBundle.setImage_url(String.join(",", comboImages));
            cartBundle.setOld_price(Math.round(oldTotal * 100.0) / 100.0);
            cartBundle.setNew_price(Math.round(newTotal * 100.0) / 100.0);
            
            model.addAttribute("combo", cartBundle);
            return "combo_checkout"; 
        }
        
        if (comboId == null) {
            return "redirect:/combos"; 
        }

        // IF COMING DIRECTLY FROM THE "ORDER NOW" BUTTON
        Combo dbCombo = comboRepository.findById(comboId).orElse(null);
        
        if (dbCombo != null) {
            Combo displayCombo = new Combo();
            displayCombo.setCombo_id(dbCombo.getCombo_id());
            displayCombo.setName(dbCombo.getName());
            displayCombo.setDescription(dbCombo.getDescription());
            displayCombo.setOld_price(dbCombo.getOld_price());
            displayCombo.setNew_price(dbCombo.getNew_price());
            displayCombo.setImage_url("images/" + dbCombo.getImage_url());
            
            model.addAttribute("combo", displayCombo);
        }
        
        return "combo_checkout"; 
    }

    // 2. Process the Order into the "Specific Order" system
    @PostMapping("/checkout/combo/process")
    public String processComboOrder(@RequestParam("itemName") String itemName,
                                    @RequestParam("totalAmount") double totalAmount,
                                    @RequestParam("customerName") String customerName,
                                    @RequestParam("specialNotes") String specialNotes,
                                    HttpSession session) { 
        
        Specific_order newOrder = new Specific_order(); 
        newOrder.setItemName(itemName);            
        newOrder.setCustomerName(customerName);
        newOrder.setTotalAmount((int) totalAmount); 
        newOrder.setSpecialNotes("Location: " + specialNotes); 
        newOrder.setQuantity(1);
        newOrder.setSize("Combo Set");
        newOrder.setPaymentMethod("Pay at Counter");
        newOrder.setExtras("None");
        newOrder.setPromoCode("None");
        newOrder.setStatus("Received"); 
        
        Specific_order savedOrder = specificRepo.save(newOrder);
        
        if (itemName.contains("VIP Selection") || itemName.contains("VIP Master Bundle")) {
            User user = (User) session.getAttribute("loggedUser");
            if (user != null) {
                List<CartItem> items = cartRepo.findByUser(user);
                for (CartItem item : items) {
                    if (item.getCombo() != null) {
                        cartRepo.delete(item); 
                    }
                }
            }
        }
        
        return "redirect:/order/track?id=" + savedOrder.getId(); 
    }

    // 3. The Live Tracking Page (GET)
    @GetMapping("/order/track")
    public String trackOrder(@RequestParam("id") Long id, Model model) {
        Specific_order currentOrder = specificRepo.findById(id).orElse(null);
        model.addAttribute("order", currentOrder);
        
        if (currentOrder != null) {
            if ("Ready".equalsIgnoreCase(currentOrder.getStatus())) {
                model.addAttribute("waitTime", "Ready Now!");
            } else if ("Brewing".equalsIgnoreCase(currentOrder.getStatus())) {
                model.addAttribute("waitTime", "Approx. 5 Mins");
            } else {
                model.addAttribute("waitTime", "10 - 15 Mins");
            }
        }
        return "combo_success"; 
    }
}