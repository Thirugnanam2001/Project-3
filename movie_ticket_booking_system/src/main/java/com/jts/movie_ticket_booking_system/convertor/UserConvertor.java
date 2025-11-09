package com.jts.movie_ticket_booking_system.convertor;

import com.jts.movie_ticket_booking_system.entity.User;
import com.jts.movie_ticket_booking_system.request.UserRequest;
import com.jts.movie_ticket_booking_system.response.UserResponse;


public class UserConvertor {

    public static User userDtoToUser(UserRequest userRequest, String encodePassword) {
        User user = User.builder()
                .name(userRequest.getName())
                .age(userRequest.getAge())
                .address(userRequest.getAddress())
                .gender(userRequest.getGender())
                .mobileNo(userRequest.getMobileNo())
                .emailId(userRequest.getEmailId())
                .roles(userRequest.getRoles())
                .password(encodePassword)
                .build();

        return user;
    }

    public static UserResponse userToUserDto(User user) {
        return UserResponse.builder()
                .name(user.getName())
                .emailId(user.getEmailId())
                .age(user.getAge())
                .gender(user.getGender())
                .address(user.getAddress())
                .mobileNo(user.getMobileNo())
                .roles(user.getRoles())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
    public static User convertToEntity(UserRequest userRequest, String encodedPassword) {
        return userDtoToUser(userRequest, encodedPassword);
    }

    public static UserResponse convertToResponse(User user) {
        return userToUserDto(user);
    }
}
