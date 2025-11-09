package com.jts.movie_ticket_booking_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.Date;

@Entity
@Table(name = "bookings")
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "show_time_id")
    private ShowTime showTime;

    @Column(name = "selected_seats")
    private String selectedSeats; // Store as comma-separated string: "A1,A2,B3"

    @Column(name = "total_amount")
    private Double totalAmount;

    @Column(name = "booking_status")
    private String bookingStatus; // CONFIRMED, CANCELLED

    @Column(name = "booking_date")
    private Date bookingDate;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ShowTime getShowTime() {
        return showTime;
    }

    public void setShowTime(ShowTime showTime) {
        this.showTime = showTime;
    }

    public String getSelectedSeats() {
        return selectedSeats;
    }

    public void setSelectedSeats(String selectedSeats) {
        this.selectedSeats = selectedSeats;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public Date getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    // Helper methods
    @Transient
    public String[] getSeatsArray() {
        if (selectedSeats != null && !selectedSeats.isEmpty()) {
            return selectedSeats.split(",");
        }
        return new String[0];
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", user=" + (user != null ? user.getEmailId() : "null") +
                ", showTime=" + (showTime != null ? showTime.getId() : "null") +
                ", selectedSeats='" + selectedSeats + '\'' +
                ", totalAmount=" + totalAmount +
                ", bookingStatus='" + bookingStatus + '\'' +
                ", bookingDate=" + bookingDate +
                '}';
    }
}