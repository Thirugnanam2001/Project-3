package com.jts.movie_ticket_booking_system.entity;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "SHOWS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Show {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="time")
    private Time time;

    @Column
    private Date date;

    @ManyToOne
    @JoinColumn
    private Movie movie;

    @ManyToOne
    @JoinColumn
    private Theater theater;

    @OneToMany(mappedBy = "show", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ShowSeat> showSeatList = new ArrayList<>();

    @OneToMany(mappedBy = "show", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Ticket> ticketList = new ArrayList<>();

    @Override
    public String toString() {
        return "Show{" +
                "id=" + id +
                ", time=" + time +
                ", date=" + date +
                ", movie=" + (movie != null ? movie.getMovieName() : "null") +
                ", theater=" + (theater != null ? theater.getName() : "null") +
                // Exclude collections to avoid circular reference
                '}';
    }
}
