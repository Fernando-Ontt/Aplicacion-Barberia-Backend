package com.sistemabarberia.fadex_backend.modules.venta.comprobante.controller;

import com.sistemabarberia.fadex_backend.auth.security.service.CustomUserDetails;
import com.sistemabarberia.fadex_backend.modules.venta.comprobante.service.ComprobanteClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/historial/compras/{ventaId}/comprobante")
@RequiredArgsConstructor
public class ComprobanteClienteController {

    private final ComprobanteClienteService comprobanteClienteService;

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> descargarPdf(
            @PathVariable Integer ventaId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        ComprobanteClienteService.BoletaPdf boleta = comprobanteClienteService
                .generarPdfDeCompra(userDetails.getUsuario().getIdUsuario(), ventaId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + boleta.nombreArchivo() + "\"")
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION)
                .contentType(MediaType.APPLICATION_PDF)
                .body(boleta.contenido());
    }
}