package com.jts.movie_ticket_booking_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jts.movie_ticket_booking_system.entity.Theater;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TheaterRepository extends JpaRepository<Theater, Integer> {
    Theater findByAddress(String address);

    // Find theaters that have shows for a specific movie
    @Query("SELECT DISTINCT t FROM Theater t " +
            "JOIN t.showList s " +
            "WHERE s.movie.id = :movieId")
    List<Theater> findTheatersByMovieId(@Param("movieId") Integer movieId);

    List<Theater> findAll();

     Optional<Theater> findById(Integer theaterId);
}
