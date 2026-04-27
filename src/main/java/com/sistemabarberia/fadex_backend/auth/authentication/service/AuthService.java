package com.sistemabarberia.fadex_backend.auth.authentication.service;


import com.sistemabarberia.fadex_backend.auth.authentication.dto.request.LoginRequest;
import com.sistemabarberia.fadex_backend.auth.authentication.dto.response.TokenResponse;
import com.sistemabarberia.fadex_backend.auth.security.jwt.JwtProperties;
import com.sistemabarberia.fadex_backend.auth.security.jwt.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;


    public TokenResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);

        String username = jwtService.extractClaim(token, Claims::getSubject);
        long expiredIn = jwtProperties.getExpiration() / 1000;


        List<String> roles = jwtService.extractClaim(token,
                claims -> claims.get("roles", List.class));

        List<String> permisos = jwtService.extractClaim(token,
                claims -> claims.get("permisos", List.class));

        String rol = (roles != null && !roles.isEmpty())
                ? roles.get(0).replace("ROLE_", "")
                : null;

        return new TokenResponse(token, "bearer", expiredIn, username, rol, permisos);
    }
}
