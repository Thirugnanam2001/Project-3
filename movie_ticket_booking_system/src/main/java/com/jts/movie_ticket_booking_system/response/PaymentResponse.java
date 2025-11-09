package com.jts.movie_ticket_booking_system.response;

import com.jts.movie_ticket_booking_system.enums.PaymentMethod;
import com.jts.movie_ticket_booking_system.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private String status; // "success" or "error"
    private String message;
    private PaymentData payment;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentData {
        private String id;
        private Double amount;
        private PaymentMethod paymentMethod;
        private PaymentStatus status;
        private String transactionId;
        private Date paymentDate;
        private String bookingId;
        private String notes;
    }
}
