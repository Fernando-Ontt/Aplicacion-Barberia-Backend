package com.sistemabarberia.fadex_backend.modules.venta.comprobante.service;

import com.sistemabarberia.fadex_backend.commons.exception.ResourceNotFoundException;
import com.sistemabarberia.fadex_backend.modules.venta.comprobante.generator.ComprobantePdfGenerator;
import com.sistemabarberia.fadex_backend.modules.venta.entity.Venta;
import com.sistemabarberia.fadex_backend.modules.venta.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ComprobanteVentaServiceImpl implements IComprobanteVentaService {

    private final VentaRepository ventaRepository;
    private final ComprobantePdfGenerator comprobantePdfGenerator;

    @Override
    @Transactional(readOnly = true)
    public byte[] generarPdf(Integer ventaId) {
        Venta venta = ventaRepository.findByIdWithDetalles(ventaId);
        if (venta == null) {
            throw new ResourceNotFoundException("Venta no encontrada");
        }
        return comprobantePdfGenerator.generar(venta);
    }
}
