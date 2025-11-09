package com.jts.movie_ticket_booking_system.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Time;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShowRequest {

    private Time showStartTime;
    private Date showDate;
    private Integer theaterId;
    private Integer movieId;
}
