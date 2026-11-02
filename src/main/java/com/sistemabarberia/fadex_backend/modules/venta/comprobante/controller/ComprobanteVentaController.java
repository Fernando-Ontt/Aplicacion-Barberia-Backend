package com.sistemabarberia.fadex_backend.modules.venta.comprobante.controller;

import com.sistemabarberia.fadex_backend.modules.venta.comprobante.service.IComprobanteVentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ventas/{ventaId}/comprobante")
@RequiredArgsConstructor
public class ComprobanteVentaController {

    private final IComprobanteVentaService comprobanteVentaService;

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> descargarPdf(@PathVariable Integer ventaId) {
        byte[] pdf = comprobanteVentaService.generarPdf(ventaId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"comprobante-venta-" + ventaId + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
