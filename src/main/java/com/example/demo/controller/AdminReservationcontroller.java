package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.model.Reservation;

@Controller
@RequestMapping("/admin")
public class AdminReservationcontroller {

    @Autowired
    private ReservationRepository reservationRepository;

    @GetMapping("/reservations")
    public String showReservations(String keyword, Model model) {
        if (keyword != null && !keyword.isEmpty()) {
            // Updated to use our new custom query method
            model.addAttribute("list", reservationRepository.searchByCustomerName(keyword));
        } else {
            // Default behavior: load all reservations
            model.addAttribute("list", reservationRepository.findAll());
        }
        return "admin/admin-reservations";
    }
	
    @GetMapping("/delete/{id}")
    public String deleteReservation(@PathVariable Integer id) {
        reservationRepository.deleteById(id);
        return "redirect:/admin/reservations";
    }
    @GetMapping("/edit/{id}")
    public String editReservation(@PathVariable Integer id, Model model) {
        Reservation reservation = reservationRepository.findById(id).orElse(null);
        model.addAttribute("reservation", reservation);
        return "admin/edit-reservation";
    }
    @PostMapping("/update")
    public String updateReservation(@ModelAttribute Reservation reservation) {
        reservationRepository.save(reservation);
        return "redirect:/admin/reservations";
    }
}