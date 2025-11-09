package com.jts.movie_ticket_booking_system.config;

import com.jts.movie_ticket_booking_system.utils.TimeFormatter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public TimeFormatter timeFormatter() {
        return new TimeFormatter();
    }
}