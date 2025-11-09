package com.jts.movie_ticket_booking_system.repository;

import com.jts.movie_ticket_booking_system.entity.Theater;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TheaterRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TheaterRepository theaterRepository;

    @Test
    void findByAddress_WhenTheaterExists_ShouldReturnTheater() {
        // Given
        Theater theater = Theater.builder()
                .name("Test Theater")
                .address("123 Test Street")
                .build();
        entityManager.persistAndFlush(theater);

        // When
        Theater found = theaterRepository.findByAddress("123 Test Street");

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getAddress()).isEqualTo("123 Test Street");
    }

    @Test
    void findById_WhenTheaterExists_ShouldReturnTheater() {
        // Given
        Theater theater = Theater.builder()
                .name("Test Theater")
                .address("123 Test Street")
                .build();
        Theater saved = entityManager.persistAndFlush(theater);

        // When
        Optional<Theater> found = theaterRepository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Theater");
    }

    @Test
    void findAll_ShouldReturnAllTheaters() {
        // Given
        Theater theater1 = Theater.builder().name("Theater 1").address("Addr 1").build();
        Theater theater2 = Theater.builder().name("Theater 2").address("Addr 2").build();

        entityManager.persist(theater1);
        entityManager.persist(theater2);
        entityManager.flush();

        // When
        List<Theater> theaters = theaterRepository.findAll();

        // Then
        assertThat(theaters).hasSize(2);
    }
}