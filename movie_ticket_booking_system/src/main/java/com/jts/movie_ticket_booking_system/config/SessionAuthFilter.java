
package com.jts.movie_ticket_booking_system.config;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@Component
public class SessionAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session != null) {
            // Restore security context from session if available
            Object securityContext = session.getAttribute("SPRING_SECURITY_CONTEXT");
            if (securityContext instanceof org.springframework.security.core.context.SecurityContext) {
                org.springframework.security.core.context.SecurityContextHolder.setContext(
                        (org.springframework.security.core.context.SecurityContext) securityContext
                );
            }
        }

        filterChain.doFilter(request, response);
    }
}