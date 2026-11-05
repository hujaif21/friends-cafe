package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.order;
import com.example.demo.repository.OrderRepository;

@Controller
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    // ===============================
    // LOAD ORDER PAGE
    // ===============================
    @GetMapping("/order")
    public String showOrderPage() {
        return "order";
    }

    // ===============================
    // SAVE ORDER
    // ===============================
    @PostMapping("/order")
    public String saveOrder(
            @RequestParam String customerName,
            @RequestParam(required = false) String customerPhone,
            @RequestParam String coffeeType,
            @RequestParam String size,
            @RequestParam(required = false) List<String> extras,
            @RequestParam Integer quantity,
            @RequestParam(required = false) String specialNotes,
            @RequestParam(required = false) String promoCode,
            RedirectAttributes redirectAttributes
    ) {

        // ===== PRICE CALCULATION =====
        String[] coffeeParts = coffeeType.split("\\|");
        double coffeePrice = Double.parseDouble(coffeeParts[1]);

        String[] sizeParts = size.split("\\|");
        double sizePrice = Double.parseDouble(sizeParts[1]);

        double extrasTotal = 0;
        StringBuilder extrasList = new StringBuilder();

        if (extras != null) {
            for (String e : extras) {
                String[] parts = e.split("\\|");
                extrasTotal += Double.parseDouble(parts[1]);
                extrasList.append(parts[0]).append(", ");
            }
        }

        double total = (coffeePrice + sizePrice + extrasTotal) * quantity;

        // ===== PROMO DISCOUNT =====
        if ("FRIENDS50".equalsIgnoreCase(promoCode)) {
            total = total * 0.5;
        } else if ("FRIENDS20".equalsIgnoreCase(promoCode)) {
            total = total * 0.8;
        }

        // ===== SAVE TO DATABASE =====
        order order = new order();

        order.setCustomerName(customerName);
        order.setCustomerPhone(customerPhone);
        order.setCoffeeType(coffeeParts[0]);
        order.setSize(sizeParts[0]);
        order.setExtras(extrasList.toString());
        order.setQuantity(quantity);
        order.setTotalAmount(total);
        order.setSpecialNotes(specialNotes);
        order.setPromoCode(promoCode);
        order.setOrderTime(LocalDateTime.now());

        orderRepository.save(order);

        // ===== POPUP TRIGGER =====
        redirectAttributes.addFlashAttribute("success", true);
        redirectAttributes.addFlashAttribute("orderId", order.getOrder_id());

        return "redirect:/order";
    }
    
    @GetMapping("/admin/orders")
    public String viewOrders(Model model) {
        model.addAttribute("orders", orderRepository.findAll());
        return "admin/admin_orders";   // folder/file
    }

    @GetMapping("/admin/orders/delete/{id}")
    public String deleteOrder(@PathVariable Integer id) {
        orderRepository.deleteById(id);
        return "redirect:/admin/orders";
    }

    @GetMapping("/admin/orders/edit/{id}")
    public String editOrder(@PathVariable Integer id, Model model) {
        order order = orderRepository.findById(id).orElse(null);
        model.addAttribute("order", order);
        return "admin/edit_order";
    }

    @PostMapping("/admin/orders/update")
    public String updateOrder(@ModelAttribute order order) {
        orderRepository.save(order);
        return "redirect:/admin/orders";
    }
 /*   @PostMapping("/order/save")
    public String saveComboOrder(@ModelAttribute order newOrder) { // Note: I used lowercase 'order' to match your previous Mam code!
        
        // 1. Save the order to the database
        orderRepository.save(newOrder);
        
        // 2. Redirect the user back to the home page or combo page after successful order
        return "redirect:/combos"; 
    }*/

}