package com.example.booking.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private final JWTVerifier verifier;

    public JwtUtil(@Value("${jwt.secret}") String secret, @Value("${jwt.algorithm:HS256}") String alg) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        this.verifier = JWT.require(algorithm).build();
    }

    public DecodedJWT verifyToken(String token) {
        return verifier.verify(token); // lancia eccezione se invalid
    }

    public String getUsernameFromToken(String token) {
        DecodedJWT jwt = verifyToken(token);
        return jwt.getSubject(); // 'sub' claim
    }
}
