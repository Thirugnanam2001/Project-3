package com.jts.movie_ticket_booking_system.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class WebController {

    // ========== PUBLIC PAGES ==========
    @GetMapping
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("pageTitle", "About Us - Movies Ticket Browser");
        return "about";
    }


    // ========== AUTHENTICATED USER PAGES ==========
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Dashboard - Movie Ticket Browser");
        return "dashboard";
    }


//    @GetMapping("/access-denied")
//    public String accessDenied(Model model) {
//        model.addAttribute("pageTitle", "Access Denied - Movie Ticket Browser");
//        return "error/403";
//    }

}