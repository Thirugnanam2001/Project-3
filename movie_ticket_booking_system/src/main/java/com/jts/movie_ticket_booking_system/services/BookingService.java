package com.jts.movie_ticket_booking_system.services;

import com.jts.movie_ticket_booking_system.entity.*;
import com.jts.movie_ticket_booking_system.repository.*;
import com.jts.movie_ticket_booking_system.request.BookingRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
//import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TheaterRepository theaterRepository;
//
//    @Autowired
//    private ShowTimeRepository showTimeRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private ShowSeatRepository showSeatRepository;

    public List<Theater> getTheatersByMovieId(Integer movieId) {
        try {
            return theaterRepository.findTheatersByMovieId(movieId);
        } catch (Exception e) {
            System.err.println("Error getting theaters: " + e.getMessage());
            return theaterRepository.findAll();
        }
    }

    public List<Show> getShowTimesByMovieAndTheater(Integer movieId, Integer theaterId, String date) {
        try {
            return showRepository.getAllShowsOfMovie(movieId).stream()
                    .filter(show -> show.getTheater().getId().equals(theaterId))
                    .filter(show -> show.getDate().toString().equals(date))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error getting show times: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<ShowSeat> getAvailableSeats(Integer showId) {
        try {
            return showRepository.findById(showId)
                    .map(Show::getShowSeatList)
                    .orElse(new ArrayList<>())
                    .stream()
                    .filter(ShowSeat::getIsAvailable)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error getting available seats: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<String> getOccupiedSeats(Integer showId) {
        try {
            // Get all seats that are not available
            return showRepository.findById(showId)
                    .map(Show::getShowSeatList)
                    .orElse(new ArrayList<>())
                    .stream()
                    .filter(seat -> !seat.getIsAvailable())
                    .map(ShowSeat::getSeatNo)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error getting occupied seats: " + e.getMessage());
            return new ArrayList<>();
        }
    }
//    public List<ShowTime> getShowTimesByMovieAndTheater(Long movieId, Long theaterId, LocalDate date) {
//        try {
//            // Simple implementation - return all show times for now
//            // You'll need to implement proper filtering based on your entity structure
//            return showTimeRepository.findAll();
//        } catch (Exception e) {
//            System.err.println("Error getting show times: " + e.getMessage());
//            return new ArrayList<>();
//        }
//    }

    public List<String> getAvailableSeats(Long showTimeId) {
        // Return mock seat data for now
        return List.of(
                "1A", "1B", "1C", "1D", "1E", "1F",
                "2A", "2B", "2C", "2D", "2E", "2F",
                "3A", "3B", "3C", "3D", "3E", "3F",
                "4A", "4B", "4C", "4D", "4E", "4F"
        );
    }

    public List<String> getOccupiedSeats(Long showTimeId) {
        try {
            return bookingRepository.findOccupiedSeatsByShowTimeId(showTimeId);
        } catch (Exception e) {
            System.err.println("Error getting occupied seats: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Booking createBooking(BookingRequest bookingRequest, String username) {
        try {
            User user = userRepository.findByEmailId(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Create a simple booking for now
            Booking booking = new Booking();
            booking.setUser(user);
            booking.setSelectedSeats(String.join(",", bookingRequest.getSelectedSeats()));
            booking.setTotalAmount(bookingRequest.getTotalAmount());
            booking.setBookingStatus("CONFIRMED");
            booking.setBookingDate(java.sql.Date.valueOf(LocalDate.now()));

            return bookingRepository.save(booking);
        } catch (Exception e) {
            throw new RuntimeException("Booking creation failed: " + e.getMessage());
        }
    }

    public List<Booking> getUserBookings(String username) {
        try {
            User user = userRepository.findByEmailId(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return bookingRepository.findByUserOrderByBookingDateDesc(user);
        } catch (Exception e) {
            System.err.println("Error getting user bookings: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Booking cancelBooking(Long bookingId, String username) {
        try {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            if (!booking.getUser().getEmailId().equals(username)) {
                throw new RuntimeException("Unauthorized to cancel this booking");
            }

            booking.setBookingStatus("CANCELLED");
            return bookingRepository.save(booking);
        } catch (Exception e) {
            throw new RuntimeException("Cancellation failed: " + e.getMessage());
        }
    }
}
