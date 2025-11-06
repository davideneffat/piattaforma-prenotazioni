package com.example.booking.service;

import com.example.booking.client.AvailabilityClient;
import com.example.booking.client.UserClient;
import com.example.booking.model.Booking;
import com.example.booking.repository.BookingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserClient userClient;
    private final AvailabilityClient availabilityClient;

    public BookingService(BookingRepository bookingRepository, UserClient userClient, AvailabilityClient availabilityClient) {
        this.bookingRepository = bookingRepository;
        this.userClient = userClient;
        this.availabilityClient = availabilityClient;
    }
    
    @Transactional
    public Booking createBooking(Booking booking, String username) {
        // 1- Ottenere l'ID utente dal user-service
        UserClient.UserDto userDto = userClient.getByUsername(username)
            .blockOptional() // .block() per attendere la risposta
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User '" + username + "' not found"));
        
        booking.setUserId(userDto.id());

        // 2- Verificare la disponibilità chiamando il servizio Go
        var checkRequest = new AvailabilityClient.AvailabilityCheckRequest(booking.getServiceName(), booking.getBookingTime());
        AvailabilityClient.AvailabilityCheckResponse availability = availabilityClient.checkAvailability(checkRequest)
            .blockOptional()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Availability check service failed"));

        if (!availability.available()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Time slot not available: " + availability.reason());
        }

        // 3- Addebitare il costo sul wallet dell'utente (costo fisso di 10.0 per ora)
        double bookingCost = 10.0;
        var chargeRequest = new AvailabilityClient.ChargeRequest(userDto.id(), bookingCost);
        AvailabilityClient.ChargeResponse chargeResponse = availabilityClient.chargeUser(chargeRequest)
            .blockOptional()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Payment service failed"));

        if (!chargeResponse.success()) {
            throw new ResponseStatusException(HttpStatus.PAYMENT_REQUIRED, "Payment failed: " + chargeResponse.reason());
        }

        // 4- Solo se tutto è andato a buon fine, salva la prenotazione
        return bookingRepository.save(booking);
    }

    @Transactional(readOnly = true) 
    public List<Booking> getBookingsByUser(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    @Transactional
    public void cancelBooking(Long id) {
        bookingRepository.deleteById(id);
    }
}
