package com.jts.movie_ticket_booking_system.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.jts.movie_ticket_booking_system.entity.*;
import com.jts.movie_ticket_booking_system.repository.BookingRepository;
import com.jts.movie_ticket_booking_system.response.TicketResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jts.movie_ticket_booking_system.convertor.TicketConvertor;
import com.jts.movie_ticket_booking_system.exceptions.SeatsNotAvailable;
import com.jts.movie_ticket_booking_system.exceptions.ShowDoesNotExists;
import com.jts.movie_ticket_booking_system.exceptions.UserDoesNotExists;
import com.jts.movie_ticket_booking_system.repository.ShowRepository;
import com.jts.movie_ticket_booking_system.repository.TicketRepository;
import com.jts.movie_ticket_booking_system.repository.UserRepository;
import com.jts.movie_ticket_booking_system.request.TicketRequest;


@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookingRepository bookingRepository;


    public TicketResponse ticketBooking(TicketRequest ticketRequest) {
        Optional<Show> showOpt = showRepository.findById(ticketRequest.getShowId());

        if (showOpt.isEmpty()) {
            throw new ShowDoesNotExists();
        }

        Optional<User> userOpt = userRepository.findById(ticketRequest.getUserId());

        if (userOpt.isEmpty()) {
            throw new UserDoesNotExists();
        }

        User user = userOpt.get();
        Show show = showOpt.get();

        Boolean isSeatAvailable = isSeatAvailable(show.getShowSeatList(), ticketRequest.getRequestSeats());

        if (!isSeatAvailable) {
            throw new SeatsNotAvailable();
        }

        // Calculate price and assign seats
        Integer totalPrice = getPriceAndAssignSeats(show.getShowSeatList(), ticketRequest.getRequestSeats());

        String seats = listToString(ticketRequest.getRequestSeats());

        Ticket ticket = Ticket.builder()
                .totalTicketsPrice(totalPrice)
                .bookedSeats(seats)
                .user(user)
                .show(show)
                .bookingId(generateBookingId())
                .build();

        ticket = ticketRepository.save(ticket);

        // Update user and show entities
        user.getTicketList().add(ticket);
        show.getTicketList().add(ticket);
        userRepository.save(user);
        showRepository.save(show);

        // Use the convertor
        return TicketConvertor.returnTicket(show, ticket);
    }


    public List<TicketResponse> getUserTickets(Integer userId) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return new ArrayList<>();
            }

            User user = userOpt.get();
            List<Ticket> userTickets = user.getTicketList() != null ?
                    user.getTicketList() : new ArrayList<>();

            if (userTickets.isEmpty()) {
                return new ArrayList<>();
            }
//            if (user.getTicketList() == null || user.getTicketList().isEmpty()) {
//                return new ArrayList<>();
//            }

//            return userTickets.stream()
//                    .map(ticket -> TicketConvertor.returnTicket(ticket.getShow(), ticket))
//                    .sorted((t1, t2) -> {
//                        if (t1.getBookingDate() == null || t2.getBookingDate() == null) {
//                            return 0;
//                        }
//                        return t2.getBookingDate().compareTo(t1.getBookingDate());
//                    })
//                    .toList();
//        } catch (Exception e) {
//            System.out.println("Error getting user tickets for user ID " + userId + ": " + e.getMessage());
//            return new ArrayList<>();
//        }

            return user.getTicketList().stream()
                    .map(ticket -> TicketConvertor.returnTicket(ticket.getShow(), ticket))
                    .sorted((t1, t2) -> {
                        // Use bookedAt instead of bookingDate
                        if (t1.getBookingDate() == null || t2.getBookingDate() == null) {
                            return 0;
                        }
                        return t2.getBookingDate().compareTo(t1.getBookingDate());
                    })
                    .toList();
        } catch (Exception e) {
            System.out.println("Error getting user tickets for user ID " + userId + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

//    public List<TicketResponse> getUserTickets(Integer userId) {
//        try {
//            Optional<User> userOpt = userRepository.findById(userId);
//            if (userOpt.isEmpty()) {
//                // Return empty list instead of throwing exception
//                return new ArrayList<>();
//            }
//
//            User user = userOpt.get();
//            if (user.getTicketList() == null || user.getTicketList().isEmpty()) {
//                return new ArrayList<>();
//            }
//
//            return user.getTicketList().stream()
//                    .map(ticket -> TicketConvertor.returnTicket(ticket.getShow(), ticket))
//                    .sorted((t1, t2) -> {
//                        if (t1.getBookingDate() == null || t2.getBookingDate() == null) {
//                            return 0;
//                        }
//                        return t2.getBookingDate().compareTo(t1.getBookingDate()); // Latest first
//                    })
//                    .toList();
//        } catch (Exception e) {
//            System.out.println("Error getting user tickets for user ID " + userId + ": " + e.getMessage());
//            return new ArrayList<>();
//        }
//    }


    private Boolean isSeatAvailable(List<ShowSeat> showSeatList, List<String> requestSeats) {
        for (ShowSeat showSeat : showSeatList) {
            String seatNo = showSeat.getSeatNo();

            if (requestSeats.contains(seatNo) && !showSeat.getIsAvailable()) {
                return false;
            }
        }

        return true;
    }

    private Integer getPriceAndAssignSeats(List<ShowSeat> showSeatList, List<String> requestSeats) {
        Integer totalAmount = 0;

        for (ShowSeat showSeat : showSeatList) {
            if (requestSeats.contains(showSeat.getSeatNo())) {
                totalAmount += showSeat.getPrice();
                showSeat.setIsAvailable(Boolean.FALSE);
            }
        }

        return totalAmount;
    }

    private String listToString(List<String> requestSeats) {
        return String.join(",", requestSeats);
    }

    private String generateBookingId() {
        return "TKT" + System.currentTimeMillis() + (int) (Math.random() * 1000);
    }
}

//    private void releaseSeats(List<ShowSeat> showSeatList, String bookedSeats) {
//        List<String> seats = List.of(bookedSeats.split(","));
//
//        for (ShowSeat showSeat : showSeatList) {
//            if (seats.contains(showSeat.getSeatNo())) {
//                showSeat.setIsAvailable(Boolean.TRUE);
//            }
//        }
//    }

//    public List<TicketResponse> getUserTickets(Integer userId) {
//        try {
//            Optional<User> userOpt = userRepository.findById(userId);
//            if (userOpt.isEmpty()) {
//                // Return empty list instead of throwing exception
//                return new ArrayList<>();
//            }
//
//            User user = userOpt.get();
//            if (user.getTicketList() == null || user.getTicketList().isEmpty()) {
//                return new ArrayList<>();
//            }
//
//            return user.getTicketList().stream()
//                    .map(ticket -> TicketConvertor.returnTicket(ticket.getShow(), ticket))
//                    .sorted((t1, t2) -> {
//                        if (t1.getBookingDate() == null || t2.getBookingDate() == null) {
//                            return 0;
//                        }
//                        return t2.getBookingDate().compareTo(t1.getBookingDate());
//                    })
//                    .toList();
//        } catch (Exception e) {
//            System.out.println("Error getting user tickets: " + e.getMessage());
//            return new ArrayList<>();
//        }
//    }

//    public TicketResponse getTicketById(Integer ticketId) {
//        Optional<Ticket> ticketOpt = ticketRepository.findById(ticketId);
//        if (ticketOpt.isEmpty()) {
//            throw new RuntimeException("Ticket not found with ID: " + ticketId);
//        }
//
//        Ticket ticket = ticketOpt.get();
//        return TicketConvertor.returnTicket(ticket.getShow(), ticket);
//    }

//    public TicketResponse getTicketByBookingId(String bookingId) {
//        Optional<Ticket> ticketOpt = ticketRepository.findAll().stream()
//                .filter(ticket -> bookingId.equals(ticket.getBookingId()))
//                .findFirst();
//
//        if (ticketOpt.isEmpty()) {
//            throw new RuntimeException("Ticket not found with booking ID: " + bookingId);
//        }
//
//        Ticket ticket = ticketOpt.get();
//        return TicketConvertor.returnTicket(ticket.getShow(), ticket);
//    }

//    public boolean cancelTicket(Integer ticketId) {
//        Optional<Ticket> ticketOpt = ticketRepository.findById(ticketId);
//        if (ticketOpt.isEmpty()) {
//            return false;
//        }
//
//        Ticket ticket = ticketOpt.get();
//
//        // Release the seats
//        releaseSeats(ticket.getShow().getShowSeatList(), ticket.getBookedSeats());
//
//        // Remove ticket from user and show
//        ticket.getUser().getTicketList().remove(ticket);
//        ticket.getShow().getTicketList().remove(ticket);
//
//        // Delete the ticket
//        ticketRepository.delete(ticket);
//
//        return true;
//    }


//    // Additional utility methods
//    public double calculateTotalAmount(List<String> selectedSeats, Show show) {
//        double total = 0;
//        for (String seatNo : selectedSeats) {
//            Optional<ShowSeat> seatOpt = show.getShowSeatList().stream()
//                    .filter(s -> s.getSeatNo().equals(seatNo))
//                    .findFirst();
//
//            if (seatOpt.isPresent()) {
//                total += seatOpt.get().getPrice();
//            }
//        }
//        return total;
//    }


//
//    public boolean validateSeatsAvailability(Show show, List<String> selectedSeats) {
//        return isSeatAvailable(show.getShowSeatList(), selectedSeats);
//    }
//
//    public List<TicketResponse> getAllTickets() {
//        return ticketRepository.findAll().stream()
//                .map(ticket -> TicketConvertor.returnTicket(ticket.getShow(), ticket))
//                .toList();
//    }

//    public List<TicketResponse> getUserTickets(Integer userId) {
//        try {
//            // This should return only bookings for the specific user
//            List<Object[]> bookingData = bookingRepository.findBookingsByUserId(userId);
//
//            return bookingData.stream()
//                    .map(this::mapToTicketResponse)
//                    .collect(Collectors.toList());
//
//        } catch (Exception e) {
//            System.out.println("Error getting user tickets for user ID " + userId + ": " + e.getMessage());
//            throw new RuntimeException("Failed to fetch user tickets", e);
//        }
//    }

//    private TicketResponse mapToTicketResponse(Object[] booking) {
//        // Your mapping logic here
//        TicketResponse response;
//        response = new TicketResponse();
//        // Set properties from booking array
//        return response;
//    }



