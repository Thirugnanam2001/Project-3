package com.jts.movie_ticket_booking_system.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jts.movie_ticket_booking_system.request.TicketRequest;
import com.jts.movie_ticket_booking_system.response.TicketResponse;
import com.jts.movie_ticket_booking_system.services.TicketService;
import com.jts.movie_ticket_booking_system.services.TicketPdfService;
import com.jts.movie_ticket_booking_system.entity.Ticket;
import com.jts.movie_ticket_booking_system.repository.TicketRepository;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;

@Controller
@RequestMapping("/ticket")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketPdfService ticketPdfService;

    @GetMapping
    public String showTicketPage(Model model) {

        // Check if we have flash attributes from payment process
        Boolean paymentSuccess = (Boolean) model.getAttribute("paymentSuccess");
        TicketResponse ticketData = (TicketResponse) model.getAttribute("ticketData");

        System.out.println("Ticket page requested - paymentSuccess: " + paymentSuccess);
        System.out.println("Ticket data: " + ticketData);

        if (paymentSuccess == null || !paymentSuccess || ticketData == null) {
            model.addAttribute("paymentSuccess", false);
            model.addAttribute("pageTitle", "Ticket Not Found");
            return "ticket";
        }

        model.addAttribute("ticket", ticketData);
        model.addAttribute("paymentSuccess", true);
        model.addAttribute("pageTitle", "Ticket - " + ticketData.getMovieName());

        System.out.println("Ticket loaded successfully: " + ticketData.getMovieName());
        return "ticket";
    }

    @GetMapping("/download-pdf")
    public void downloadTicketPdf(@RequestParam String bookingId,
                                  HttpServletResponse response) throws Exception {

        System.out.println("Download PDF requested for booking ID: " + bookingId);

        // Use the repository method to find by booking ID
        Optional<Ticket> ticketOpt = ticketRepository.findByBookingId(bookingId);

        if (ticketOpt.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Ticket not found with booking ID: " + bookingId);
            return;
        }

        Ticket ticket = ticketOpt.get();

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=ticket-" + bookingId + ".pdf");

        ticketPdfService.generateTicketPdf(ticket, response.getOutputStream());
        response.flushBuffer();

        System.out.println("PDF generated successfully for booking ID: " + bookingId);
    }

    @PostMapping("/book")
    public ResponseEntity<Object> ticketBooking(@RequestBody TicketRequest ticketRequest) {
        try {
            TicketResponse result = ticketService.ticketBooking(ticketRequest);
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}