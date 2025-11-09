package com.jts.movie_ticket_booking_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Date;
import java.time.LocalDate;

@Entity
@Table(name = "TICKETS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer ticketId;

    @Column(unique = true, nullable = false)
    private String bookingId;

    @Column(nullable = false)
    private Integer totalTicketsPrice;

    @Column
	private String bookedSeats;

	@CreationTimestamp
    @Column(name = "booked_at")
	private Date bookedAt;

    @Column(name = "user_email_id")
    private String userEmailId;

    @ManyToOne
    @JoinColumn
	private Show show;

    // FIX: Use consistent naming for user_id column
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // Use the same name as database column
    private User user;

    // Add this method if you need to access it as bookingDate in business logic
    public Date getBookingDate() {
        return this.bookedAt;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookedAt = bookingDate;
    }

//	@ManyToOne
//	@JoinColumn
//	private User user;

}
