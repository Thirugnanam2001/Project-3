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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
        try {
            System.out.println("Processing payment request: " + paymentRequest);

            // Validate booking exists
            Optional<Booking> bookingOpt = bookingRepository.findById(paymentRequest.getBookingId());
            if (bookingOpt.isEmpty()) {
                return PaymentResponse.builder()
                        .status("error")
                        .message("Booking not found")
                        .build();
            }

            Booking booking = bookingOpt.get();

            // Check for duplicate payments
            if (hasSuccessfulPayment(booking)) {
                return createDuplicatePaymentResponse(booking, paymentRequest);
            }

            // Process payment based on method
            PaymentStatus paymentStatus;
            String transactionId = generateTransactionId();
            String paymentNotes = paymentRequest.getNotes();

            if (paymentRequest.getPaymentMethod() == PaymentMethod.ADMIN) {
                // Admin payment - always successful
                paymentStatus = PaymentStatus.SUCCESS;
                paymentNotes = "Admin payment processed by: " + paymentRequest.getAdminUsername();
            } else {
                // For demo purposes, all non-admin payments succeed
                // In real app, you'd integrate with payment gateway here
                paymentStatus = PaymentStatus.SUCCESS;
                paymentNotes = "Demo payment - " + getPaymentMethodDescription(paymentRequest.getPaymentMethod());
            }

            // Create and save payment
            Payment payment = Payment.builder()
                    .booking(booking)
                    .amount(paymentRequest.getAmount())
                    .paymentMethod(paymentRequest.getPaymentMethod())
                    .status(paymentStatus)
                    .transactionId(transactionId)
                    .paymentGateway("DEMO_GATEWAY")
                    .paymentNotes(paymentNotes)
                    .adminUsername(paymentRequest.getAdminUsername())
                    .build();

            Payment savedPayment = paymentRepository.save(payment);

            // Update booking status if payment successful
            if (paymentStatus == PaymentStatus.SUCCESS) {
                booking.setBookingStatus("CONFIRMED");
                bookingRepository.save(booking);
            }

            // Build response
            return PaymentResponse.builder()
                    .status("success")
                    .message("Payment processed successfully")
                    .payment(PaymentResponse.PaymentData.builder()
                            .id(savedPayment.getPaymentId())
                            .amount(savedPayment.getAmount())
                            .paymentMethod(savedPayment.getPaymentMethod())
                            .status(savedPayment.getStatus())
                            .transactionId(savedPayment.getTransactionId())
                            .paymentDate(savedPayment.getPaymentDate())
                            .bookingId(booking.getId().toString())
                            .notes(savedPayment.getPaymentNotes())
                            .build())
                    .build();

        } catch (Exception e) {
            System.err.println("Payment processing error: " + e.getMessage());
            return PaymentResponse.builder()
                    .status("error")
                    .message("Payment processing failed: " + e.getMessage())
                    .build();
        }
    }

    public PaymentResponse validateAdminCredentials(String username, String password) {
        try {
            Optional<User> adminOpt = userRepository.findByEmailId(username);

            if (adminOpt.isPresent()) {
                User admin = adminOpt.get();

                // Check if user has admin role and password matches
                if (admin.getRoles().contains("ROLE_ADMIN") &&
                        passwordEncoder.matches(password, admin.getPassword())) {

                    return PaymentResponse.builder()
                            .status("success")
                            .message("Admin authentication successful")
                            .build();
                }
            }

            return PaymentResponse.builder()
                    .status("error")
                    .message("Invalid admin credentials")
                    .build();

        } catch (Exception e) {
            return PaymentResponse.builder()
                    .status("error")
                    .message("Admin authentication failed: " + e.getMessage())
                    .build();
        }
    }

    private boolean hasSuccessfulPayment(Booking booking) {
        return paymentRepository.findByBookingAndStatus(booking, PaymentStatus.SUCCESS).isPresent();
    }

    private PaymentResponse createDuplicatePaymentResponse(Booking booking, PaymentRequest paymentRequest) {
        // Create a duplicate payment record
        Payment duplicatePayment = Payment.builder()
                .booking(booking)
                .amount(paymentRequest.getAmount())
                .paymentMethod(paymentRequest.getPaymentMethod())
                .status(PaymentStatus.DUPLICATE)
                .transactionId("DUP_" + System.currentTimeMillis())
                .paymentGateway("DEMO_GATEWAY")
                .paymentNotes("Duplicate payment detected - Original payment exists")
                .adminUsername(paymentRequest.getAdminUsername())
                .build();

        Payment savedDuplicate = paymentRepository.save(duplicatePayment);

        return PaymentResponse.builder()
                .status("success")
                .message("Duplicate payment detected - Original payment already exists")
                .payment(PaymentResponse.PaymentData.builder()
                        .id(savedDuplicate.getPaymentId())
                        .amount(savedDuplicate.getAmount())
                        .paymentMethod(savedDuplicate.getPaymentMethod())
                        .status(savedDuplicate.getStatus())
                        .transactionId(savedDuplicate.getTransactionId())
                        .paymentDate(savedDuplicate.getPaymentDate())
                        .bookingId(booking.getId().toString())
                        .notes(savedDuplicate.getPaymentNotes())
                        .build())
                .build();
    }

    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }

    private String getPaymentMethodDescription(PaymentMethod method) {
        switch (method) {
            case CREDIT_CARD:
                return "Credit Card Payment (Demo)";
            case DEBIT_CARD:
                return "Debit Card Payment (Demo)";
            case UPI:
                return "UPI Payment (Demo)";
            case NET_BANKING:
                return "Net Banking (Demo)";
            case ADMIN:
                return "Admin Payment";
            default:
                return "Unknown Payment Method";
        }
    }
}
