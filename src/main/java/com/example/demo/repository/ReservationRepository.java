package com.example.demo.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import com.example.demo.model.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.reservation_date = :date AND r.reservation_time = :time AND r.status != 'Cancelled'")
    long countActiveReservations(@Param("date") LocalDate date, @Param("time") LocalTime time);
    
    // THE BULLETPROOF FIX: We tell SQL exactly how to sort it using @Query
    @Query("SELECT r FROM Reservation r ORDER BY r.reservation_id ASC")
    List<Reservation> findAllSortedById(); 
    @Query("SELECT r FROM Reservation r WHERE r.customer_name LIKE CONCAT('%', :keyword, '%')")
    List<Reservation> searchByCustomerName(@Param("keyword") String keyword);

}