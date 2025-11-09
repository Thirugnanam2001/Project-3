package com.jts.movie_ticket_booking_system.controllers;

import com.jts.movie_ticket_booking_system.entity.*;
import com.jts.movie_ticket_booking_system.enums.Genre;
import com.jts.movie_ticket_booking_system.enums.Language;
import com.jts.movie_ticket_booking_system.enums.SeatType;
import com.jts.movie_ticket_booking_system.repository.MovieRepository;
import com.jts.movie_ticket_booking_system.repository.ShowRepository;
import com.jts.movie_ticket_booking_system.repository.TheaterRepository;
import com.jts.movie_ticket_booking_system.repository.ShowSeatRepository;
import com.jts.movie_ticket_booking_system.services.BookingService;
import com.jts.movie_ticket_booking_system.utils.TimeFormatter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@Import(TimeFormatter.class) // Import the TimeFormatter bean
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieRepository movieRepository;

    @MockBean
    private TheaterRepository theaterRepository;

    @MockBean
    private ShowRepository showRepository;

    @MockBean
    private ShowSeatRepository showSeatRepository;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private TimeFormatter timeFormatter; // Inject the TimeFormatter

    @Test
    @WithMockUser
    void showBookingPage_WithValidMovie_ShouldReturnTheaterSelection() throws Exception {
        // Given
        Movie movie = createTestMovie();
        when(movieRepository.findById(1)).thenReturn(Optional.of(movie));
        when(theaterRepository.findTheatersByMovieId(1)).thenReturn(Arrays.asList(createTestTheater()));

        // When & Then
        mockMvc.perform(get("/booking").param("movieId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("booking/select-theater"))
                .andExpect(model().attributeExists("movie", "theaters"))
                .andExpect(model().attribute("pageTitle", "Book Tickets - " + movie.getMovieName()));
    }

    @Test
    @WithMockUser
    void showBookingPage_WithInvalidMovie_ShouldRedirectToMovies() throws Exception {
        // Given
        when(movieRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/booking").param("movieId", "999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/movies"));
    }

    @Test
    @WithMockUser
    void selectTheater_WithValidData_ShouldReturnTheaterPage() throws Exception {
        // Given
        Movie movie = createTestMovie();
        when(movieRepository.findById(1)).thenReturn(Optional.of(movie));
        when(theaterRepository.findTheatersByMovieId(1)).thenReturn(Arrays.asList(createTestTheater()));

        // When & Then
        mockMvc.perform(get("/booking/theater").param("movieId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("booking/select-theater"))
                .andExpect(model().attributeExists("movie", "theaters"));
    }

    @Test
    @WithMockUser
    void selectShowTime_WithValidData_ShouldReturnShowTimePage() throws Exception {
        // Given
        Movie movie = createTestMovie();
        Theater theater = createTestTheater();
        Show show = createTestShow(movie, theater);

        when(movieRepository.findById(1)).thenReturn(Optional.of(movie));
        when(theaterRepository.findById(1)).thenReturn(Optional.of(theater));
        when(showRepository.getAllShowsOfMovie(1)).thenReturn(Arrays.asList(show));

        // When & Then
        mockMvc.perform(get("/booking/showtime")
                        .param("movieId", "1")
                        .param("theaterId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("booking/select-showtime"))
                .andExpect(model().attributeExists("movie", "theater", "shows"))
                .andExpect(model().attributeExists("timeFormatter"));
    }

    @Test
    @WithMockUser
    void selectSeats_WithValidShow_ShouldReturnSeatsPage() throws Exception {
        // Given
        Movie movie = createTestMovie();
        Theater theater = createTestTheater();
        Show show = createTestShow(movie, theater);
        show.setShowSeatList(createTestShowSeats(show));

        when(showRepository.findById(1)).thenReturn(Optional.of(show));

        // When & Then
        mockMvc.perform(get("/booking/seats").param("showId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("booking/select-seats"))
                .andExpect(model().attributeExists("show", "availableSeats", "rows"))
                .andExpect(model().attributeExists("dateFormatter"));
    }

//    @Test
//    @WithMockUser
//    void reviewBooking_WithValidData_ShouldReturnReviewPage() throws Exception {
//        // Given
//        Movie movie = createTestMovie();
//        Theater theater = createTestTheater();
//        Show show = createTestShow(movie, theater);
//        show.setShowSeatList(createTestShowSeats(show));
//
//        when(showRepository.findById(1)).thenReturn(Optional.of(show));
//
//        // When & Then
//        mockMvc.perform(post("/booking/review")
//                        .with(csrf())
//                        .param("showId", "1")
//                        .param("selectedSeats", "A1", "A2")) // Pass as multiple params
//                .andExpect(status().isOk())
//                .andExpect(view().name("booking/review-booking"))
//                .andExpect(model().attributeExists("show", "selectedSeats", "totalAmount"));
//    }
//
//    @Test
//    @WithMockUser
//    void processPayment_WithValidData_ShouldRedirectToPayment() throws Exception {
//        // When & Then - Use URL encoding for the expected URL
//        mockMvc.perform(post("/booking/payment")
//                        .with(csrf())
//                        .param("showId", "1")
//                        .param("totalAmount", "550.0")
//                        .param("selectedSeats", "A1", "A2")) // Pass as multiple params
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/payment?showId=1&totalAmount=550.0&selectedSeats=A1,A2"));
//    }
//
//    @Test
//    @WithMockUser
//    void processPayment_WithEmptySeats_ShouldRedirectWithError() throws Exception {
//        // When & Then - Test with empty seats list
//        mockMvc.perform(post("/booking/payment")
//                        .with(csrf())
//                        .param("showId", "1")
//                        .param("totalAmount", "550.0")
//                        .param("selectedSeats", "")) // Empty seats
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/booking/review?showId=1&error=true"));
//    }
//
//    @Test
//    @WithMockUser
//    void processPayment_WithNullSeats_ShouldRedirectWithError() throws Exception {
//        // When & Then - Test with no seats parameter at all
//        mockMvc.perform(post("/booking/payment")
//                        .with(csrf())
//                        .param("showId", "1")
//                        .param("totalAmount", "550.0"))
//                // No selectedSeats parameter
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/booking/review?showId=1&error=true"));
//    }

    @Test
    @WithMockUser
    void selectShowTime_WithInvalidTheater_ShouldRedirectToTheater() throws Exception {
        // Given
        Movie movie = createTestMovie();
        when(movieRepository.findById(1)).thenReturn(Optional.of(movie));
        when(theaterRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/booking/showtime")
                        .param("movieId", "1")
                        .param("theaterId", "999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/booking/theater?movieId=1"));
    }

    @Test
    @WithMockUser
    void selectSeats_WithInvalidShow_ShouldRedirectToMovies() throws Exception {
        // Given
        when(showRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/booking/seats").param("showId", "999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/movies"));
    }

    @Test
    @WithMockUser
    void reviewBooking_WithInvalidShow_ShouldRedirectToMovies() throws Exception {
        // Given
        when(showRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(post("/booking/review")
                        .with(csrf())
                        .param("showId", "999")
                        .param("selectedSeats", "A1", "A2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/movies"));
    }

    @Test
    @WithMockUser
    void reviewBooking_WithEmptySeats_ShouldRedirectToMovies() throws Exception {
        // Given
        Movie movie = createTestMovie();
        Theater theater = createTestTheater();
        Show show = createTestShow(movie, theater);

        when(showRepository.findById(1)).thenReturn(Optional.of(show));

        // When & Then - Empty seats list
        mockMvc.perform(post("/booking/review")
                        .with(csrf())
                        .param("showId", "1")
                        .param("selectedSeats", "")) // Empty seats
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/movies"));
    }

    private Movie createTestMovie() {
        return Movie.builder()
                .id(1)
                .movieName("Test Movie")
                .genre(Genre.ACTION)
                .language(Language.ENGLISH)
                .duration(120)
                .rating(4.5)
                .releaseDate(Date.valueOf(LocalDate.now().plusDays(30)))
                .build();
    }

    private Theater createTestTheater() {
        return Theater.builder()
                .id(1)
                .name("Test Theater")
                .address("Test Address")
                .build();
    }

    private Show createTestShow(Movie movie, Theater theater) {
        return Show.builder()
                .id(1)
                .time(Time.valueOf(LocalTime.of(18, 0)))
                .date(Date.valueOf(LocalDate.now().plusDays(1)))
                .movie(movie)
                .theater(theater)
                .build();
    }

    private List<ShowSeat> createTestShowSeats(Show show) {
        ShowSeat seat1 = ShowSeat.builder()
                .seatNo("A1")
                .seatType(SeatType.CLASSIC)
                .price(200)
                .isAvailable(true)
                .show(show)
                .build();

        ShowSeat seat2 = ShowSeat.builder()
                .seatNo("A2")
                .seatType(SeatType.PREMIUM)
                .price(350)
                .isAvailable(true)
                .show(show)
                .build();

        ShowSeat seat3 = ShowSeat.builder()
                .seatNo("B1")
                .seatType(SeatType.CLASSIC)
                .price(200)
                .isAvailable(false) // Occupied seat
                .show(show)
                .build();

        return Arrays.asList(seat1, seat2, seat3);
    }
}






//package com.jts.movie.controllers;
//
//import com.jts.movie.entity.*;
//import com.jts.movie.enums.Genre;
//import com.jts.movie.enums.Language;
//import com.jts.movie.enums.SeatType;
//import com.jts.movie.repository.MovieRepository;
//import com.jts.movie.repository.ShowRepository;
//import com.jts.movie.repository.TheaterRepository;
//import com.jts.movie.repository.ShowSeatRepository;
//import com.jts.movie.services.BookingService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.sql.Date;
//import java.sql.Time;
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.mockito.ArgumentMatchers.anyInt;
//import static org.mockito.Mockito.when;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(BookingController.class)
//class BookingControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private MovieRepository movieRepository;
//
//    @MockBean
//    private TheaterRepository theaterRepository;
//
//    @MockBean
//    private ShowRepository showRepository;
//
//    @MockBean
//    private ShowSeatRepository showSeatRepository;
//
//    @MockBean
//    private BookingService bookingService;
//
//    @Test
//    @WithMockUser
//    void showBookingPage_WithValidMovie_ShouldReturnTheaterSelection() throws Exception {
//        // Given
//        Movie movie = createTestMovie();
//        when(movieRepository.findById(1)).thenReturn(Optional.of(movie));
//        when(theaterRepository.findTheatersByMovieId(1)).thenReturn(Arrays.asList(createTestTheater()));
//
//        // When & Then
//        mockMvc.perform(get("/booking").param("movieId", "1"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("booking/select-theater"))
//                .andExpect(model().attributeExists("movie", "theaters"))
//                .andExpect(model().attribute("pageTitle", "Book Tickets - " + movie.getMovieName()));
//    }
//
//    @Test
//    @WithMockUser
//    void showBookingPage_WithInvalidMovie_ShouldRedirectToMovies() throws Exception {
//        // Given
//        when(movieRepository.findById(999)).thenReturn(Optional.empty());
//
//        // When & Then
//        mockMvc.perform(get("/booking").param("movieId", "999"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/movies"));
//    }
//
//    @Test
//    @WithMockUser
//    void selectTheater_WithValidData_ShouldReturnTheaterPage() throws Exception {
//        // Given
//        Movie movie = createTestMovie();
//        when(movieRepository.findById(1)).thenReturn(Optional.of(movie));
//        when(theaterRepository.findTheatersByMovieId(1)).thenReturn(Arrays.asList(createTestTheater()));
//
//        // When & Then
//        mockMvc.perform(get("/booking/theater").param("movieId", "1"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("booking/select-theater"))
//                .andExpect(model().attributeExists("movie", "theaters"));
//    }
//
//    @Test
//    @WithMockUser
//    void selectShowTime_WithValidData_ShouldReturnShowTimePage() throws Exception {
//        // Given
//        Movie movie = createTestMovie();
//        Theater theater = createTestTheater();
//        Show show = createTestShow(movie, theater);
//
//        when(movieRepository.findById(1)).thenReturn(Optional.of(movie));
//        when(theaterRepository.findById(1)).thenReturn(Optional.of(theater));
//        when(showRepository.getAllShowsOfMovie(1)).thenReturn(Arrays.asList(show));
//
//        // When & Then
//        mockMvc.perform(get("/booking/showtime")
//                        .param("movieId", "1")
//                        .param("theaterId", "1"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("booking/select-showtime"))
//                .andExpect(model().attributeExists("movie", "theater", "shows"))
//                .andExpect(model().attributeExists("timeFormatter"));
//    }
//
//    @Test
//    @WithMockUser
//    void selectSeats_WithValidShow_ShouldReturnSeatsPage() throws Exception {
//        // Given
//        Movie movie = createTestMovie();
//        Theater theater = createTestTheater();
//        Show show = createTestShow(movie, theater);
//        show.setShowSeatList(createTestShowSeats(show));
//
//        when(showRepository.findById(1)).thenReturn(Optional.of(show));
//
//        // When & Then
//        mockMvc.perform(get("/booking/seats").param("showId", "1"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("booking/select-seats"))
//                .andExpect(model().attributeExists("show", "availableSeats", "rows"))
//                .andExpect(model().attributeExists("dateFormatter"));
//    }
//
//    @Test
//    @WithMockUser
//    void reviewBooking_WithValidData_ShouldReturnReviewPage() throws Exception {
//        // Given
//        Movie movie = createTestMovie();
//        Theater theater = createTestTheater();
//        Show show = createTestShow(movie, theater);
//
//        when(showRepository.findById(1)).thenReturn(Optional.of(show));
//
//        // When & Then
//        mockMvc.perform(post("/booking/review")
//                        .with(csrf())
//                        .param("showId", "1")
//                        .param("selectedSeats", "A1,A2"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("booking/review-booking"))
//                .andExpect(model().attributeExists("show", "selectedSeats", "totalAmount"));
//    }
//
//    @Test
//    @WithMockUser
//    void processPayment_WithValidData_ShouldRedirectToPayment() throws Exception {
//        // When & Then
//        mockMvc.perform(post("/booking/payment")
//                        .with(csrf())
//                        .param("showId", "1")
//                        .param("totalAmount", "550.0")
//                        .param("selectedSeats", "A1,A2"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/payment?showId=1&totalAmount=550.0&selectedSeats=A1,A2"));
//    }
//
//    @Test
//    @WithMockUser
//    void processPayment_WithError_ShouldRedirectWithError() throws Exception {
//        // When & Then
//        mockMvc.perform(post("/booking/payment")
//                        .with(csrf())
//                        .param("showId", "1")
//                        .param("totalAmount", "550.0")
//                        .param("selectedSeats", "")) // Empty seats should cause error
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/booking/review?showId=1&error=true"));
//    }
//
//    private Movie createTestMovie() {
//        return Movie.builder()
//                .id(1)
//                .movieName("Test Movie")
//                .genre(Genre.ACTION)
//                .language(Language.ENGLISH)
//                .duration(120)
//                .rating(4.5)
//                .releaseDate(Date.valueOf(LocalDate.now().plusDays(30)))
//                .build();
//    }
//
//    private Theater createTestTheater() {
//        return Theater.builder()
//                .id(1)
//                .name("Test Theater")
//                .address("Test Address")
//                .build();
//    }
//
//    private Show createTestShow(Movie movie, Theater theater) {
//        return Show.builder()
//                .id(1)
//                .time(Time.valueOf(LocalTime.of(18, 0)))
//                .date(Date.valueOf(LocalDate.now().plusDays(1)))
//                .movie(movie)
//                .theater(theater)
//                .build();
//    }
//
//    private List<ShowSeat> createTestShowSeats(Show show) {
//        ShowSeat seat1 = ShowSeat.builder()
//                .seatNo("A1")
//                .seatType(SeatType.CLASSIC)
//                .price(200)
//                .isAvailable(true)
//                .show(show)
//                .build();
//
//        ShowSeat seat2 = ShowSeat.builder()
//                .seatNo("A2")
//                .seatType(SeatType.PREMIUM)
//                .price(350)
//                .isAvailable(true)
//                .show(show)
//                .build();
//
//        return Arrays.asList(seat1, seat2);
//    }
//}