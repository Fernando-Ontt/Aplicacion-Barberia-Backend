package com.sistemabarberia.fadex_backend.modules.venta.comprobante.service;

import com.sistemabarberia.fadex_backend.commons.exception.ResourceNotFoundException;
import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import com.sistemabarberia.fadex_backend.modules.cliente.repository.ClienteRepository;
import com.sistemabarberia.fadex_backend.modules.venta.comprobante.generator.ComprobantePdfGenerator;
import com.sistemabarberia.fadex_backend.modules.venta.entity.Venta;
import com.sistemabarberia.fadex_backend.modules.venta.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ComprobanteClienteService {

    private final ClienteRepository clienteRepository;
    private final VentaRepository ventaRepository;
    private final ComprobantePdfGenerator comprobantePdfGenerator;

    public record BoletaPdf(String nombreArchivo, byte[] contenido) {
    }

    @Transactional(readOnly = true)
    public BoletaPdf generarPdfDeCompra(Integer usuarioId, Integer ventaId) {
        Cliente cliente = clienteRepository.findByPersona_Usuario_IdUsuario(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        Venta venta = ventaRepository.findByIdWithDetalles(ventaId);

        // Si no existe o no es del cliente autenticado: mismo 404 (no revela que existe)
        if (venta == null
                || venta.getCliente() == null
                || !venta.getCliente().getClienteId().equals(cliente.getClienteId())) {
            throw new ResourceNotFoundException("Compra no encontrada");
        }

        byte[] pdf = comprobantePdfGenerator.generar(venta);
        String nombre = "boleta-" + venta.getSerieComprobante() + "-" + venta.getNumeroComprobante() + ".pdf";
        return new BoletaPdf(nombre, pdf);
    }
}