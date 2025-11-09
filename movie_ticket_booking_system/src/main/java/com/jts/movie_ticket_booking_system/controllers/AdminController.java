package com.jts.movie_ticket_booking_system.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jts.movie_ticket_booking_system.request.TheaterRequest;
import com.jts.movie_ticket_booking_system.request.TheaterSeatRequest;
import com.jts.movie_ticket_booking_system.services.TheaterService;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    private final TheaterService theaterService;

    public AdminController(TheaterService theaterService) {
        this.theaterService = theaterService;
    }


    @GetMapping("/theaters/add")
    public String showAddTheaterForm(Model model) {
        model.addAttribute("theaterRequest", new TheaterRequest());
        model.addAttribute("pageTitle", "Add Theater - Admin");
        return "admin/add-theater";
    }

    @PostMapping("/theaters/add")
    public String addTheater(@ModelAttribute TheaterRequest theaterRequest,
                             RedirectAttributes redirectAttributes) {
        try {
            String result = theaterService.addTheater(theaterRequest);
            redirectAttributes.addFlashAttribute("successMessage", result);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/theaters/add";
    }

    @GetMapping("/theaters/seats")
    public String showAddTheaterSeatsForm(Model model) {
        model.addAttribute("theaterSeatRequest", new TheaterSeatRequest());
        model.addAttribute("pageTitle", "Add Theater Seats - Admin");
        return "admin/add-theater-seats";
    }

    @PostMapping("/theaters/seats")
    public String addTheaterSeats(@ModelAttribute TheaterSeatRequest theaterSeatRequest,
                                  RedirectAttributes redirectAttributes) {
        try {
            String result = theaterService.addTheaterSeat(theaterSeatRequest);
            redirectAttributes.addFlashAttribute("successMessage", result);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/theaters/seats";
    }

    @GetMapping("/movies/add")
    public String showAddMovieForm(Model model) {
        model.addAttribute("pageTitle", "Add Movie - Admin");
        return "admin/add-movie";
    }

    @GetMapping("/reports")
    public String viewReports(Model model) {
        model.addAttribute("pageTitle", "Reports - Admin");
        return "admin/reports";
    }
}