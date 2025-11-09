package com.jts.movie_ticket_booking_system.convertor;

import com.jts.movie_ticket_booking_system.entity.Show;
import com.jts.movie_ticket_booking_system.entity.Ticket;
import com.jts.movie_ticket_booking_system.response.TicketResponse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TicketConvertor {

    public static TicketResponse returnTicket(Show show, Ticket ticket) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

        // SAFE NULL HANDLING for all potential null values
        String movieName = show.getMovie() != null ? show.getMovie().getMovieName() : "Unknown Movie";
        String theaterName = show.getTheater() != null ? show.getTheater().getName() : "Unknown Theater";

        LocalDate showDate = null;
        if (show.getDate() != null) {
            showDate = show.getDate().toLocalDate();
        } else {
            showDate = LocalDate.now();
        }

        String showTime = "N/A";
        if (show.getTime() != null) {
            showTime = show.getTime().toLocalTime().format(timeFormatter);
        }

        String seats = ticket.getBookedSeats() != null ? ticket.getBookedSeats() : "";

        Double totalAmount = 0.0;
        if (ticket.getTotalTicketsPrice() != null) {
            totalAmount = ticket.getTotalTicketsPrice().doubleValue();
        }

        // SAFE HANDLING for bookedAt - this was causing the NPE
        LocalDate bookingDate;
        if (ticket.getBookedAt() != null) {
            bookingDate = ticket.getBookedAt().toLocalDate();
        } else {
            bookingDate = LocalDate.now(); // Use current date as fallback
        }

        Integer screenNumber = show.getId() != null ? show.getId() : 0;

        String duration = "N/A";
        if (show.getMovie() != null && show.getMovie().getDuration() != null) {
            duration = show.getMovie().getDuration() + " mins";
        }

        return TicketResponse.builder()
                .bookingId(ticket.getBookingId() != null ? ticket.getBookingId() : "Unknown")
                .movieName(movieName)
                .theaterName(theaterName)
                .showDate(showDate)
                .showTime(showTime)
                .seats(seats)
                .totalAmount(totalAmount)
                .bookingDate(bookingDate) // This is now safe
                .screenNumber(screenNumber)
                .duration(duration)
                .build();
    }
}


//package com.jts.movie.convertor;
//
//import com.jts.movie.entity.Show;
//import com.jts.movie.entity.Ticket;
//import com.jts.movie.response.TicketResponse;
//
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//
//public class TicketConvertor {
//
//    public static TicketResponse returnTicket(Show show, Ticket ticket) {
//        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
//
//        LocalDate bookingDate = null;
//        if (ticket.getBookedAt() != null) {
//            bookingDate = ticket.getBookedAt().toLocalDate();
//        } else {
//            // Use current date as fallback
//            bookingDate = LocalDate.now();
//        }
//        return TicketResponse.builder()
//                .bookingId(ticket.getBookingId())
//                .movieName(show.getMovie().getMovieName())
//                .theaterName(show.getTheater().getName())
//                .showDate(show.getDate().toLocalDate())
//                .showTime(show.getTime().toLocalTime().format(timeFormatter))
//                .seats(ticket.getBookedSeats())
//                .totalAmount(ticket.getTotalTicketsPrice().doubleValue())
//                .bookingDate(ticket.getBookedAt().toLocalDate())
//                .screenNumber(show.getId()) // Using show ID as screen number
//                .duration(show.getMovie().getDuration() + " mins")
//                .build();
//    }
//
//
//}