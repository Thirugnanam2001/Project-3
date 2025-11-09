package com.jts.movie_ticket_booking_system.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jts.movie_ticket_booking_system.convertor.ShowConvertor;
import com.jts.movie_ticket_booking_system.entity.Movie;
import com.jts.movie_ticket_booking_system.entity.Show;
import com.jts.movie_ticket_booking_system.entity.ShowSeat;
import com.jts.movie_ticket_booking_system.entity.Theater;
import com.jts.movie_ticket_booking_system.entity.TheaterSeat;
import com.jts.movie_ticket_booking_system.enums.SeatType;
import com.jts.movie_ticket_booking_system.exceptions.MovieDoesNotExists;
import com.jts.movie_ticket_booking_system.exceptions.ShowDoesNotExists;
import com.jts.movie_ticket_booking_system.exceptions.TheaterDoesNotExists;
import com.jts.movie_ticket_booking_system.repository.MovieRepository;
import com.jts.movie_ticket_booking_system.repository.ShowRepository;
import com.jts.movie_ticket_booking_system.repository.TheaterRepository;
import com.jts.movie_ticket_booking_system.request.ShowRequest;
import com.jts.movie_ticket_booking_system.request.ShowSeatRequest;

@Service
public class ShowService {

	@Autowired
	private MovieRepository movieRepository;

	@Autowired
	private TheaterRepository theaterRepository;

	@Autowired
	private ShowRepository showRepository;

	public String addShow(ShowRequest showRequest) {
		Show show = ShowConvertor.showDtoToShow(showRequest);

		Optional<Movie> movieOpt = movieRepository.findById(showRequest.getMovieId());

		if (movieOpt.isEmpty()) {
			throw new MovieDoesNotExists();
		}

		Optional<Theater> theaterOpt = theaterRepository.findById(showRequest.getTheaterId());

		if (theaterOpt.isEmpty()) {
			throw new TheaterDoesNotExists();
		}

		Theater theater = theaterOpt.get();
		Movie movie = movieOpt.get();

		show.setMovie(movie);
		show.setTheater(theater);
		show = showRepository.save(show);

		movie.getShows().add(show);
		theater.getShowList().add(show);

		movieRepository.save(movie);
		theaterRepository.save(theater);

		return "Show has been added Successfully";
	}
    public String associateShowSeats(ShowSeatRequest showSeatRequest) throws ShowDoesNotExists {
        Optional<Show> showOpt = showRepository.findById(showSeatRequest.getShowId());

        if (showOpt.isEmpty()) {
            throw new ShowDoesNotExists();
        }

        Show show = showOpt.get();
        Theater theater = show.getTheater();

        List<TheaterSeat> theaterSeatList = theater.getTheaterSeatList();
        List<ShowSeat> showSeatList = show.getShowSeatList();

        for (TheaterSeat theaterSeat : theaterSeatList) {
            // Determine price based on seat type
            Integer price = theaterSeat.getSeatType().equals(SeatType.CLASSIC)
                    ? showSeatRequest.getPriceOfClassicSeat()
                    : showSeatRequest.getPriceOfPremiumSeat();

            // Use builder pattern to create ShowSeat
            ShowSeat showSeat = ShowSeat.builder()
                    .seatNo(theaterSeat.getSeatNo())
                    .seatType(theaterSeat.getSeatType())
                    .price(price)
                    .isAvailable(Boolean.TRUE)
                    .isFoodContains(Boolean.FALSE)
                    .show(show)
                    .build();

            showSeatList.add(showSeat);
        }

        showRepository.save(show);

        return "Show seats have been associated successfully";
    }
}
