package com.sistemabarberia.fadex_backend.modules.venta.controller;

import com.sistemabarberia.fadex_backend.auth.security.service.CustomUserDetails;
import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.modules.venta.dto.request.CheckoutReservaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.venta.dto.response.VentaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.venta.service.impl.CheckoutReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/checkout/reserva")
@RequiredArgsConstructor
public class CheckoutReservaController {

    private final CheckoutReservaService checkoutReservaService;

    @PostMapping
    public ResponseEntity<ApiResponse<VentaResponseDTO>> pagar(
            @Valid @RequestBody CheckoutReservaRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        VentaResponseDTO venta = checkoutReservaService.pagar(userDetails.getUsuario().getIdUsuario(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Reserva pagada correctamente", venta));
    }
}
