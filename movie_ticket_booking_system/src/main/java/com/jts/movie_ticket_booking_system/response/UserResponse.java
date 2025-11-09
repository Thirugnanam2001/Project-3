package com.jts.movie_ticket_booking_system.response;

import com.jts.movie_ticket_booking_system.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private String name;
    private String emailId;
    private Integer age;
    private Gender gender;
    private String address;
    private String mobileNo;
    private String roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
