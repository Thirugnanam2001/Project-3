package com.jts.movie_ticket_booking_system.services;

import com.jts.movie_ticket_booking_system.entity.*;
import com.jts.movie_ticket_booking_system.enums.SeatType;
import com.jts.movie_ticket_booking_system.exceptions.SeatsNotAvailable;
import com.jts.movie_ticket_booking_system.exceptions.ShowDoesNotExists;
import com.jts.movie_ticket_booking_system.exceptions.UserDoesNotExists;
import com.jts.movie_ticket_booking_system.repository.ShowRepository;
import com.jts.movie_ticket_booking_system.repository.TicketRepository;
import com.jts.movie_ticket_booking_system.repository.UserRepository;
import com.jts.movie_ticket_booking_system.request.TicketRequest;
import com.jts.movie_ticket_booking_system.response.TicketResponse;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private ShowRepository showRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TicketService ticketService;

    private TicketRequest ticketRequest;
    private Show show;
    private User user;
    private Movie movie;
    private Theater theater;

    @BeforeEach
    void setUp() {
        movie = Movie.builder()
                .movieName("Test Movie")
                .duration(120)
                .build();

        theater = Theater.builder()
                .name("Test Theater")
                .address("Test Address")
                .build();

        show = Show.builder()
                .id(1)
                .time(Time.valueOf(LocalTime.of(18, 0)))
                .date(Date.valueOf(LocalDate.now().plusDays(1)))
                .movie(movie)
                .theater(theater)
                .build();

        user = User.builder()
                .id(1)
                .name("Test User")
                .emailId("test@example.com")
                .build();

        // Create show seats
        ShowSeat seat1 = ShowSeat.builder()
                .seatNo("A1")
                .seatType(SeatType.CLASSIC)
                .price(200)
                .isAvailable(true)
                .build();

        ShowSeat seat2 = ShowSeat.builder()
                .seatNo("A2")
                .seatType(SeatType.PREMIUM)
                .price(350)
                .isAvailable(true)
                .build();

        show.setShowSeatList(Arrays.asList(seat1, seat2));

        ticketRequest = new TicketRequest(1, 1, Arrays.asList("A1", "A2"));
    }

    @Test
    void ticketBooking_WhenValidRequest_ShouldReturnTicketResponse() {
        // Given
        when(showRepository.findById(1)).thenReturn(Optional.of(show));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        // Mock the saved ticket with bookedAt date
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> {
            Ticket ticket = invocation.getArgument(0);
            ticket.setTicketId(1);
            // Set bookedAt date to avoid null in convertor
            ticket.setBookedAt(new Date(System.currentTimeMillis()));
            return ticket;
        });

        // When
        TicketResponse result = ticketService.ticketBooking(ticketRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getMovieName()).isEqualTo("Test Movie");
        assertThat(result.getTheaterName()).isEqualTo("Test Theater");
        assertThat(result.getTotalAmount()).isEqualTo(550.0); // 200 + 350
        assertThat(result.getSeats()).isEqualTo("A1,A2");
        assertThat(result.getBookingDate()).isNotNull(); // Should not be null

        verify(showRepository).findById(1);
        verify(userRepository).findById(1);
        verify(ticketRepository).save(any(Ticket.class));
    }

    @Test
    void ticketBooking_WhenShowNotExists_ShouldThrowException() {
        // Given
        when(showRepository.findById(1)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> ticketService.ticketBooking(ticketRequest))
                .isInstanceOf(ShowDoesNotExists.class);

        verify(userRepository, never()).findById(anyInt());
    }

    @Test
    void ticketBooking_WhenUserNotExists_ShouldThrowException() {
        // Given
        when(showRepository.findById(1)).thenReturn(Optional.of(show));
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> ticketService.ticketBooking(ticketRequest))
                .isInstanceOf(UserDoesNotExists.class);
    }

    @Test
    void ticketBooking_WhenSeatsNotAvailable_ShouldThrowException() {
        // Given
        // Make seats unavailable
        show.getShowSeatList().get(0).setIsAvailable(false);
        when(showRepository.findById(1)).thenReturn(Optional.of(show));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        // When & Then
        assertThatThrownBy(() -> ticketService.ticketBooking(ticketRequest))
                .isInstanceOf(SeatsNotAvailable.class);
    }

    @Test
    void getUserTickets_WhenUserHasTickets_ShouldReturnTicketList() {
        // Given
        Ticket ticket = Ticket.builder()
                .bookingId("TKT123")
                .totalTicketsPrice(550)
                .bookedSeats("A1,A2")
                .bookedAt(new Date(System.currentTimeMillis())) // Set bookedAt
                .show(show)
                .user(user)
                .build();

        user.setTicketList(Arrays.asList(ticket));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        // When
        List<TicketResponse> result = ticketService.getUserTickets(1);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBookingId()).isEqualTo("TKT123");
        assertThat(result.get(0).getBookingDate()).isNotNull();
    }

    @Test
    void getUserTickets_WhenUserHasNoTickets_ShouldReturnEmptyList() {
        // Given
        user.setTicketList(Arrays.asList());
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        // When
        List<TicketResponse> result = ticketService.getUserTickets(1);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void getUserTickets_WhenUserNotExists_ShouldReturnEmptyList() {
        // Given
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        // When
        List<TicketResponse> result = ticketService.getUserTickets(999);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void ticketBooking_WithNullBookedAt_ShouldStillWork() {
        // Given
        when(showRepository.findById(1)).thenReturn(Optional.of(show));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        // Mock ticket with null bookedAt to test the fallback
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> {
            Ticket ticket = invocation.getArgument(0);
            ticket.setTicketId(1);
            // Don't set bookedAt to test null handling
            return ticket;
        });

        // When
        TicketResponse result = ticketService.ticketBooking(ticketRequest);

        // Then - Should not throw exception and should use current date as fallback
        assertThat(result).isNotNull();
        assertThat(result.getBookingDate()).isNotNull(); // Should use current date as fallback
        assertThat(result.getMovieName()).isEqualTo("Test Movie");
    }
}



//package com.jts.movie.services;
//
//import com.jts.movie.entity.*;
//import com.jts.movie.enums.SeatType;
//import com.jts.movie.exceptions.SeatsNotAvailable;
//import com.jts.movie.exceptions.ShowDoesNotExists;
//import com.jts.movie.exceptions.UserDoesNotExists;
//import com.jts.movie.repository.ShowRepository;
//import com.jts.movie.repository.TicketRepository;
//import com.jts.movie.repository.UserRepository;
//import com.jts.movie.request.TicketRequest;
//import com.jts.movie.response.TicketResponse;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.sql.Date;
//import java.sql.Time;
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class TicketServiceTest {
//
//    @Mock
//    private TicketRepository ticketRepository;
//
//    @Mock
//    private ShowRepository showRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @InjectMocks
//    private TicketService ticketService;
//
//    private TicketRequest ticketRequest;
//    private Show show;
//    private User user;
//    private Movie movie;
//    private Theater theater;
//
//    @BeforeEach
//    void setUp() {
//        movie = Movie.builder()
//                .movieName("Test Movie")
//                .duration(120)
//                .build();
//
//        theater = Theater.builder()
//                .name("Test Theater")
//                .address("Test Address")
//                .build();
//
//        show = Show.builder()
//                .Id(1)
//                .time(Time.valueOf(LocalTime.of(18, 0)))
//                .date(Date.valueOf(LocalDate.now().plusDays(1)))
//                .movie(movie)
//                .theater(theater)
//                .build();
//
//        user = User.builder()
//                .id(1)
//                .name("Test User")
//                .emailId("test@example.com")
//                .build();
//
//        // Create show seats
//        ShowSeat seat1 = ShowSeat.builder()
//                .seatNo("A1")
//                .seatType(SeatType.CLASSIC)
//                .price(200)
//                .isAvailable(true)
//                .build();
//
//        ShowSeat seat2 = ShowSeat.builder()
//                .seatNo("A2")
//                .seatType(SeatType.PREMIUM)
//                .price(350)
//                .isAvailable(true)
//                .build();
//
//        show.setShowSeatList(Arrays.asList(seat1, seat2));
//
//        ticketRequest = new TicketRequest(1, 1, Arrays.asList("A1", "A2"));
//    }
//
//    @Test
//    void ticketBooking_WhenValidRequest_ShouldReturnTicketResponse() {
//        // Given
//        when(showRepository.findById(1)).thenReturn(Optional.of(show));
//        when(userRepository.findById(1)).thenReturn(Optional.of(user));
//        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> {
//            Ticket ticket = invocation.getArgument(0);
//            ticket.setTicketId(1);
//            return ticket;
//        });
//
//        // When
//        TicketResponse result = ticketService.ticketBooking(ticketRequest);
//
//        // Then
//        assertThat(result).isNotNull();
//        assertThat(result.getMovieName()).isEqualTo("Test Movie");
//        assertThat(result.getTheaterName()).isEqualTo("Test Theater");
//        assertThat(result.getTotalAmount()).isEqualTo(550.0); // 200 + 350
//        assertThat(result.getSeats()).isEqualTo("A1,A2");
//
//        verify(showRepository).findById(1);
//        verify(userRepository).findById(1);
//        verify(ticketRepository).save(any(Ticket.class));
//    }
//
//    @Test
//    void ticketBooking_WhenShowNotExists_ShouldThrowException() {
//        // Given
//        when(showRepository.findById(1)).thenReturn(Optional.empty());
//
//        // When & Then
//        assertThatThrownBy(() -> ticketService.ticketBooking(ticketRequest))
//                .isInstanceOf(ShowDoesNotExists.class);
//
//        verify(userRepository, never()).findById(anyInt());
//    }
//
//    @Test
//    void ticketBooking_WhenUserNotExists_ShouldThrowException() {
//        // Given
//        when(showRepository.findById(1)).thenReturn(Optional.of(show));
//        when(userRepository.findById(1)).thenReturn(Optional.empty());
//
//        // When & Then
//        assertThatThrownBy(() -> ticketService.ticketBooking(ticketRequest))
//                .isInstanceOf(UserDoesNotExists.class);
//    }
//
//    @Test
//    void ticketBooking_WhenSeatsNotAvailable_ShouldThrowException() {
//        // Given
//        // Make seats unavailable
//        show.getShowSeatList().get(0).setIsAvailable(false);
//        when(showRepository.findById(1)).thenReturn(Optional.of(show));
//        when(userRepository.findById(1)).thenReturn(Optional.of(user));
//
//        // When & Then
//        assertThatThrownBy(() -> ticketService.ticketBooking(ticketRequest))
//                .isInstanceOf(SeatsNotAvailable.class);
//    }
//
//    @Test
//    void getUserTickets_WhenUserHasTickets_ShouldReturnTicketList() {
//        // Given
//        Ticket ticket = Ticket.builder()
//                .bookingId("TKT123")
//                .totalTicketsPrice(550)
//                .bookedSeats("A1,A2")
//                .show(show)
//                .user(user)
//                .build();
//
//        user.setTicketList(Arrays.asList(ticket));
//        when(userRepository.findById(1)).thenReturn(Optional.of(user));
//
//        // When
//        List<TicketResponse> result = ticketService.getUserTickets(1);
//
//        // Then
//        assertThat(result).hasSize(1);
//        assertThat(result.get(0).getBookingId()).isEqualTo("TKT123");
//    }
//
//    @Test
//    void getUserTickets_WhenUserHasNoTickets_ShouldReturnEmptyList() {
//        // Given
//        user.setTicketList(Arrays.asList());
//        when(userRepository.findById(1)).thenReturn(Optional.of(user));
//
//        // When
//        List<TicketResponse> result = ticketService.getUserTickets(1);
//
//        // Then
//        assertThat(result).isEmpty();
//    }
//
//    @Test
//    void getUserTickets_WhenUserNotExists_ShouldReturnEmptyList() {
//        // Given
//        when(userRepository.findById(999)).thenReturn(Optional.empty());
//
//        // When
//        List<TicketResponse> result = ticketService.getUserTickets(999);
//
//        // Then
//        assertThat(result).isEmpty();
//    }
//}