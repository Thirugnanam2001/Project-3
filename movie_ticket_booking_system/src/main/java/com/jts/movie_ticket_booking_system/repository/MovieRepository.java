package com.jts.movie_ticket_booking_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jts.movie_ticket_booking_system.entity.Movie;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Integer> {
	Movie findByMovieName(String name);

    List<Movie> findAll();

    @Query("SELECT m FROM Movie m WHERE m.id = :movieId")
    Optional<Movie> findByMovieId(@Param("movieId") Integer movieId);
}