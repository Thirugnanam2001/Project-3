package com.jts.movie_ticket_booking_system.services;

import com.jts.movie_ticket_booking_system.entity.*;
import com.jts.movie_ticket_booking_system.enums.Genre;
import com.jts.movie_ticket_booking_system.enums.Language;
import com.jts.movie_ticket_booking_system.enums.SeatType;
import com.jts.movie_ticket_booking_system.exceptions.MovieDoesNotExists;
import com.jts.movie_ticket_booking_system.exceptions.ShowDoesNotExists;
import com.jts.movie_ticket_booking_system.exceptions.TheaterDoesNotExists;
import com.jts.movie_ticket_booking_system.repository.MovieRepository;
import com.jts.movie_ticket_booking_system.repository.ShowRepository;
import com.jts.movie_ticket_booking_system.repository.TheaterRepository;
import com.jts.movie_ticket_booking_system.request.ShowRequest;
import com.jts.movie_ticket_booking_system.request.ShowSeatRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShowServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private TheaterRepository theaterRepository;

    @Mock
    private ShowRepository showRepository;

    @InjectMocks
    private ShowService showService;

    private ShowRequest showRequest;
    private ShowSeatRequest showSeatRequest;
    private Movie movie;
    private Theater theater;
    private Show show;

    @BeforeEach
    void setUp() {
        showRequest = new ShowRequest();
        showRequest.setShowStartTime(Time.valueOf(LocalTime.of(18, 0)));
        showRequest.setShowDate(Date.valueOf(LocalDate.now().plusDays(1)));
        showRequest.setTheaterId(1);
        showRequest.setMovieId(1);

        showSeatRequest = new ShowSeatRequest();
        showSeatRequest.setShowId(1);
        showSeatRequest.setPriceOfPremiumSeat(350);
        showSeatRequest.setPriceOfClassicSeat(200);

        movie = Movie.builder()
                .id(1)
                .movieName("Test Movie")
                .genre(Genre.ACTION)
                .language(Language.ENGLISH)
                .shows(new ArrayList<>())
                .build();

        theater = Theater.builder()
                .id(1)
                .name("Test Theater")
                .address("Test Address")
                .showList(new ArrayList<>())
                .theaterSeatList(createTheaterSeats())
                .build();

        show = Show.builder()
                .id(1)
                .time(Time.valueOf(LocalTime.of(18, 0)))
                .date(Date.valueOf(LocalDate.now().plusDays(1)))
                .movie(movie)
                .theater(theater)
                .showSeatList(new ArrayList<>())
                .build();
    }

    @Test
    void addShow_WhenValidRequest_ShouldSaveShow() {
        // Given
        when(movieRepository.findById(1)).thenReturn(Optional.of(movie));
        when(theaterRepository.findById(1)).thenReturn(Optional.of(theater));
        when(showRepository.save(any(Show.class))).thenReturn(show);

        // When
        String result = showService.addShow(showRequest);

        // Then
        assertThat(result).isEqualTo("Show has been added Successfully");
        verify(movieRepository).findById(1);
        verify(theaterRepository).findById(1);
        verify(showRepository).save(any(Show.class));
        verify(movieRepository).save(movie);
        verify(theaterRepository).save(theater);
    }

    @Test
    void addShow_WhenMovieNotExists_ShouldThrowException() {
        // Given
        when(movieRepository.findById(1)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> showService.addShow(showRequest))
                .isInstanceOf(MovieDoesNotExists.class);

        verify(theaterRepository, never()).findById(anyInt());
        verify(showRepository, never()).save(any(Show.class));
    }

    @Test
    void addShow_WhenTheaterNotExists_ShouldThrowException() {
        // Given
        when(movieRepository.findById(1)).thenReturn(Optional.of(movie));
        when(theaterRepository.findById(1)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> showService.addShow(showRequest))
                .isInstanceOf(TheaterDoesNotExists.class);

        verify(showRepository, never()).save(any(Show.class));
    }

    @Test
    void associateShowSeats_WhenShowExists_ShouldAssociateSeats() {
        // Given
        when(showRepository.findById(1)).thenReturn(Optional.of(show));
        when(showRepository.save(any(Show.class))).thenReturn(show);

        // When
        String result = showService.associateShowSeats(showSeatRequest);

        // Then
        assertThat(result).isEqualTo("Show seats have been associated successfully");
        verify(showRepository).findById(1);
        verify(showRepository).save(show);

        // Verify show seats were created
        assertThat(show.getShowSeatList()).isNotEmpty();

        // Verify pricing
        show.getShowSeatList().forEach(showSeat -> {
            if (showSeat.getSeatType() == SeatType.PREMIUM) {
                assertThat(showSeat.getPrice()).isEqualTo(350);
            } else {
                assertThat(showSeat.getPrice()).isEqualTo(200);
            }
        });
    }

    @Test
    void associateShowSeats_WhenShowNotExists_ShouldThrowException() {
        // Given
        when(showRepository.findById(1)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> showService.associateShowSeats(showSeatRequest))
                .isInstanceOf(ShowDoesNotExists.class);

        verify(showRepository, never()).save(any(Show.class));
    }

    private List<TheaterSeat> createTheaterSeats() {
        List<TheaterSeat> seats = new ArrayList<>();

        // Add some premium seats
        for (int i = 1; i <= 2; i++) {
            for (char c = 'A'; c <= 'E'; c++) {
                TheaterSeat seat = TheaterSeat.builder()
                        .seatNo(i + String.valueOf(c))
                        .seatType(SeatType.PREMIUM)
                        .theater(theater)
                        .build();
                seats.add(seat);
            }
        }

        // Add some classic seats
        for (int i = 3; i <= 5; i++) {
            for (char c = 'A'; c <= 'E'; c++) {
                TheaterSeat seat = TheaterSeat.builder()
                        .seatNo(i + String.valueOf(c))
                        .seatType(SeatType.CLASSIC)
                        .theater(theater)
                        .build();
                seats.add(seat);
            }
        }

        return seats;
    }
}