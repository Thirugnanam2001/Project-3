package com.jts.movie_ticket_booking_system.controllers;

import com.jts.movie_ticket_booking_system.controllers.ShowController;
import com.jts.movie_ticket_booking_system.request.ShowRequest;
import com.jts.movie_ticket_booking_system.request.ShowSeatRequest;
import com.jts.movie_ticket_booking_system.services.ShowService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShowController.class)
class ShowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShowService showService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void addShow_WithValidData_ShouldReturnSuccess() throws Exception {
        // Given
        ShowRequest showRequest = new ShowRequest();
        showRequest.setShowStartTime(Time.valueOf(LocalTime.of(18, 0)));
        showRequest.setShowDate(Date.valueOf(LocalDate.now().plusDays(1)));
        showRequest.setTheaterId(1);
        showRequest.setMovieId(1);

        when(showService.addShow(any(ShowRequest.class)))
                .thenReturn("Show has been added Successfully");

        // When & Then
        mockMvc.perform(post("/show/addNew")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Show has been added Successfully"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addShow_WithInvalidMovie_ShouldReturnBadRequest() throws Exception {
        // Given
        ShowRequest showRequest = new ShowRequest();
        showRequest.setShowStartTime(Time.valueOf(LocalTime.of(18, 0)));
        showRequest.setShowDate(Date.valueOf(LocalDate.now().plusDays(1)));
        showRequest.setTheaterId(1);
        showRequest.setMovieId(999); // Non-existent movie

        when(showService.addShow(any(ShowRequest.class)))
                .thenThrow(new com.jts.movie_ticket_booking_system.exceptions.MovieDoesNotExists());

        // When & Then
        mockMvc.perform(post("/show/addNew")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addMultipleShows_WithValidData_ShouldReturnSuccess() throws Exception {
        // Given
        ShowRequest showRequest1 = new ShowRequest();
        showRequest1.setShowStartTime(Time.valueOf(LocalTime.of(18, 0)));
        showRequest1.setShowDate(Date.valueOf(LocalDate.now().plusDays(1)));
        showRequest1.setTheaterId(1);
        showRequest1.setMovieId(1);

        ShowRequest showRequest2 = new ShowRequest();
        showRequest2.setShowStartTime(Time.valueOf(LocalTime.of(21, 0)));
        showRequest2.setShowDate(Date.valueOf(LocalDate.now().plusDays(1)));
        showRequest2.setTheaterId(1);
        showRequest2.setMovieId(1);

        List<ShowRequest> showRequests = Arrays.asList(showRequest1, showRequest2);

        when(showService.addShow(any(ShowRequest.class)))
                .thenReturn("Show has been added Successfully");

        // When & Then
        mockMvc.perform(post("/show/addMultiple")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showRequests)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("completed"))
                .andExpect(jsonPath("$.successCount").value(2))
                .andExpect(jsonPath("$.errorCount").value(0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addMultipleShows_WithPartialFailures_ShouldReturnMixedResults() throws Exception {
        // Given
        ShowRequest showRequest1 = new ShowRequest();
        showRequest1.setShowStartTime(Time.valueOf(LocalTime.of(18, 0)));
        showRequest1.setShowDate(Date.valueOf(LocalDate.now().plusDays(1)));
        showRequest1.setTheaterId(1);
        showRequest1.setMovieId(1);

        ShowRequest showRequest2 = new ShowRequest();
        showRequest2.setShowStartTime(Time.valueOf(LocalTime.of(21, 0)));
        showRequest2.setShowDate(Date.valueOf(LocalDate.now().plusDays(1)));
        showRequest2.setTheaterId(1);
        showRequest2.setMovieId(999); // This will fail

        List<ShowRequest> showRequests = Arrays.asList(showRequest1, showRequest2);

        when(showService.addShow(any(ShowRequest.class)))
                .thenReturn("Show has been added Successfully")
                .thenThrow(new com.jts.movie_ticket_booking_system.exceptions.MovieDoesNotExists());

        // When & Then
        mockMvc.perform(post("/show/addMultiple")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showRequests)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("completed"))
                .andExpect(jsonPath("$.successCount").value(1))
                .andExpect(jsonPath("$.errorCount").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void associateShowSeats_WithValidData_ShouldReturnSuccess() throws Exception {
        // Given
        ShowSeatRequest seatRequest = new ShowSeatRequest();
        seatRequest.setShowId(1);
        seatRequest.setPriceOfPremiumSeat(350);
        seatRequest.setPriceOfClassicSeat(200);

        when(showService.associateShowSeats(any(ShowSeatRequest.class)))
                .thenReturn("Show seats have been associated successfully");

        // When & Then
        mockMvc.perform(post("/show/associateSeats")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(seatRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Show seats have been associated successfully"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void associateShowSeats_WithInvalidShow_ShouldReturnBadRequest() throws Exception {
        // Given
        ShowSeatRequest seatRequest = new ShowSeatRequest();
        seatRequest.setShowId(999); // Non-existent show

        when(showService.associateShowSeats(any(ShowSeatRequest.class)))
                .thenThrow(new com.jts.movie_ticket_booking_system.exceptions.ShowDoesNotExists());

        // When & Then
        mockMvc.perform(post("/show/associateSeats")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(seatRequest)))
                .andExpect(status().isBadRequest());
    }
}