package com.sistemabarberia.fadex_backend.auth.authentication.controller;

import com.sistemabarberia.fadex_backend.auth.authentication.dto.request.LoginRequest;
import com.sistemabarberia.fadex_backend.auth.authentication.dto.response.TokenResponse;
import com.sistemabarberia.fadex_backend.auth.authentication.service.AuthService;
import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login( @RequestBody @Valid LoginRequest loginRequest){
        TokenResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.ok("Login correctamente", response));
    }

}
