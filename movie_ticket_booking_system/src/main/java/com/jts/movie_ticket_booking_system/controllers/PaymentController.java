package com.jts.movie_ticket_booking_system.controllers;

import com.jts.movie_ticket_booking_system.entity.*;
import com.jts.movie_ticket_booking_system.repository.*;
import com.jts.movie_ticket_booking_system.services.PaymentService;
import com.jts.movie_ticket_booking_system.services.TicketService;
import com.jts.movie_ticket_booking_system.request.TicketRequest;
import com.jts.movie_ticket_booking_system.response.TicketResponse;
import com.jts.movie_ticket_booking_system.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;

@Controller
@RequestMapping("/payment")
public class PaymentController {

  @Autowired
  private UserService userService;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TheaterRepository theaterRepository;

    @GetMapping
    public String showPaymentPage(@RequestParam Integer showId,
                                  @RequestParam Double totalAmount,
                                  @RequestParam String selectedSeats,
                                  Model model) {
        try {
            // Get show details
            Optional<Show> showOpt = showRepository.findById(showId);
            if (showOpt.isEmpty()) {
                return "redirect:/movies";
            }

            Show show = showOpt.get();
            Movie movie = show.getMovie();
            Theater theater = show.getTheater();

            // Convert java.sql.Time to LocalTime for Thymeleaf formatting
            LocalTime showTime = show.getTime().toLocalTime();
            String formattedTime = showTime.format(DateTimeFormatter.ofPattern("hh:mm a"));

            model.addAttribute("showId", showId);
            model.addAttribute("totalAmount", totalAmount);
            model.addAttribute("selectedSeats", selectedSeats);
            model.addAttribute("movie", movie);
            model.addAttribute("theater", theater);
            model.addAttribute("show", show);
            model.addAttribute("formattedTime", formattedTime);
            model.addAttribute("pageTitle", "Payment - " + movie.getMovieName());

            return "payment";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading payment page");
            return "redirect:/movies";
        }
    }
    @PostMapping("/process")
    public String processPayment(@RequestParam Integer showId,
                                 @RequestParam Double totalAmount,
                                 @RequestParam String selectedSeats,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        try {
            // Get authenticated user from security context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            Optional<User> userOpt = userRepository.findByEmailId(username);
            if (userOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "User not found. Please login again.");
                return "redirect:/login";
            }

            User user = userOpt.get();

            // Create ticket with actual user ID
            TicketRequest ticketRequest = new TicketRequest();
            ticketRequest.setShowId(showId);
            ticketRequest.setUserId(user.getId()); // USE ACTUAL USER ID
            ticketRequest.setRequestSeats(Arrays.asList(selectedSeats.split(",")));

            TicketResponse ticketResponse = ticketService.ticketBooking(ticketRequest);

            redirectAttributes.addFlashAttribute("paymentSuccess", Boolean.TRUE);
            redirectAttributes.addFlashAttribute("ticketData", ticketResponse);

            return "redirect:/ticket";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Payment failed: " + e.getMessage());
            return "redirect:/payment?showId=" + showId + "&totalAmount=" + totalAmount + "&selectedSeats=" + selectedSeats;
        }
    }
//    @PostMapping("/process")
//    public String processPayment(@RequestParam Integer showId,
//                                 @RequestParam Double totalAmount,
//                                 @RequestParam String selectedSeats,
//                                 @RequestParam String username,
//                                 @RequestParam String password,
//                                 RedirectAttributes redirectAttributes) {
//        try {
//            System.out.println("Processing payment for show: " + showId);
//            System.out.println("Username: " + username);
//
//            // Simple authentication check
//            if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
//                redirectAttributes.addFlashAttribute("error", "Please enter both username and password");
//                return "redirect:/payment?showId=" + showId + "&totalAmount=" + totalAmount + "&selectedSeats=" + selectedSeats;
//            }
//            User currentUser = userService.getCurrentUser();
//            // Create ticket
//            TicketRequest ticketRequest = new TicketRequest();
//            ticketRequest.setShowId(showId);
//            ticketRequest.setUserId(currentUser.getId()); // Default user for demo
//            ticketRequest.setRequestSeats(Arrays.asList(selectedSeats.split(",")));
//
//            TicketResponse ticketResponse = ticketService.ticketBooking(ticketRequest);
//
//            // Add success attributes for ticket page - make sure paymentSuccess is Boolean
//            redirectAttributes.addFlashAttribute("paymentSuccess", Boolean.TRUE);
//            redirectAttributes.addFlashAttribute("ticketData", ticketResponse);
//
//            return "redirect:/ticket";
//
//        } catch (Exception e) {
//            System.err.println("Payment processing error: " + e.getMessage());
//            redirectAttributes.addFlashAttribute("error", "Payment processing failed: " + e.getMessage());
//            return "redirect:/payment?showId=" + showId + "&totalAmount=" + totalAmount + "&selectedSeats=" + selectedSeats;
//        }
//    }
}

