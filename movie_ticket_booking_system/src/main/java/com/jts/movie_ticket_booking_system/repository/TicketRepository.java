package com.jts.movie_ticket_booking_system.repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jts.movie_ticket_booking_system.entity.Ticket;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    @Query("SELECT t FROM Ticket t WHERE t.bookingId = :bookingId")
    Optional<Ticket> findByBookingId(@Param("bookingId") String bookingId);

    @Query(value = "SELECT * FROM tickets WHERE user_id = :userId ORDER BY booked_at DESC", nativeQuery = true)
    List<Ticket> findTicketsByUserId(@Param("userId") Integer userId);

    // Use the correct field name from Ticket entity
    @Query("SELECT t FROM Ticket t WHERE t.user.id = :userId ORDER BY t.bookedAt DESC")
    List<Ticket> findByUserId(@Param("userId") Integer userId);

    // Alternative: Use Spring Data JPA derived query (recommended)
    List<Ticket> findByUser_IdOrderByBookedAtDesc(Integer userId);

}