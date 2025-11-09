package com.jts.movie_ticket_booking_system.controllers;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.jts.movie_ticket_booking_system.entity.User;
import com.jts.movie_ticket_booking_system.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        try {
            System.out.println("Login attempt for user: " + loginRequest.getUsername());

            // Manually validate user
            Optional<User> userOpt = userRepository.findByEmailId(loginRequest.getUsername());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                System.out.println("User found: " + user.getEmailId());

                // Verify password
                if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                    System.out.println("Password matches!");

                    // Create authorities
                    List<GrantedAuthority> authorities = Arrays.stream(user.getRoles().split(","))
                            .map(role -> new SimpleGrantedAuthority(role.trim()))
                            .collect(Collectors.toList());

                    // Create authentication token
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(user.getEmailId(), null, authorities);
                    authToken.setDetails(user);

                    // Set authentication in security context
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    // Create or get session and store authentication
                    HttpSession session = request.getSession(true);
                    session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
                    session.setAttribute("authenticatedUser", user);
                    session.setMaxInactiveInterval(30 * 60); // 30 minutes

                    System.out.println("Session created with ID: " + session.getId());
                    System.out.println("Authentication stored in session");

                    // Create success response
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "success");
                    response.put("message", "Login successful");
                    response.put("username", user.getEmailId());
                    response.put("authenticated", true);
                    response.put("roles", user.getRoles());
                    response.put("userId", user.getId());

                    return ResponseEntity.ok(response);
                } else {
                    System.out.println("Password does not match!");
                    throw new RuntimeException("Invalid password");
                }
            } else {
                System.out.println("User not found: " + loginRequest.getUsername());
                throw new RuntimeException("User not found");
            }

        } catch (Exception e) {
            System.err.println("Login failed: " + e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Invalid username or password");
            response.put("authenticated", false);

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkAuth(HttpServletRequest request) {
        // Try to get the session first
        HttpSession session = request.getSession(false);

        System.out.println("=== AUTH CHECK DEBUG ===");
        System.out.println("Session ID: " + (session != null ? session.getId() : "No session"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("SecurityContext Authentication: " + authentication);

        // If no authentication in context, try to restore from session
        if ((authentication == null || !authentication.isAuthenticated() ||
                authentication.getName().equals("anonymousUser")) && session != null) {

            // FIX: Check if session attribute exists and is not null
            Object securityContext = session.getAttribute("SPRING_SECURITY_CONTEXT");
            if (securityContext instanceof org.springframework.security.core.context.SecurityContext) {
                SecurityContextHolder.setContext((org.springframework.security.core.context.SecurityContext) securityContext);
                authentication = SecurityContextHolder.getContext().getAuthentication();
                System.out.println("Restored authentication from session: " + authentication);
            } else {
                System.out.println("No valid SecurityContext found in session");
            }
        }

        Map<String, Object> response = new HashMap<>();

        if (authentication != null && authentication.isAuthenticated() &&
                !authentication.getName().equals("anonymousUser")) {
            response.put("authenticated", true);
            response.put("username", authentication.getName());
            response.put("authorities", authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));
            response.put("sessionActive", session != null);
            if (session != null) {
                response.put("sessionId", session.getId());
            }
        } else {
            response.put("authenticated", false);
            response.put("message", "Not authenticated. Please login first.");
            response.put("sessionActive", false);
        }

        System.out.println("Final response: " + response);
        System.out.println("=== END DEBUG ===");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/session-info")
    public ResponseEntity<Map<String, Object>> sessionInfo(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        HttpSession session = request.getSession(false);

        if (session != null) {
            response.put("sessionId", session.getId());
            response.put("creationTime", new Date(session.getCreationTime()));
            response.put("lastAccessedTime", new Date(session.getLastAccessedTime()));
            response.put("maxInactiveInterval", session.getMaxInactiveInterval());
            response.put("attributes", Collections.list(session.getAttributeNames()));
            response.put("message", "Active session found");
        } else {
            response.put("message", "No active session");
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/debug-password")
    public ResponseEntity<Map<String, Object>> debugPassword(@RequestBody LoginRequest loginRequest) {
        Map<String, Object> response = new HashMap<>();

        Optional<User> userOpt = userRepository.findByEmailId(loginRequest.getUsername());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            response.put("user_found", true);
            response.put("stored_password", user.getPassword());
            response.put("input_password", loginRequest.getPassword());
            response.put("password_matches", passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()));
            response.put("password_encoded_with_same_encoder", passwordEncoder.encode(loginRequest.getPassword()));
        } else {
            response.put("user_found", false);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        SecurityContextHolder.clearContext();

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Logout successful");

        return ResponseEntity.ok(response);
    }

    // Login request DTO
    public static class LoginRequest {
        private String username;
        private String password;

        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}

//
//    @GetMapping("/session-info")
//    public ResponseEntity<Map<String, Object>> sessionInfo(HttpServletRequest request) {
//        Map<String, Object> response = new HashMap<>();
//        HttpSession session = request.getSession(false);
//
//        if (session != null) {
//            response.put("sessionId", session.getId());
//            response.put("creationTime", new Date(session.getCreationTime()));
//            response.put("lastAccessedTime", new Date(session.getLastAccessedTime()));
//            response.put("maxInactiveInterval", session.getMaxInactiveInterval());
//            response.put("attributes", Collections.list(session.getAttributeNames()));
//        } else {
//            response.put("message", "No active session");
//        }
//
//        return ResponseEntity.ok(response);
//    }

//    @GetMapping("/check")
//    public ResponseEntity<Map<String, Object>> checkAuth(HttpServletRequest request) {
//        // Try to get the session first
//        HttpSession session = request.getSession(false);
//
//        System.out.println("=== AUTH CHECK DEBUG ===");
//        System.out.println("Session ID: " + (session != null ? session.getId() : "No session"));
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        System.out.println("SecurityContext Authentication: " + authentication);
//
//        // If no authentication in context, try to restore from session
//        if ((authentication == null || !authentication.isAuthenticated() ||
//                authentication.getName().equals("anonymousUser")) && session != null) {
//
//            // Try to restore authentication from session
//            SecurityContextHolder.setContext(
//                    (org.springframework.security.core.context.SecurityContext)
//                            session.getAttribute("SPRING_SECURITY_CONTEXT")
//            );
//            authentication = SecurityContextHolder.getContext().getAuthentication();
//            System.out.println("Restored authentication from session: " + authentication);
//        }
//
//        Map<String, Object> response = new HashMap<>();
//
//        if (authentication != null && authentication.isAuthenticated() &&
//                !authentication.getName().equals("anonymousUser")) {
//            response.put("authenticated", true);
//            response.put("username", authentication.getName());
//            response.put("authorities", authentication.getAuthorities().stream()
//                    .map(GrantedAuthority::getAuthority)
//                    .collect(Collectors.toList()));
//            response.put("sessionActive", session != null);
//            if (session != null) {
//                response.put("sessionId", session.getId());
//            }
//        } else {
//            response.put("authenticated", false);
//            response.put("message", "Not authenticated. Please login first.");
//            response.put("sessionActive", false);
//        }
//
//        System.out.println("Final response: " + response);
//        System.out.println("=== END DEBUG ===");
//
//        return ResponseEntity.ok(response);
//    }