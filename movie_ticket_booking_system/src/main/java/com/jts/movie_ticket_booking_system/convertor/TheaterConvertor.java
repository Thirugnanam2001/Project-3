package com.jts.movie_ticket_booking_system.convertor;


import com.jts.movie_ticket_booking_system.entity.Theater;
import com.jts.movie_ticket_booking_system.request.TheaterRequest;

public class TheaterConvertor {

    public static Theater theaterDtoToTheater(TheaterRequest theaterRequest) {
        Theater theater = Theater.builder()
                .name(theaterRequest.getName())
                .address(theaterRequest.getAddress())
                .build();
        return theater;
    }
}
