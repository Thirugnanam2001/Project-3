package com.jts.movie_ticket_booking_system.repository;

import com.jts.movie_ticket_booking_system.entity.Movie;
import com.jts.movie_ticket_booking_system.entity.ShowTime;
import com.jts.movie_ticket_booking_system.entity.Theater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ShowTimeRepository extends JpaRepository<ShowTime, Long> {

    List<ShowTime> findByMovieAndTheater(Movie movie, Theater theater);

    // Simple implementation for now
    @Query("SELECT st FROM ShowTime st WHERE st.movie.id = :movieId")
    List<ShowTime> findByMovieIdAndTheaterIdAndShowDate(@Param("movieId") Long movieId,
                                                        @Param("theaterId") Long theaterId,
                                                        @Param("showDate") LocalDate showDate);

    @Query("SELECT '1A,1B,1C,2A,2B,2C' FROM ShowTime st WHERE st.id = :showTimeId")
    List<String> findAvailableSeatsByShowTimeId(@Param("showTimeId") Long showTimeId);
}