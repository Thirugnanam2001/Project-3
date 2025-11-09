package com.jts.movie_ticket_booking_system.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Date;

import com.jts.movie_ticket_booking_system.enums.Genre;
import com.jts.movie_ticket_booking_system.enums.Language;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieRequest {
	private String movieName;
	private Integer duration;
	private Double rating;
	private Date releaseDate;
	private Genre genre;
	private Language language;
}
