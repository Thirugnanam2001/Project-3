package com.jts.movie_ticket_booking_system.repository;

import com.jts.movie_ticket_booking_system.entity.ShowSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShowSeatRepository extends JpaRepository<ShowSeat, Integer> {
}