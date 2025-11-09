package com.jts.movie_ticket_booking_system.request;

import lombok.Data;
import java.util.List;

@Data
public class SetupRequest {
    private List<MovieRequest> movies;
    private List<TheaterRequest> theaters;
    private List<TheaterSeatRequest> theaterSeats;
    private List<ShowRequest> shows;
    private List<ShowSeatRequest> showSeats;
}