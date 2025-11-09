package com.jts.movie_ticket_booking_system.controllers;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import com.jts.movie_ticket_booking_system.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(com.jts.movie_ticket_booking_system.controllers.AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

   @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    void allPostEndpoints_RequireCsrfProtection() throws Exception {
        String jsonRequest = """
            {
                "username": "test@example.com",
                "password": "password123"
            }
            """;

        // Test login without CSRF - should be forbidden
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isForbidden());

        // Test debug-password without CSRF - should be forbidden
        mockMvc.perform(post("/api/auth/debug-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isForbidden());

        // Test logout without CSRF - should be forbidden
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isForbidden());
    }
}