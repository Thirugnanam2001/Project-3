package com.jts.movie_ticket_booking_system.exceptions;

public class TheaterIsNotExist extends RuntimeException{
    private static final long serialVersionUID = -80039152090012599L;

	public TheaterIsNotExist() {
        super("Theater is not present in this address");
    }
}
