package com.jts.movie_ticket_booking_system.exceptions;

public class MovieDoesNotExists extends RuntimeException {
	private static final long serialVersionUID = -5385129013790060351L;

	public MovieDoesNotExists() {
		super("Movie dose not Exists");
	}
}
