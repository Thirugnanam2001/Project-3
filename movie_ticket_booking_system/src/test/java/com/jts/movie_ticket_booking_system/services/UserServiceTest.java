package com.jts.movie_ticket_booking_system.services;

import com.jts.movie_ticket_booking_system.entity.User;
import com.jts.movie_ticket_booking_system.enums.Gender;
import com.jts.movie_ticket_booking_system.exceptions.UserExist;
import com.jts.movie_ticket_booking_system.repository.UserRepository;
import com.jts.movie_ticket_booking_system.request.UserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserRequest userRequest;
    private User existingUser;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequest(
                "Test User",
                25,
                "Test Address",
                "1234567890",
                "test@example.com",
                Gender.MALE,
                "ROLE_USER",
                "password123"
        );

        existingUser = User.builder()
                .name("Existing User")
                .emailId("existing@example.com")
                .password("encodedPassword")
                .roles("ROLE_USER")
                .build();
    }

    @Test
    void addUser_WhenUserDoesNotExist_ShouldSaveUser() {
        // Given
        when(userRepository.findByEmailId(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // When
        String result = userService.addUser(userRequest);

        // Then
        assertThat(result).isEqualTo("User Saved Successfully");
        verify(userRepository).findByEmailId("test@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void addUser_WhenUserAlreadyExists_ShouldThrowException() {
        // Given
        when(userRepository.findByEmailId(anyString())).thenReturn(Optional.of(existingUser));

        // When & Then
        assertThatThrownBy(() -> userService.addUser(userRequest))
                .isInstanceOf(UserExist.class)
                .hasMessage("User Already Exists with this EmailId");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserByEmail_WhenUserExists_ShouldReturnUser() {
        // Given
        when(userRepository.findByEmailId("test@example.com")).thenReturn(Optional.of(existingUser));

        // When
        Optional<User> result = userService.getUserByEmail("test@example.com");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmailId()).isEqualTo("existing@example.com");
    }

    @Test
    void getUserByEmail_WhenUserNotExists_ShouldReturnEmpty() {
        // Given
        when(userRepository.findByEmailId("nonexistent@example.com")).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.getUserByEmail("nonexistent@example.com");

        // Then
        assertThat(result).isEmpty();
    }
}