package com.jts.movie_ticket_booking_system.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketRequest {
    private Integer showId;
    private Integer userId;
    private List<String> requestSeats;
}
