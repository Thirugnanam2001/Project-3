package com.jts.movie_ticket_booking_system.request;

import com.jts.movie_ticket_booking_system.enums.Gender;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

    private String name;
    private Integer age;
    private String address;
    private String mobileNo;
    @NotBlank
    @Email
    private String emailId;
    private Gender gender;
    private String roles;
    @NotBlank
    private String password;

    public UserRequest(String name, String emailId, String password, String roles) {
        this.name = name;
        this.emailId = emailId;
        this.password = password;
        this.roles = roles;
    }
}
