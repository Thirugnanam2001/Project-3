package com.jts.movie_ticket_booking_system.repository;

import com.jts.movie_ticket_booking_system.entity.TheaterSeat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TheaterSeatRepository extends JpaRepository<TheaterSeat,Integer> {
}
