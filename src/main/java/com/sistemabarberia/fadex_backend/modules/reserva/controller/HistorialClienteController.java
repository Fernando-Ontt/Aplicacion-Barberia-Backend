package com.sistemabarberia.fadex_backend.modules.reserva.controller;
import com.sistemabarberia.fadex_backend.auth.security.service.CustomUserDetails;
import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;

import com.sistemabarberia.fadex_backend.modules.reserva.dto.Response.HistorialItemDTO;
import com.sistemabarberia.fadex_backend.modules.reserva.service.HistorialClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/historial")
@RequiredArgsConstructor
public class HistorialClienteController {

    private final HistorialClienteService historialService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<HistorialItemDTO>>> listar(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Page<HistorialItemDTO> result = historialService.listar(
                userDetails.getUsuario().getIdUsuario(), estado, desde, hasta, PageRequest.of(page, size));

        return ResponseEntity.ok(ApiResponse.ok("Historial obtenido correctamente", result));
    }
}
