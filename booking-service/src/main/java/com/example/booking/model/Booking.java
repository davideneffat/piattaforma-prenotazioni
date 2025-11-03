package com.example.booking.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;  // id utente (collegato al microservizio utenti)
    private String serviceName;
    private LocalDateTime bookingTime;
    private String status; // PENDING, CONFIRMED, CANCELLED
}
