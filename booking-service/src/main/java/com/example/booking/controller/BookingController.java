package com.example.booking.controller;

import com.example.booking.client.UserClient;
import com.example.booking.model.Booking;
import com.example.booking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final UserClient userClient;

    @Autowired
    public BookingController(BookingService bookingService, UserClient userClient) {
        this.bookingService = bookingService;
        this.userClient = userClient;
    }

    /**
     * Crea una nuova prenotazione.
     * Richiede che il middleware (es. API Gateway) abbia aggiunto "username" all'HttpServletRequest.
     */
    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody Booking booking, Principal principal) {
        String username = principal.getName(); // Ritorna username estratto dal token
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: missing username attribute");
        }

        // Chiamata al microservizio utenti per ottenere userId
        UserClient.UserDto userDto = userClient.getByUsername(username).block();
        if (userDto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found in user-service");
        }

        booking.setUserId(userDto.id());
        Booking saved = bookingService.createBooking(booking);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Ottiene tutte le prenotazioni di un utente.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Booking>> getBookingsByUser(@PathVariable Long userId) {
        List<Booking> bookings = bookingService.getBookingsByUser(userId);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Cancella una prenotazione per ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.noContent().build();
    }
}
