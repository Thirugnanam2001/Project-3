//package com.jts.movie.repository;
//
//import com.jts.movie.entity.Movie;
//import com.jts.movie.enums.Genre;
//import com.jts.movie.enums.Language;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//import org.springframework.test.context.TestPropertySource;
//
//import java.sql.Date;
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@DataJpaTest
//@TestPropertySource(locations = "classpath:application-test.properties")
//class MovieRepositoryTest {
//
//    @Autowired
//    private TestEntityManager entityManager;
//
//    @Autowired
//    private MovieRepository movieRepository;
//
//    @Test
//    void findByMovieName_WhenMovieExists_ShouldReturnMovie() {
//        // Given
//        Movie movie = Movie.builder()
//                .movieName("Test Movie")
//                .genre(Genre.ACTION)
//                .language(Language.ENGLISH)
//                .duration(120)
//                .rating(4.5)
//                .releaseDate(Date.valueOf(LocalDate.now().plusDays(30)))
//                .build();
//
//        // Use entityManager to persist and flush
//        Movie savedMovie = entityManager.persistAndFlush(movie);
//
//        // When
//        Movie found = movieRepository.findByMovieName("Test Movie");
//
//        // Then
//        assertThat(found).isNotNull();
//        assertThat(found.getMovieName()).isEqualTo("Test Movie");
//        assertThat(found.getGenre()).isEqualTo(Genre.ACTION);
//    }
//
//    @Test
//    void findAll_ShouldReturnAllMovies() {
//        // Given
//        Movie movie1 = Movie.builder()
//                .movieName("Movie 1")
//                .genre(Genre.ACTION)
//                .language(Language.ENGLISH)
//                .duration(120)
//                .build();
//
//        Movie movie2 = Movie.builder()
//                .movieName("Movie 2")
//                .genre(Genre.DRAMA)
//                .language(Language.HINDI)
//                .duration(150)
//                .build();
//
//        entityManager.persist(movie1);
//        entityManager.persist(movie2);
//        entityManager.flush();
//
//        // When
//        List<Movie> movies = movieRepository.findAll();
//
//        // Then
//        assertThat(movies).hasSize(2);
//        assertThat(movies).extracting(Movie::getMovieName)
//                .containsExactlyInAnyOrder("Movie 1", "Movie 2");
//    }
//
//    @Test
//    void findByMovieId_WhenMovieExists_ShouldReturnMovie() {
//        // Given
//        Movie movie = Movie.builder()
//                .movieName("Test Movie")
//                .genre(Genre.ACTION)
//                .language(Language.ENGLISH)
//                .duration(120)
//                .build();
//
//        Movie saved = entityManager.persistAndFlush(movie);
//
//        // When
//        Optional<Movie> found = movieRepository.findByMovieId(saved.getId());
//
//        // Then
//        assertThat(found).isPresent();
//        assertThat(found.get().getId()).isEqualTo(saved.getId());
//        assertThat(found.get().getMovieName()).isEqualTo("Test Movie");
//    }
//
//    @Test
//    void findByMovieName_WhenMovieNotExists_ShouldReturnNull() {
//        // When
//        Movie found = movieRepository.findByMovieName("NonExistentMovie");
//
//        // Then
//        assertThat(found).isNull();
//    }
//}

package com.jts.movie_ticket_booking_system.repository;

import com.jts.movie_ticket_booking_system.entity.Movie;
import com.jts.movie_ticket_booking_system.enums.Genre;
import com.jts.movie_ticket_booking_system.enums.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MovieRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MovieRepository movieRepository;

    @Test
    void findByMovieName_WhenMovieExists_ShouldReturnMovie() {
        // Given
        Movie movie = Movie.builder()
                .movieName("Test Movie")
                .genre(Genre.ACTION)
                .language(Language.ENGLISH)
                .duration(120)
                .rating(4.5)
                .releaseDate(Date.valueOf(LocalDate.now().plusDays(30)))
                .build();
        entityManager.persistAndFlush(movie);

        // When
        Movie found = movieRepository.findByMovieName("Test Movie");

        // Then
        assertThat(found).isNotNull();
        assertThat(found.getMovieName()).isEqualTo("Test Movie");
    }

    @Test
    void findAll_ShouldReturnAllMovies() {
        // Given
        Movie movie1 = Movie.builder()
                .movieName("Movie 1")
                .genre(Genre.ACTION)
                .language(Language.ENGLISH)
                .duration(120)
                .build();

        Movie movie2 = Movie.builder()
                .movieName("Movie 2")
                .genre(Genre.DRAMA)
                .language(Language.HINDI)
                .duration(150)
                .build();

        entityManager.persist(movie1);
        entityManager.persist(movie2);
        entityManager.flush();

        // When
        List<Movie> movies = movieRepository.findAll();

        // Then
        assertThat(movies).hasSize(2);
        assertThat(movies).extracting(Movie::getMovieName)
                .containsExactlyInAnyOrder("Movie 1", "Movie 2");
    }

    @Test
    void findByMovieId_WhenMovieExists_ShouldReturnMovie() {
        // Given
        Movie movie = Movie.builder()
                .movieName("Test Movie")
                .genre(Genre.ACTION)
                .language(Language.ENGLISH)
                .duration(120)
                .build();
        Movie saved = entityManager.persistAndFlush(movie);

        // When
        Optional<Movie> found = movieRepository.findByMovieId(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(saved.getId());
    }
}