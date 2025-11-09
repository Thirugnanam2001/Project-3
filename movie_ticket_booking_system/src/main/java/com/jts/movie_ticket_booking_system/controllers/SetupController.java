package com.jts.movie_ticket_booking_system.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.jts.movie_ticket_booking_system.request.*;
import com.jts.movie_ticket_booking_system.services.MovieService;
import com.jts.movie_ticket_booking_system.services.TheaterService;
import com.jts.movie_ticket_booking_system.services.ShowService;

import java.util.*;

@RestController
@RequestMapping("/api/setup")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class SetupController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private TheaterService theaterService;

    @Autowired
    private ShowService showService;

    @PostMapping("/complete")
    public ResponseEntity<Map<String, Object>> completeSetup(@RequestBody SetupRequest setupRequest) {
        try {
            System.out.println("Starting complete setup...");

            Map<String, Object> response = new HashMap<>();
            Map<String, Object> results = new HashMap<>();

            // Process Movies
            if (setupRequest.getMovies() != null && !setupRequest.getMovies().isEmpty()) {
                results.put("movies", processMovies(setupRequest.getMovies()));
            }

            // Process Theaters
            if (setupRequest.getTheaters() != null && !setupRequest.getTheaters().isEmpty()) {
                results.put("theaters", processTheaters(setupRequest.getTheaters()));
            }

            // Process Theater Seats
            if (setupRequest.getTheaterSeats() != null && !setupRequest.getTheaterSeats().isEmpty()) {
                results.put("theaterSeats", processTheaterSeats(setupRequest.getTheaterSeats()));
            }

            // Process Shows
            if (setupRequest.getShows() != null && !setupRequest.getShows().isEmpty()) {
                results.put("shows", processShows(setupRequest.getShows()));
            }

            // Process Show Seats
            if (setupRequest.getShowSeats() != null && !setupRequest.getShowSeats().isEmpty()) {
                results.put("showSeats", processShowSeats(setupRequest.getShowSeats()));
            }

            response.put("status", "success");
            response.put("message", "Setup completed successfully");
            response.put("results", results);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Setup failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    private Map<String, Object> processMovies(List<MovieRequest> movies) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> details = new ArrayList<>();
        int success = 0;
        int failed = 0;

        for (MovieRequest movie : movies) {
            Map<String, Object> detail = new HashMap<>();
            detail.put("movieName", movie.getMovieName());

            try {
                String message = movieService.addMovie(movie);
                detail.put("status", "success");
                detail.put("message", message);
                success++;
            } catch (Exception e) {
                detail.put("status", "error");
                detail.put("message", e.getMessage());
                failed++;
            }
            details.add(detail);
        }

        result.put("total", movies.size());
        result.put("success", success);
        result.put("failed", failed);
        result.put("details", details);

        return result;
    }

    private Map<String, Object> processTheaters(List<TheaterRequest> theaters) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> details = new ArrayList<>();
        int success = 0;
        int failed = 0;

        for (TheaterRequest theater : theaters) {
            Map<String, Object> detail = new HashMap<>();
            detail.put("theaterName", theater.getName());

            try {
                String message = theaterService.addTheater(theater);
                detail.put("status", "success");
                detail.put("message", message);
                success++;
            } catch (Exception e) {
                detail.put("status", "error");
                detail.put("message", e.getMessage());
                failed++;
            }
            details.add(detail);
        }

        result.put("total", theaters.size());
        result.put("success", success);
        result.put("failed", failed);
        result.put("details", details);

        return result;
    }

    private Map<String, Object> processTheaterSeats(List<TheaterSeatRequest> theaterSeats) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> details = new ArrayList<>();
        int success = 0;
        int failed = 0;

        for (TheaterSeatRequest seat : theaterSeats) {
            Map<String, Object> detail = new HashMap<>();
            detail.put("address", seat.getAddress());

            try {
                String message = theaterService.addTheaterSeat(seat);
                detail.put("status", "success");
                detail.put("message", message);
                success++;
            } catch (Exception e) {
                detail.put("status", "error");
                detail.put("message", e.getMessage());
                failed++;
            }
            details.add(detail);
        }

        result.put("total", theaterSeats.size());
        result.put("success", success);
        result.put("failed", failed);
        result.put("details", details);

        return result;
    }

    private Map<String, Object> processShows(List<ShowRequest> shows) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> details = new ArrayList<>();
        int success = 0;
        int failed = 0;

        for (ShowRequest show : shows) {
            Map<String, Object> detail = new HashMap<>();
            detail.put("showTime", show.getShowStartTime());
            detail.put("showDate", show.getShowDate());

            try {
                String message = showService.addShow(show);
                detail.put("status", "success");
                detail.put("message", message);
                success++;
            } catch (Exception e) {
                detail.put("status", "error");
                detail.put("message", e.getMessage());
                failed++;
            }
            details.add(detail);
        }

        result.put("total", shows.size());
        result.put("success", success);
        result.put("failed", failed);
        result.put("details", details);

        return result;
    }

    private Map<String, Object> processShowSeats(List<ShowSeatRequest> showSeats) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> details = new ArrayList<>();
        int success = 0;
        int failed = 0;

        for (ShowSeatRequest showSeat : showSeats) {
            Map<String, Object> detail = new HashMap<>();
            detail.put("showId", showSeat.getShowId());

            try {
                String message = showService.associateShowSeats(showSeat);
                detail.put("status", "success");
                detail.put("message", message);
                success++;
            } catch (Exception e) {
                detail.put("status", "error");
                detail.put("message", e.getMessage());
                failed++;
            }
            details.add(detail);
        }

        result.put("total", showSeats.size());
        result.put("success", success);
        result.put("failed", failed);
        result.put("details", details);

        return result;
    }
}