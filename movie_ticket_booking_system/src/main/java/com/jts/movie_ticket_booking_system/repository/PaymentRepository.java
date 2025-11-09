package com.jts.movie_ticket_booking_system.repository;

import com.jts.movie_ticket_booking_system.entity.Booking;
import com.jts.movie_ticket_booking_system.entity.Payment;
import com.jts.movie_ticket_booking_system.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentId(String paymentId);
    Optional<Payment> findByBookingAndStatus(Booking booking, PaymentStatus status);
    boolean existsByTransactionId(String transactionId);
}