package com.jts.movie_ticket_booking_system.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketResponse {
    private String bookingId;
    private String movieName;
    private String theaterName;
    private LocalDate showDate;
    private String showTime;
    private String seats;
    private Double totalAmount;
    private LocalDate bookingDate;
    private Integer screenNumber;
    private String duration;

}
