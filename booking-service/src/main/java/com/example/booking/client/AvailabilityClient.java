package com.example.booking.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;

@Component
public class AvailabilityClient {

    private final WebClient webClient;

    public AvailabilityClient(@Qualifier("availabilityWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    // DTO per le risposte e le richieste
    public record AvailabilityCheckRequest(String serviceName, LocalDateTime bookingTime) {}
    public record AvailabilityCheckResponse(boolean available, String reason) {}
    public record ChargeRequest(Long userId, double amount) {}
    public record ChargeResponse(boolean success, String reason) {}

    public Mono<AvailabilityCheckResponse> checkAvailability(AvailabilityCheckRequest request) {
        return webClient.post()
                .uri("/availability/check")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AvailabilityCheckResponse.class);
    }

    public Mono<ChargeResponse> chargeUser(ChargeRequest request) {
        return webClient.post()
                .uri("/payments/charge")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ChargeResponse.class);
    }
}