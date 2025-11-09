package com.jts.movie_ticket_booking_system.repository;

import com.jts.movie_ticket_booking_system.entity.User;
import com.jts.movie_ticket_booking_system.enums.Gender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmailId_WhenUserExists_ShouldReturnUser() {
        // Given
        User user = User.builder()
                .name("Test User")
                .emailId("test@example.com")
                .password("encodedPassword")
                .roles("ROLE_USER")
                .gender(Gender.MALE)
                .mobileNo("1234567890")
                .build();
        entityManager.persistAndFlush(user);

        // When
        Optional<User> found = userRepository.findByEmailId("test@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmailId()).isEqualTo("test@example.com");
    }

    @Test
    void existsByEmailId_WhenUserExists_ShouldReturnTrue() {
        // Given
        User user = User.builder()
                .name("Test User")
                .emailId("exists@example.com")
                .password("encodedPassword")
                .roles("ROLE_USER")
                .build();
        entityManager.persistAndFlush(user);

        // When
        boolean exists = userRepository.existsByEmailId("exists@example.com");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void findByEmailId_WhenUserNotExists_ShouldReturnEmpty() {
        // When
        Optional<User> found = userRepository.findByEmailId("nonexistent@example.com");

        // Then
        assertThat(found).isEmpty();
    }
}