package com.jts.movie_ticket_booking_system.services;

import java.util.Optional;

import com.jts.movie_ticket_booking_system.exceptions.UserDoesNotExists;
import com.jts.movie_ticket_booking_system.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jts.movie_ticket_booking_system.convertor.UserConvertor;
import com.jts.movie_ticket_booking_system.entity.User;
import com.jts.movie_ticket_booking_system.exceptions.UserExist;
import com.jts.movie_ticket_booking_system.repository.UserRepository;
import com.jts.movie_ticket_booking_system.request.UserRequest;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String addUser(UserRequest userRequest) {
        Optional<User> users = userRepository.findByEmailId(userRequest.getEmailId());

        if (users.isPresent()) {
            throw new UserExist();
        }

        // Properly encode the password
        String encodedPassword = passwordEncoder.encode(userRequest.getPassword());
        System.out.println("Registering user: " + userRequest.getEmailId());
        System.out.println("Raw password: " + userRequest.getPassword());
        System.out.println("Encoded password: " + encodedPassword);

        User user = UserConvertor.userDtoToUser(userRequest, encodedPassword);
        userRepository.save(user);
        return "User Saved Successfully";
    }

    public UserResponse getUserProfile(String email) {
        User user = userRepository.findByEmailId(email)
                .orElseThrow(UserDoesNotExists::new);
        return UserConvertor.userToUserDto(user);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmailId(email)
                .orElseThrow(UserDoesNotExists::new);
    }

//    public List<UserResponse> getAllUsers() {
//        return userRepository.findAll().stream()
//                .map(UserConvertor::userToUserDto)
//                .collect(Collectors.toList());
//    }

    public String updateUserProfile(UserRequest userRequest) {
        User currentUser = getCurrentUser();

        currentUser.setName(userRequest.getName());
        currentUser.setAge(userRequest.getAge());
        currentUser.setAddress(userRequest.getAddress());
        currentUser.setGender(userRequest.getGender());
        currentUser.setMobileNo(userRequest.getMobileNo());

        userRepository.save(currentUser);
        return "Profile updated successfully";
    }

    public String changePassword(String currentPassword, String newPassword) {
        User user = getCurrentUser();

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return "Password changed successfully";
    }

    // Additional utility methods for profile page
    public long getTotalBookingsCount(Integer userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(u -> u.getTicketList().size()).orElse(Math.toIntExact(0L));
    }

    public long getMonthlyBookingsCount(Integer userId) {
        // For demo purposes - in real app, filter by current month
        Optional<User> user = userRepository.findById(userId);
        return user.map(u -> u.getTicketList().size()).orElse(Math.toIntExact(0L));
    }

    public Optional<User> getUserByEmail(String email) {
        try {
            return userRepository.findByEmailId(email);
        } catch (Exception e) {
            System.out.println("Error getting user by email: " + e.getMessage());
            return Optional.empty();
        }
    }
//    public List<User> getAllUsers() {
//        try {
//            // Use the repository's findAll method
//            List<User> users = userRepository.findAll();
//            System.out.println("Found " + users.size() + " users in database");
//            return users;
//        } catch (Exception e) {
//            System.out.println("Error in getAllUsers: " + e.getMessage());
//            return List.of(); // Return empty list instead of null
//        }
//    }
}
