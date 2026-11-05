package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.Reservation;
import com.example.demo.repository.ReservationRepository;

@Controller
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;

    // SaaS Configuration: How many tables can be booked for the exact same time slot?
    private final int MAX_TABLES_PER_SLOT = 1;

    // Show reservation page
    @GetMapping("/reservation")
    public String showReservationForm(Model model) {
        // Creates the empty object so Thymeleaf doesn't crash
        model.addAttribute("reservation", new Reservation());
        return "reserve-table";
    }

    // THE ONLY SAVE METHOD (Merged and Cleaned)
    @PostMapping("/saveReservation")
    public String saveReservation(@ModelAttribute Reservation reservation) {

        // 1. Check capacity in the database
        long currentBookings = reservationRepository.countActiveReservations(
                reservation.getReservation_date(), 
                reservation.getReservation_time()
        );

        String outcome = "";
        
        // 2. Decision Engine
        if (currentBookings < MAX_TABLES_PER_SLOT) {
            reservation.setStatus("Confirmed"); 
            outcome = "available";
        } else {
            reservation.setStatus("Pending"); 
            outcome = "waiting";
        }

        // 3. Save to database FIRST (so it is included in the total count)
        reservationRepository.save(reservation);

        // 4. THE REVIEW 22 FIX: Count total reservations to get the Sequential ID!
        // This guarantees the number matches the 'iterStat' on your Admin Dashboard.
        long sequentialId = reservationRepository.count();

        // 5. Redirect with the SEQUENTIAL ID attached to the URL instead of the database ID!
        return "redirect:/reserve-status"
                + "?id=" + sequentialId 
                + "&name=" + reservation.getCustomer_name()
                + "&date=" + reservation.getReservation_date()
                + "&time=" + reservation.getReservation_time()
                + "&guests=" + reservation.getGuests()
                + "&occasion=" + reservation.getOccasion()
                + "&mood=" + reservation.getMood()
                + "&outcome=" + outcome; 
    }

    // Show status page
    @GetMapping("/reserve-status")
    public String showStatusPage() {
        return "reserve-status";
    }

    // ========== LIVE TRACKING FEATURES ==========

    // 1. Show the Tracking Page
    @GetMapping("/track-table")
    public String trackTablePage() {
        return "track-table";
    }

    // ========== MASTERPIECE FEATURE: SEQUENTIAL TABLE TRACKING ==========
    
    @GetMapping("/track-table/search")
    public String searchTable(@RequestParam("displayId") int displayId, Model model) {
        
        // UPDATE THIS LINE to use our new bulletproof method name
        List<Reservation> allReservations = reservationRepository.findAllSortedById(); 

        if (displayId > 0 && displayId <= allReservations.size()) {
            Reservation foundReservation = allReservations.get(displayId - 1);
            model.addAttribute("order", foundReservation);
            return "track-table"; 
        } else {
            return "redirect:/track-table?error=InvalidID";
        }
    }
}