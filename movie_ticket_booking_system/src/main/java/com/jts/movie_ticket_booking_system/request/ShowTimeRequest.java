package com.jts.movie_ticket_booking_system.request;

import com.jts.movie_ticket_booking_system.entity.Movie;
import com.jts.movie_ticket_booking_system.entity.Theater;
import jakarta.persistence.*;

import java.sql.Time;
import java.util.Date;

public class ShowTimeRequest {
    private Long id;
    private Time time;
    private Date date;
    private Movie movie;
    private Theater theater;
    private String availableSeats;
}
