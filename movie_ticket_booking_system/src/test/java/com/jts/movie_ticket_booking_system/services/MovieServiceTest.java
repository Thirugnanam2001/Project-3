package com.jts.movie_ticket_booking_system.services;

import com.jts.movie_ticket_booking_system.entity.Movie;
import com.jts.movie_ticket_booking_system.enums.Genre;
import com.jts.movie_ticket_booking_system.enums.Language;
import com.jts.movie_ticket_booking_system.exceptions.MovieAlreadyExist;
import com.jts.movie_ticket_booking_system.repository.MovieRepository;
import com.jts.movie_ticket_booking_system.request.MovieRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    private MovieRequest movieRequest;
    private Movie existingMovie;

    @BeforeEach
    void setUp() {
        movieRequest = new MovieRequest(
                "Test Movie",
                120,
                4.5,
                Date.valueOf(LocalDate.now().plusDays(30)),
                Genre.ACTION,
                Language.ENGLISH
        );

        existingMovie = Movie.builder()
                .movieName("Existing Movie")
                .language(Language.ENGLISH)
                .build();
    }

    @Test
    void addMovie_WhenMovieDoesNotExist_ShouldSaveMovie() {
        // Given
        when(movieRepository.findByMovieName(anyString())).thenReturn(null);
        when(movieRepository.save(any(Movie.class))).thenReturn(existingMovie);

        // When
        String result = movieService.addMovie(movieRequest);

        // Then
        assertThat(result).isEqualTo("The movie has been added successfully");
        verify(movieRepository).findByMovieName("Test Movie");
        verify(movieRepository).save(any(Movie.class));
    }

    @Test
    void addMovie_WhenMovieWithSameNameAndLanguageExists_ShouldThrowException() {
        // Given
        when(movieRepository.findByMovieName(anyString())).thenReturn(existingMovie);

        // When & Then
        assertThatThrownBy(() -> movieService.addMovie(movieRequest))
                .isInstanceOf(MovieAlreadyExist.class)
                .hasMessage("Movie is already exists with same name and language");

        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    void getAllMovies_ShouldReturnMovieList() {
        // Given
        Movie movie1 = Movie.builder().movieName("Movie 1").build();
        Movie movie2 = Movie.builder().movieName("Movie 2").build();
        List<Movie> movies = Arrays.asList(movie1, movie2);

        when(movieRepository.findAll()).thenReturn(movies);

        // When
        List<Movie> result = movieService.getAllMovies();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Movie::getMovieName)
                .containsExactly("Movie 1", "Movie 2");
    }

    @Test
    void getMovieById_WhenMovieExists_ShouldReturnMovie() {
        // Given
        when(movieRepository.findById(1)).thenReturn(Optional.of(existingMovie));

        // When
        Movie result = movieService.getMovieById(1);

        // Then
        assertThat(result).isEqualTo(existingMovie);
    }

    @Test
    void getMovieById_WhenMovieNotExists_ShouldThrowException() {
        // Given
        when(movieRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> movieService.getMovieById(999))
                .isInstanceOf(com.jts.movie_ticket_booking_system.exceptions.MovieDoesNotExists.class);
    }
}