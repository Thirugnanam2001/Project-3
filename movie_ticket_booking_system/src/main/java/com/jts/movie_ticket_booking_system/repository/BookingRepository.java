package com.jts.movie_ticket_booking_system.repository;

import com.jts.movie_ticket_booking_system.entity.Booking;
import com.jts.movie_ticket_booking_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserOrderByBookingDateDesc(User user);
//    List<Booking> findByUserEmailIdOrderByBookingDateDesc(String userEmailId);
    List<Object[]> findBookingsByUserId(@Param("userId") Long userId);

    @Query("SELECT b.selectedSeats FROM Booking b WHERE b.showTime.id = :showTimeId AND b.bookingStatus = 'CONFIRMED'")
    List<String> findOccupiedSeatsByShowTimeId(@Param("showTimeId") Long showTimeId);
}