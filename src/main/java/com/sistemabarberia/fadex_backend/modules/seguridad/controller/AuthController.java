package com.sistemabarberia.fadex_backend.modules.seguridad.controller;

import com.sistemabarberia.fadex_backend.modules.seguridad.service.impl.AuthServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> request) {
        try {
            String token = authService.login(
                    request.get("username"),
                    request.get("password")
            );

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "tipo", "Bearer"
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}