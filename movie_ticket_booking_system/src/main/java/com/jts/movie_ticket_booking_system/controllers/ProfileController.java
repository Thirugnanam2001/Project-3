package com.jts.movie_ticket_booking_system.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jts.movie_ticket_booking_system.entity.User;
import com.jts.movie_ticket_booking_system.response.TicketResponse;
import com.jts.movie_ticket_booking_system.services.TicketService;
import com.jts.movie_ticket_booking_system.services.UserService;
import com.jts.movie_ticket_booking_system.repository.UserRepository;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String showProfile(Model model) {
        try {
            // Get authenticated user's email from security context
            String userEmail = getAuthenticatedUserEmail();

            if (userEmail == null) {
                model.addAttribute("errorMessage", "Please login to view your profile");
                return "redirect:/login";
            }

            // Get user from database using email
            User user = getUserByEmail(userEmail);

            if (user == null) {
                model.addAttribute("errorMessage", "User not found. Please contact support.");
                // Add empty data to avoid template errors
                model.addAttribute("user", new User());
                model.addAttribute("bookings", new ArrayList<>());
                model.addAttribute("totalBookings", 0);
                model.addAttribute("monthlyBookings", 0);
                model.addAttribute("pageTitle", "My Profile - Movies Ticket Browser");
                return "profile";
            }

            // Get ONLY current user's bookings using their specific user ID
            List<TicketResponse> userBookings = new ArrayList<>();
            if (user.getId() != null) {
                try {
                    userBookings = ticketService.getUserTickets(user.getId());
                    System.out.println("Found " + userBookings.size() + " bookings for user: " + userEmail);

                    // Debug: Print booking details
                    for (TicketResponse booking : userBookings) {
                        System.out.println("Booking: " + booking.getBookingId() +
                                " | Movie: " + booking.getMovieName() +
                                " | Date: " + booking.getBookingDate());
                    }
                } catch (Exception e) {
                    System.out.println("Error fetching bookings for user " + userEmail + ": " + e.getMessage());
                    userBookings = new ArrayList<>();
                }
            }

            // Calculate stats
            int totalBookings = userBookings.size();
            int monthlyBookings = (int) userBookings.stream()
                    .filter(booking -> booking != null && booking.getBookingDate() != null)
                    .filter(booking -> isCurrentMonth(booking.getBookingDate()))
                    .count();

            model.addAttribute("user", user);
            model.addAttribute("bookings", userBookings);
            model.addAttribute("totalBookings", totalBookings);
            model.addAttribute("monthlyBookings", monthlyBookings);
            model.addAttribute("pageTitle", "My Profile - Movies Ticket Browser");

        } catch (Exception e) {
            System.out.println("Error in profile controller: " + e.getMessage());
            model.addAttribute("errorMessage", "An error occurred while loading your profile");
            // Add empty data to avoid template errors
            model.addAttribute("user", new User());
            model.addAttribute("bookings", new ArrayList<>());
            model.addAttribute("totalBookings", 0);
            model.addAttribute("monthlyBookings", 0);
        }

        return "profile";
    }

    private String getAuthenticatedUserEmail() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null &&
                    authentication.isAuthenticated() &&
                    !authentication.getName().equals("anonymousUser")) {
                System.out.println("Authenticated user email: " + authentication.getName());
                return authentication.getName();
            }
        } catch (Exception e) {
            System.out.println("Error getting authenticated user: " + e.getMessage());
        }
        System.out.println("No authenticated user found");
        return null;
    }

    private User getUserByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        try {
            Optional<User> userOpt = userRepository.findByEmailId(email);
            if (userOpt.isPresent()) {
                System.out.println("User found: " + email);
                return userOpt.get();
            } else {
                System.out.println("User not found in database: " + email);
                return null;
            }
        } catch (Exception e) {
            System.out.println("Error getting user by email " + email + ": " + e.getMessage());
            return null;
        }
    }

    private boolean isCurrentMonth(java.time.LocalDate date) {
        if (date == null) return false;
        java.time.LocalDate now = java.time.LocalDate.now();
        return date.getMonth() == now.getMonth() && date.getYear() == now.getYear();
    }
}
//package com.jts.movie.controllers;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import com.jts.movie.entity.User;
//import com.jts.movie.response.TicketResponse;
//import com.jts.movie.services.TicketService;
//import com.jts.movie.repository.UserRepository;
//
//import java.util.List;
//import java.util.ArrayList;
//import java.util.Optional;
//
//@Controller
//@RequestMapping("/profile")
//public class ProfileController {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private TicketService ticketService;
//
//    @GetMapping
//    public String showProfile(Model model) {
//        // Get authenticated user's email from security context
//        String userEmail = getAuthenticatedUserEmail();
//
//        // Get user from database using email
//        User user = getUserByEmail(userEmail);
//
//        if (user == null) {
//            // If no authenticated user or user not found, try getting any user
//            user = getAnyUserFromDatabase();
//        }
//
//        // Get user's bookings
//        List<TicketResponse> userBookings = new ArrayList<>();
//        if (user != null && user.getId() != null) {
//            try {
//                userBookings = ticketService.getUserTickets(user.getId());
//            } catch (Exception e) {
//                System.out.println("No bookings found: " + e.getMessage());
//            }
//        }
//
//        // Calculate stats
//        int totalBookings = userBookings.size();
//        int monthlyBookings = (int) userBookings.stream()
//                .filter(booking -> booking != null && booking.getBookingDate() != null)
//                .filter(booking -> isCurrentMonth(booking.getBookingDate()))
//                .count();
//
//        model.addAttribute("user", user);
//        model.addAttribute("bookings", userBookings);
//        model.addAttribute("totalBookings", totalBookings);
//        model.addAttribute("monthlyBookings", monthlyBookings);
//        model.addAttribute("pageTitle", "My Profile - Movies Ticket Browser");
//
//        return "profile";
//    }
//
//    private String getAuthenticatedUserEmail() {
//        try {
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            if (authentication != null && authentication.isAuthenticated()) {
//                return authentication.getName(); // Usually returns email/username
//            }
//        } catch (Exception e) {
//            System.out.println("No authenticated user found: " + e.getMessage());
//        }
//        return null;
//    }
//
//    private User getUserByEmail(String email) {
//        if (email == null || email.trim().isEmpty()) {
//            return null;
//        }
//        try {
//            Optional<User> userOpt = userRepository.findByEmailId(email);
//            return userOpt.orElse(null);
//        } catch (Exception e) {
//            System.out.println("Error getting user by email: " + e.getMessage());
//            return null;
//        }
//    }
//
//    private User getAnyUserFromDatabase() {
//        try {
//            List<User> allUsers = userRepository.findAll();
//            if (!allUsers.isEmpty()) {
//                return allUsers.get(0);
//            }
//            System.out.println("No users found in database");
//            return null;
//        } catch (Exception e) {
//            System.out.println("Error getting any user from database: " + e.getMessage());
//            return null;
//        }
//    }
//
//    private boolean isCurrentMonth(java.time.LocalDate date) {
//        if (date == null) return false;
//        java.time.LocalDate now = java.time.LocalDate.now();
//        return date.getMonth() == now.getMonth() && date.getYear() == now.getYear();
//    }
//}
