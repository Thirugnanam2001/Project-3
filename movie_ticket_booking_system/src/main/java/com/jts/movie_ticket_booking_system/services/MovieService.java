
package com.jts.movie_ticket_booking_system.services;

import com.jts.movie_ticket_booking_system.exceptions.MovieDoesNotExists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jts.movie_ticket_booking_system.convertor.MovieConvertor;
import com.jts.movie_ticket_booking_system.entity.Movie;
import com.jts.movie_ticket_booking_system.exceptions.MovieAlreadyExist;
import com.jts.movie_ticket_booking_system.repository.MovieRepository;
import com.jts.movie_ticket_booking_system.request.MovieRequest;

import java.util.List;
//import java.util.Optional;

@Service
public class MovieService {

	@Autowired
	private MovieRepository movieRepository;

	public String addMovie(MovieRequest movieRequest) {
		Movie movieByName = movieRepository.findByMovieName(movieRequest.getMovieName());

		if (movieByName != null && movieByName.getLanguage().equals(movieRequest.getLanguage())) {
			throw new MovieAlreadyExist();
		}

		Movie movie = MovieConvertor.movieDtoToMovie(movieRequest);

		movieRepository.save(movie);
		return "The movie has been added successfully";
	}
    // In MovieService.java
    public List<Movie> getAllMovies() {
        System.out.println("=== MOVIE SERVICE: Fetching all movies ===");
        List<Movie> movies = movieRepository.findAll();
        System.out.println("Found " + movies.size() + " movies in database");
        for (Movie movie : movies) {
            System.out.println("Movie: " + movie.getMovieName() +
                    " | Genre: " + movie.getGenre() +
                    " | Language: " + movie.getLanguage());
        }

        return movies;
    }
    public Movie getMovieById(Integer id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new MovieDoesNotExists());
    }

}
