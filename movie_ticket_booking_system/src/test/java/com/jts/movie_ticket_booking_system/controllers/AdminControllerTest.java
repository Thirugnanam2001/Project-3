package com.jts.movie_ticket_booking_system.controllers;

import com.jts.movie_ticket_booking_system.request.TheaterRequest;
import com.jts.movie_ticket_booking_system.request.TheaterSeatRequest;
import com.jts.movie_ticket_booking_system.services.TheaterService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TheaterService theaterService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void addTheater_WithValidData_ShouldRedirectWithSuccess() throws Exception {
        // Given
        when(theaterService.addTheater(any(TheaterRequest.class)))
                .thenReturn("Theater has been saved Successfully");

        // When & Then
        mockMvc.perform(post("/admin/theaters/add")
                        .param("name", "New Theater")
                        .param("address", "New Address"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/theaters/add"))
                .andExpect(flash().attributeExists("successMessage"));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void addTheaterSeats_WithValidData_ShouldRedirectWithSuccess() throws Exception {
        // Given
        when(theaterService.addTheaterSeat(any(TheaterSeatRequest.class)))
                .thenReturn("Theater Seats have been added successfully");

        // When & Then
        mockMvc.perform(post("/admin/theaters/seats")
                        .param("address", "Existing Theater Address")
                        .param("noOfSeatInRow", "10")
                        .param("noOfPremiumSeat", "20")
                        .param("noOfClassicSeat", "80"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/theaters/seats"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @WithMockUser(roles = "USER") // Regular user, not admin
    void adminEndpoints_WithUserRole_ShouldBeForbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/admin/theaters/add"))
                .andExpect(status().isForbidden());
    }
}