package com.jts.movie_ticket_booking_system.repository;

import com.jts.movie_ticket_booking_system.entity.*;
import com.jts.movie_ticket_booking_system.enums.Genre;
import com.jts.movie_ticket_booking_system.enums.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ShowRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ShowRepository showRepository;

    @Test
    void getAllShowsOfMovie_WhenShowsExist_ShouldReturnShows() {
        // Given
        Movie movie = createTestMovie();
        Theater theater = createTestTheater();
        Show show = createTestShow(movie, theater);

        entityManager.persist(movie);
        entityManager.persist(theater);
        entityManager.persistAndFlush(show);

        // When
        List<Show> shows = showRepository.getAllShowsOfMovie(movie.getId());

        // Then
        assertThat(shows).hasSize(1);
        assertThat(shows.get(0).getMovie().getId()).isEqualTo(movie.getId());
        assertThat(shows.get(0).getTheater().getId()).isEqualTo(theater.getId());
    }

    @Test
    void getAllShowsOfMovie_WhenNoShows_ShouldReturnEmptyList() {
        // When
        List<Show> shows = showRepository.getAllShowsOfMovie(999);

        // Then
        assertThat(shows).isEmpty();
    }

    @Test
    void getShowTimingsOnDate_ShouldReturnTimings() {
        // Given
        Movie movie = createTestMovie();
        Theater theater = createTestTheater();
        Date showDate = Date.valueOf(LocalDate.now().plusDays(1));

        Show show1 = createTestShow(movie, theater);
        show1.setTime(Time.valueOf(LocalTime.of(18, 0)));
        show1.setDate(showDate);

        Show show2 = createTestShow(movie, theater);
        show2.setTime(Time.valueOf(LocalTime.of(21, 0)));
        show2.setDate(showDate);

        entityManager.persist(movie);
        entityManager.persist(theater);
        entityManager.persist(show1);
        entityManager.persist(show2);
        entityManager.flush();

        // When
        List<Time> timings = showRepository.getShowTimingsOnDate(
                showDate, theater.getId(), movie.getId());

        // Then
        assertThat(timings).hasSize(2);
    }

    @Test
    void saveShow_ShouldPersistCorrectly() {
        // Given
        Movie movie = createTestMovie();
        Theater theater = createTestTheater();
        Show show = createTestShow(movie, theater);

        entityManager.persist(movie);
        entityManager.persist(theater);

        // When
        Show saved = showRepository.save(show);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getMovie().getId()).isEqualTo(movie.getId());
        assertThat(saved.getTheater().getId()).isEqualTo(theater.getId());
    }

    private Movie createTestMovie() {
        return Movie.builder()
                .movieName("Test Movie")
                .genre(Genre.ACTION)
                .language(Language.ENGLISH)
                .duration(120)
                .rating(4.5)
                .releaseDate(Date.valueOf(LocalDate.now().plusDays(30)))
                .build();
    }

    private Theater createTestTheater() {
        return Theater.builder()
                .name("Test Theater")
                .address("Test Address")
                .build();
    }

    private Show createTestShow(Movie movie, Theater theater) {
        return Show.builder()
                .time(Time.valueOf(LocalTime.of(18, 0)))
                .date(Date.valueOf(LocalDate.now().plusDays(1)))
                .movie(movie)
                .theater(theater)
                .build();
    }
}