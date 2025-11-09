package com.jts.movie_ticket_booking_system.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor

public class BookingResponse {
    private Long bookingId;
    private String movieTitle;
    private String theaterName;
    private Date showDate;
    private String showTime;
    private List<String> seats;
    private Double totalAmount;
    private String bookingStatus;
    private String qrCode;

    // Constructors, getters, setters
    public BookingResponse() {}

    // Add getters and setters for all fields
}