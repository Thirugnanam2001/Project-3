package com.jts.movie_ticket_booking_system.services;

import com.jts.movie_ticket_booking_system.entity.Booking;
import com.jts.movie_ticket_booking_system.entity.Payment;
import com.jts.movie_ticket_booking_system.entity.User;
import com.jts.movie_ticket_booking_system.enums.PaymentMethod;
import com.jts.movie_ticket_booking_system.enums.PaymentStatus;
import com.jts.movie_ticket_booking_system.repository.BookingRepository;
import com.jts.movie_ticket_booking_system.repository.PaymentRepository;
import com.jts.movie_ticket_booking_system.repository.UserRepository;
import com.jts.movie_ticket_booking_system.request.PaymentRequest;
import com.jts.movie_ticket_booking_system.response.PaymentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PaymentService paymentService;

    private PaymentRequest paymentRequest;
    private Booking booking;
    private User adminUser;

    @BeforeEach
    void setUp() {
        paymentRequest = new PaymentRequest();
        paymentRequest.setBookingId(1L);
        paymentRequest.setAmount(550.0);
        paymentRequest.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        paymentRequest.setNotes("Test payment");

        booking = new Booking();
        booking.setId(1L);
        booking.setBookingStatus("PENDING");

        adminUser = User.builder()
                .id(1)
                .emailId("admin@example.com")
                .password("encodedPassword")
                .roles("ROLE_ADMIN")
                .build();
    }

    @Test
    void processPayment_WhenValidRequest_ShouldProcessSuccessfully() {
        // Given
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(paymentRepository.findByBookingAndStatus(any(Booking.class), any(PaymentStatus.class)))
                .thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            payment.setId(1L);
            return payment;
        });

        // When
        PaymentResponse result = paymentService.processPayment(paymentRequest);

        // Then
        assertThat(result.getStatus()).isEqualTo("success");
        assertThat(result.getMessage()).isEqualTo("Payment processed successfully");
        assertThat(result.getPayment()).isNotNull();
        assertThat(result.getPayment().getStatus()).isEqualTo(PaymentStatus.SUCCESS);

        verify(bookingRepository).findById(1L);
        verify(paymentRepository).save(any(Payment.class));
        verify(bookingRepository).save(booking);
    }

    @Test
    void processPayment_WhenBookingNotExists_ShouldReturnError() {
        // Given
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        PaymentResponse result = paymentService.processPayment(paymentRequest);

        // Then
        assertThat(result.getStatus()).isEqualTo("error");
        assertThat(result.getMessage()).isEqualTo("Booking not found");

        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void processPayment_WhenDuplicatePayment_ShouldReturnDuplicateResponse() {
        // Given
        Payment existingPayment = Payment.builder()
                .status(PaymentStatus.SUCCESS)
                .build();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(paymentRepository.findByBookingAndStatus(any(Booking.class), any(PaymentStatus.class)))
                .thenReturn(Optional.of(existingPayment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            payment.setId(2L);
            return payment;
        });

        // When
        PaymentResponse result = paymentService.processPayment(paymentRequest);

        // Then
        assertThat(result.getStatus()).isEqualTo("success");
        assertThat(result.getMessage()).contains("Duplicate payment detected");
        assertThat(result.getPayment().getStatus()).isEqualTo(PaymentStatus.DUPLICATE);
    }

    @Test
    void validateAdminCredentials_WithValidCredentials_ShouldReturnSuccess() {
        // Given
        when(userRepository.findByEmailId("admin@example.com")).thenReturn(Optional.of(adminUser));
        when(passwordEncoder.matches("admin123", "encodedPassword")).thenReturn(true);

        // When
        PaymentResponse result = paymentService.validateAdminCredentials("admin@example.com", "admin123");

        // Then
        assertThat(result.getStatus()).isEqualTo("success");
        assertThat(result.getMessage()).isEqualTo("Admin authentication successful");
    }

    @Test
    void validateAdminCredentials_WithInvalidPassword_ShouldReturnError() {
        // Given
        when(userRepository.findByEmailId("admin@example.com")).thenReturn(Optional.of(adminUser));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        // When
        PaymentResponse result = paymentService.validateAdminCredentials("admin@example.com", "wrongpassword");

        // Then
        assertThat(result.getStatus()).isEqualTo("error");
        assertThat(result.getMessage()).isEqualTo("Invalid admin credentials");
    }

    @Test
    void validateAdminCredentials_WhenUserNotAdmin_ShouldReturnError() {
        // Given
        User regularUser = User.builder()
                .emailId("user@example.com")
                .password("encodedPassword")
                .roles("ROLE_USER") // Not admin
                .build();

        when(userRepository.findByEmailId("user@example.com")).thenReturn(Optional.of(regularUser));

        // When
        PaymentResponse result = paymentService.validateAdminCredentials("user@example.com", "password");

        // Then
        assertThat(result.getStatus()).isEqualTo("error");
        assertThat(result.getMessage()).isEqualTo("Invalid admin credentials");
    }

    @Test
    void processPayment_WithAdminMethod_ShouldProcessSuccessfully() {
        // Given
        paymentRequest.setPaymentMethod(PaymentMethod.ADMIN);
        paymentRequest.setAdminUsername("admin@example.com");

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(paymentRepository.findByBookingAndStatus(any(Booking.class), any(PaymentStatus.class)))
                .thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            payment.setId(1L);
            return payment;
        });

        // When
        PaymentResponse result = paymentService.processPayment(paymentRequest);

        // Then
        assertThat(result.getStatus()).isEqualTo("success");
        assertThat(result.getPayment().getStatus()).isEqualTo(PaymentStatus.SUCCESS);
    }
}