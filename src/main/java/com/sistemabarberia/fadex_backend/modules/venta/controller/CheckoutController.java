package com.sistemabarberia.fadex_backend.modules.venta.controller;

import com.sistemabarberia.fadex_backend.auth.security.service.CustomUserDetails;
import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.modules.venta.dto.request.CheckoutRequestDTO;
import com.sistemabarberia.fadex_backend.modules.venta.service.ICheckoutService;
import com.sistemabarberia.fadex_backend.modules.venta.dto.response.VentaResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final ICheckoutService checkoutService;

    @PostMapping
    public ResponseEntity<ApiResponse<VentaResponseDTO>> procesar(
            @Valid @RequestBody CheckoutRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        VentaResponseDTO venta = checkoutService.procesar(userDetails.getUsuario().getIdUsuario(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Compra realizada correctamente", venta));
    }
}