package com.sistemabarberia.fadex_backend.modules.venta.service.impl;

import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import com.sistemabarberia.fadex_backend.commons.exception.ResourceNotFoundException;
import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import com.sistemabarberia.fadex_backend.modules.cliente.repository.ClienteRepository;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.EstadoReserva;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.Reserva;
import com.sistemabarberia.fadex_backend.modules.reserva.repository.ReservaRepository;

import com.sistemabarberia.fadex_backend.modules.venta.dto.request.VentaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.venta.dto.request.CheckoutReservaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.venta.dto.response.VentaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.venta.entity.TipoComprobante;
import com.sistemabarberia.fadex_backend.modules.venta.service.IVentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CheckoutReservaService {

    private final ClienteRepository clienteRepository;
    private final ReservaRepository reservaRepository;
    private final IVentaService ventaService;

    @Transactional
    public VentaResponseDTO pagar(Integer usuarioId, CheckoutReservaRequestDTO request) {

        Cliente cliente = clienteRepository.findByPersona_Usuario_IdUsuario(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        // Si no existe o es de otro cliente: mismo 404
        Reserva reserva = reservaRepository.findById(request.getReservaId())
                .filter(r -> r.getCliente() != null
                        && r.getCliente().getClienteId().equals(cliente.getClienteId()))
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        if (reserva.getEstadoReserva() != EstadoReserva.PENDIENTE_PAGO) {
            throw new BusinessException(
                    "La reserva no está pendiente de pago. Estado actual: " + reserva.getEstadoReserva(),
                    HttpStatus.BAD_REQUEST);
        }

        VentaRequestDTO venta = VentaRequestDTO.builder()
                .reservaId(reserva.getId())
                .barberoId(reserva.getBarbero() != null ? reserva.getBarbero().getBarberoId() : null)
                .tipoComprobante(request.getTipoComprobante() != null
                        ? request.getTipoComprobante()
                        : TipoComprobante.BOLETA)
                .metodoPago(request.getMetodoPago())
                .build();

        VentaResponseDTO response = ventaService.crear(venta);

        // crear() deja la reserva FINALIZADA; aquí solo se paga, así que vuelve a CONFIRMADA
        reserva.setEstadoReserva(EstadoReserva.CONFIRMADA);
        reservaRepository.save(reserva);

        return response;
    }
}