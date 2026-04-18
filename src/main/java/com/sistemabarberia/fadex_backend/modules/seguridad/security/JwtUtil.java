package com.sistemabarberia.fadex_backend.modules.seguridad.security;
import io.jsonwebtoken.*;

import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtil {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationInSeconds;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(CustomUserDetails userDetails) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("userId", userDetails.getUsuario().getIdUsuario())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(jwtExpirationInSeconds)))
                .signWith(getSigningKey())
                .compact();
    }

    public Integer getUserIdFromToken(String token) {
        return extractClaim(token, claims -> claims.get("userId", Integer.class));
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return resolver.apply(claims);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Integer userIdFromToken = claims.get("userId", Integer.class);

            if (userDetails instanceof CustomUserDetails customUser) {
                Integer userIdFromDb = customUser.getUsuario().getIdUsuario();
                return userIdFromToken.equals(userIdFromDb);
            }
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT inválido: {}", e.getMessage());
            return false;
        }
    }
}
