package com.jts.movie_ticket_booking_system.repository;

import com.jts.movie_ticket_booking_system.entity.Ticket;
import com.jts.movie_ticket_booking_system.entity.User;
import com.jts.movie_ticket_booking_system.enums.Gender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
//@TestPropertySource(locations = "classpath:application-test.properties")
class TicketRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TicketRepository ticketRepository;

    @Test
    void findByBookingId_WhenTicketExists_ShouldReturnTicket() {
        // Given
        Ticket ticket = createTestTicket("TKT12345");
        entityManager.persistAndFlush(ticket);

        // When
        Optional<Ticket> found = ticketRepository.findByBookingId("TKT12345");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getBookingId()).isEqualTo("TKT12345");
        assertThat(found.get().getTotalTicketsPrice()).isEqualTo(550);
    }

    @Test
    void findByBookingId_WhenTicketNotExists_ShouldReturnEmpty() {
        // When
        Optional<Ticket> found = ticketRepository.findByBookingId("NONEXISTENT");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void findByUserId_ShouldReturnUserTickets() {
        // Given
        User user = createTestUser();
        Ticket ticket1 = createTestTicket("TKT001");
        ticket1.setUser(user);
        Ticket ticket2 = createTestTicket("TKT002");
        ticket2.setUser(user);

        entityManager.persist(user);
        entityManager.persist(ticket1);
        entityManager.persist(ticket2);
        entityManager.flush();

        // When
        List<Ticket> tickets = ticketRepository.findByUserId(user.getId());

        // Then
        assertThat(tickets).hasSize(2);
        assertThat(tickets).extracting(Ticket::getBookingId)
                .containsExactlyInAnyOrder("TKT001", "TKT002");
    }

    @Test
    void findByUser_IdOrderByBookedAtDesc_ShouldReturnOrderedTickets() {
        // Given
        User user = createTestUser();
        Ticket ticket1 = createTestTicket("TKT001");
        ticket1.setUser(user);
        Ticket ticket2 = createTestTicket("TKT002");
        ticket2.setUser(user);

        entityManager.persist(user);
        entityManager.persist(ticket1);
        entityManager.persist(ticket2);
        entityManager.flush();

        // When
        List<Ticket> tickets = ticketRepository.findByUser_IdOrderByBookedAtDesc(user.getId());

        // Then
        assertThat(tickets).hasSize(2);
        // Should be ordered by bookedAt descending (latest first)
    }

    @Test
    void saveTicket_ShouldPersistCorrectly() {
        // Given
        Ticket ticket = createTestTicket("NEWTKT001");

        // When
        Ticket saved = ticketRepository.save(ticket);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getTicketId()).isNotNull();
        assertThat(saved.getBookingId()).isEqualTo("NEWTKT001");
        assertThat(saved.getTotalTicketsPrice()).isEqualTo(550);
        assertThat(saved.getBookedSeats()).isEqualTo("A1,A2");
    }

    private Ticket createTestTicket(String bookingId) {
        return Ticket.builder()
                .bookingId(bookingId)
                .totalTicketsPrice(550)
                .bookedSeats("A1,A2")
                .bookedAt(new java.sql.Date(System.currentTimeMillis()))
                .userEmailId("test@example.com")
                .build();
    }

    private User createTestUser() {
        return User.builder()
                .name("Test User")
                .emailId("test@example.com")
                .password("encodedPassword")
                .roles("ROLE_USER")
                .gender(Gender.MALE)
                .build();
    }
}