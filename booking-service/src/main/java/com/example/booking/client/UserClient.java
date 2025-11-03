package com.example.booking.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class UserClient {

    private final WebClient webClient;

    public UserClient(@Value("${user-service.url}") String baseUrl) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    // supponiamo che user-service esponga GET /users/by-username/{username} che ritorna JSON con id
    public Mono<UserDto> getByUsername(String username) {
        return webClient.get()
                .uri("/users/by-username/{username}", username)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(UserDto.class);
    }

    public record UserDto(Long id, String username, String email) {}

}
