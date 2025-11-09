package com.jts.movie_ticket_booking_system.controllers;

import com.jts.movie_ticket_booking_system.controllers.MovieController;
import com.jts.movie_ticket_booking_system.entity.Movie;
import com.jts.movie_ticket_booking_system.enums.Genre;
import com.jts.movie_ticket_booking_system.enums.Language;
import com.jts.movie_ticket_booking_system.services.MovieService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovieController.class)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieService movieService;

    @Test
    @WithMockUser
    void getAllMovies_ShouldReturnMoviesPage() throws Exception {
        // Given
        Movie movie1 = Movie.builder()
                .id(1)
                .movieName("Avatar: The Way of Water")
                .genre(Genre.ACTION)
                .language(Language.ENGLISH)
                .duration(192)
                .rating(4.5)
                .releaseDate(Date.valueOf(LocalDate.now().plusDays(30)))
                .build();

        Movie movie2 = Movie.builder()
                .id(2)
                .movieName("Pathaan")
                .genre(Genre.ACTION)
                .language(Language.HINDI)
                .duration(146)
                .rating(4.2)
                .releaseDate(Date.valueOf(LocalDate.now().plusDays(30)))
                .build();

        when(movieService.getAllMovies()).thenReturn(Arrays.asList(movie1, movie2));

        // When & Then
        mockMvc.perform(get("/movies"))
                .andExpect(status().isOk())
                .andExpect(view().name("movies"))
                .andExpect(model().attributeExists("movies"))
                .andExpect(model().attribute("pageTitle", "All Movies - Movie Ticket Browser"))
                .andExpect(model().attribute("movies", hasSize(2)));
    }

    @Test
    @WithMockUser
    void getAllMovies_WhenNoMovies_ShouldReturnEmptyList() throws Exception {
        // Given
        when(movieService.getAllMovies()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/movies"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("movies", hasSize(0)));
    }

    @Test
    @WithMockUser // ADD THIS - endpoint requires authentication
    void testMoviesEndpoint_ShouldReturnMovieCount() throws Exception {
        // Given
        Movie movie = Movie.builder()
                .id(1)
                .movieName("Test Movie")
                .genre(Genre.ACTION)
                .language(Language.ENGLISH)
                .build();

        when(movieService.getAllMovies()).thenReturn(Arrays.asList(movie));

        // When & Then
        mockMvc.perform(get("/movies/test"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Movies in database: 1")));
    }

}