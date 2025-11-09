package com.jts.movie_ticket_booking_system.controllers;

import com.jts.movie_ticket_booking_system.entity.*;
import com.jts.movie_ticket_booking_system.repository.*;
import com.jts.movie_ticket_booking_system.services.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jts.movie_ticket_booking_system.utils.TimeFormatter;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TheaterRepository theaterRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private ShowSeatRepository showSeatRepository;

    @Autowired
    private BookingService bookingService;

    public String formatTime(Time time) {
        if (time == null) return "N/A";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            return sdf.format(time);
        } catch (Exception e) {
            return time.toString();
        }
    }
    // Main booking page - redirects to theater selection
    @GetMapping
    public String showBookingPage(@RequestParam Integer movieId, Model model) {
        try {
            Optional<Movie> movieOpt = movieRepository.findById(movieId);
            if (movieOpt.isEmpty()) {
                return "redirect:/movies";
            }

            Movie movie = movieOpt.get();
            List<Theater> theaters = theaterRepository.findTheatersByMovieId(movieId);

            model.addAttribute("movie", movie);
            model.addAttribute("theaters", theaters);
            model.addAttribute("pageTitle", "Book Tickets - " + movie.getMovieName());

            return "booking/select-theater"; // Make sure this template exists
        } catch (Exception e) {
            model.addAttribute("error", "Error loading booking page");
            return "redirect:/movies";
        }
    }

    // Step 1: Select Theater
    @GetMapping("/theater")
    public String selectTheater(@RequestParam Integer movieId, Model model) {
        try {
            Optional<Movie> movieOpt = movieRepository.findById(movieId);
            if (movieOpt.isEmpty()) {
                return "redirect:/movies";
            }

            Movie movie = movieOpt.get();
            List<Theater> theaters = theaterRepository.findTheatersByMovieId(movieId);

            model.addAttribute("movie", movie);
            model.addAttribute("theaters", theaters);
            model.addAttribute("pageTitle", "Select Theater - " + movie.getMovieName());

            return "booking/select-theater";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading theaters");
            return "redirect:/movies";
        }
    }

    // Step 2: Select Show Time
    @GetMapping("/showtime")
    public String selectShowTime(@RequestParam Integer movieId,
                                 @RequestParam Integer theaterId,
                                 Model model) {
        try {
            System.out.println("Loading showtime for movieId: " + movieId + ", theaterId: " + theaterId);

            Optional<Movie> movieOpt = movieRepository.findById(movieId);
            Optional<Theater> theaterOpt = theaterRepository.findById(theaterId);

            if (movieOpt.isEmpty()) {
                System.out.println("Movie not found with ID: " + movieId);
                return "redirect:/movies";
            }
            if (theaterOpt.isEmpty()) {
                System.out.println("Theater not found with ID: " + theaterId);
                return "redirect:/booking/theater?movieId=" + movieId;
            }

            Movie movie = movieOpt.get();
            Theater theater = theaterOpt.get();

            System.out.println("Found movie: " + movie.getMovieName() + ", theater: " + theater.getName());

            // Get all shows for this movie and theater
            List<Show> shows = showRepository.getAllShowsOfMovie(movieId).stream()
                    .filter(show -> show.getTheater().getId().equals(theaterId))
                    .collect(Collectors.toList());

            System.out.println("Found " + shows.size() + " shows");

            // Add the formatTime method to the model
            model.addAttribute("formatTime", new Object() {
                public String formatTime(java.sql.Time time) {
                    if (time == null) return "N/A";
                    try {
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("hh:mm a");
                        return sdf.format(time);
                    } catch (Exception e) {
                        return time.toString();
                    }
                }
            });
            // Add this to your controller method
            model.addAttribute("timeFormatter", new TimeFormatter());
            model.addAttribute("movie", movie);
            model.addAttribute("theater", theater);
            model.addAttribute("shows", shows);
            model.addAttribute("pageTitle", "Select Show Time - " + movie.getMovieName());

            return "booking/select-showtime";
        } catch (Exception e) {
            System.err.println("ERROR in selectShowTime: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/booking/theater?movieId=" + movieId;
        }
    }

    // In your selectSeats method, add date formatters:
    @GetMapping("/seats")
    public String selectSeats(@RequestParam Integer showId, Model model) {
        try {
            Optional<Show> showOpt = showRepository.findById(showId);
            if (showOpt.isEmpty()) {
                return "redirect:/movies";
            }

            Show show = showOpt.get();
            List<ShowSeat> availableSeats = show.getShowSeatList().stream()
                    .filter(ShowSeat::getIsAvailable)
                    .collect(Collectors.toList());

            // Group seats by row for display
            List<String> rows = availableSeats.stream()
                    .map(seat -> seat.getSeatNo().substring(0, 1))
                    .distinct()
                    .collect(Collectors.toList());

            // Add date formatter utility
            model.addAttribute("dateFormatter", new Object() {
                public String formatDate(java.sql.Date date) {
                    if (date == null) return "N/A";
                    try {
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd, yyyy");
                        return sdf.format(date);
                    } catch (Exception e) {
                        return date.toString();
                    }
                }

                public String formatTime(java.sql.Time time) {
                    if (time == null) return "N/A";
                    try {
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("hh:mm a");
                        return sdf.format(time);
                    } catch (Exception e) {
                        return time.toString();
                    }
                }
            });

            model.addAttribute("show", show);
            model.addAttribute("availableSeats", availableSeats);
            model.addAttribute("rows", rows);
            model.addAttribute("pageTitle", "Select Seats - " + show.getMovie().getMovieName());

            return "booking/select-seats";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading seats");
            return "redirect:/movies";
        }
    }
// Step 4: Review Booking
@PostMapping("/review")
public String reviewBooking(@RequestParam Integer showId,
                            @RequestParam List<String> selectedSeats,
                            Model model) {
    try {
        Optional<Show> showOpt = showRepository.findById(showId);
        if (showOpt.isEmpty() || selectedSeats.isEmpty()) {
            return "redirect:/movies";
        }

        Show show = showOpt.get();

        // Calculate total amount
        double totalAmount = calculateTotalAmount(show, selectedSeats);

        model.addAttribute("show", show);
        model.addAttribute("selectedSeats", selectedSeats);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("pageTitle", "Review Booking - " + show.getMovie().getMovieName());

        return "booking/review-booking";

    } catch (Exception e) {
        // Log the error properly
        System.err.println("Error in reviewBooking: " + e.getMessage());
        e.printStackTrace();

        // Return error page or redirect with error message
        return "redirect:/booking/seats?showId=" + showId + "&error=true";
    }
}


    // Process Payment Redirect
    @PostMapping("/payment")
    public String processPayment(@RequestParam Integer showId,
                                 @RequestParam List<String> selectedSeats,
                                 @RequestParam Double totalAmount,
                                 RedirectAttributes redirectAttributes) {
        try {
            // Store booking data for payment page
            redirectAttributes.addAttribute("showId", showId);
            redirectAttributes.addAttribute("totalAmount", totalAmount);
            redirectAttributes.addAttribute("selectedSeats", String.join(",", selectedSeats));

            return "redirect:/payment";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error processing payment");
            return "redirect:/booking/review?showId=" + showId;
        }
    }

    private double calculateTotalAmount(Show show, List<String> selectedSeats) {
        double total = 0;
        for (String seatNo : selectedSeats) {
            Optional<ShowSeat> seatOpt = show.getShowSeatList().stream()
                    .filter(s -> s.getSeatNo().equals(seatNo))
                    .findFirst();

            if (seatOpt.isPresent()) {
                total += seatOpt.get().getPrice();
            }
        }
        return total;
    }
}
