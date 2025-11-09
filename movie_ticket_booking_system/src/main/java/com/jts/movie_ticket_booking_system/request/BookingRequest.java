package com.jts.movie_ticket_booking_system.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequest {
    private Long showTimeId;
    private List<String> selectedSeats;
    private Double totalAmount;
    private Date bookingDate;
    // Constructors
}