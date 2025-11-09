package com.jts.movie_ticket_booking_system.entity;


import com.jts.movie_ticket_booking_system.enums.PaymentMethod;
import com.jts.movie_ticket_booking_system.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "payments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String paymentId;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private String transactionId;

    @Column
    private String paymentGateway;

    @Column
    private String paymentNotes;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date paymentDate;

    @Column
    private String adminUsername; // For admin payments
    private String adminPassword;

    // Pre-persist to generate payment ID
    @PrePersist
    public void generatePaymentId() {
        if (this.paymentId == null) {
            this.paymentId = "PAY_" + System.currentTimeMillis() + "_" +
                    (int)(Math.random() * 1000);
        }
        if (this.paymentDate == null) {
            this.paymentDate = new Date();
        }
    }
}