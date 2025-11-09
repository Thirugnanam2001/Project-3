package com.jts.movie_ticket_booking_system.request;

import com.jts.movie_ticket_booking_system.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    private Long bookingId;
    private Double amount;
    private PaymentMethod paymentMethod;
    private String notes;

    // For admin payments
    private String adminUsername;
    private String adminPassword;

    // For card payments (fake data)
    private String cardNumber;
    private String expiryDate;
    private String cvv;
    private String cardHolderName;

    // For UPI payments (fake data)
    private String upiId;

    // For net banking (fake data)
    private String bank;
}
//package com.jts.movie.request;
//
//import com.jts.movie.entity.Payment;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class PaymentRequest {
//    private String username;
//    private Long bookingId;
//    private Double amount;
//    private Payment.PaymentMethod paymentMethod;
//    private String cardNumber;
//    private String expiryDate;
//    private String cvv;
//    private String cardHolderName;
//    private String upiId;
//    private String notes;
//    private String password;
//
//
//
//}