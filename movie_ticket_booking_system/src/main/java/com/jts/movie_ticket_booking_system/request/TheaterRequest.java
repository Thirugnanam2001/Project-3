package com.jts.movie_ticket_booking_system.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TheaterRequest {
    private Integer theaterId;
    private Integer movieId;
    private String name;
    private String address;
}
