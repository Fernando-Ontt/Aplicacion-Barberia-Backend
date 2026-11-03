package com.sistemabarberia.fadex_backend.modules.reserva.service;

import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import com.sistemabarberia.fadex_backend.commons.exception.ResourceNotFoundException;
import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import com.sistemabarberia.fadex_backend.modules.cliente.repository.ClienteRepository;
import com.sistemabarberia.fadex_backend.modules.reserva.dto.Response.HistorialItemDTO;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.EstadoReserva;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.Reserva;
import com.sistemabarberia.fadex_backend.modules.reserva.repository.ReservaRepository;
import com.sistemabarberia.fadex_backend.modules.venta.entity.Venta;
import com.sistemabarberia.fadex_backend.modules.venta.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistorialClienteService {

    private static final String ESTADO_COMPRA = "PAGADA";

    private final ClienteRepository clienteRepository;
    private final ReservaRepository reservaRepository;
    private final VentaRepository ventaRepository;

    private record Entrada(LocalDateTime orden, HistorialItemDTO dto) {
    }

    @Transactional(readOnly = true)
    public Page<HistorialItemDTO> listar(Integer usuarioId, String estado,
                                         LocalDate desde, LocalDate hasta, Pageable pageable) {

        Cliente cliente = clienteRepository.findByPersona_Usuario_IdUsuario(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        boolean soloCompras = ESTADO_COMPRA.equalsIgnoreCase(estado);
        EstadoReserva estadoReserva = null;
        if (estado != null && !estado.isBlank() && !soloCompras) {
            try {
                estadoReserva = EstadoReserva.valueOf(estado.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Estado inválido: " + estado, HttpStatus.BAD_REQUEST);
            }
        }
        boolean soloReservas = estadoReserva != null;

        List<Entrada> entradas = new ArrayList<>();

        // ---------- RESERVAS ----------
        if (!soloCompras) {
            List<Reserva> reservas = reservaRepository
                    .findHistorialByClienteFiltros(cliente.getClienteId(), estadoReserva, desde, hasta, Pageable.unpaged())
                    .getContent();

            for (Reserva r : reservas) {
                String barbero = (r.getBarbero() != null && r.getBarbero().getPersona() != null)
                        ? r.getBarbero().getPersona().getNombre() + " " + r.getBarbero().getPersona().getApellido()
                        : "Sin asignar";
                String servicio = r.getServicio() != null ? r.getServicio().getNombre() : "Sin servicio";

                HistorialItemDTO dto = HistorialItemDTO.builder()
                        .tipo("RESERVA")
                        .id(r.getId())
                        .fecha(r.getFecha())
                        .horaInicio(r.getHoraInicio())
                        .horaFin(r.getHoraFin())
                        .estadoReserva(r.getEstadoReserva().name())
                        .tipoReserva(r.getTipoReserva())
                        .nombreBarbero(barbero)
                        .nombreServicio(servicio)
                        .total(r.getTotal())
                        .observacion(r.getObservacion())
                        .build();

                LocalTime hora = r.getHoraInicio() != null ? r.getHoraInicio() : LocalTime.MIDNIGHT;
                entradas.add(new Entrada(r.getFecha().atTime(hora), dto));
            }
        }

        // ---------- COMPRAS DE PRODUCTOS ----------
        if (!soloReservas) {
            for (Venta v : ventaRepository.findByCliente_ClienteId(cliente.getClienteId())) {
                if (v.getFecha() == null) continue;
                LocalDate dia = v.getFecha().toLocalDate();
                if (desde != null && dia.isBefore(desde)) continue;
                if (hasta != null && dia.isAfter(hasta)) continue;

                String productos = v.getDetalles() == null ? "" : v.getDetalles().stream()
                        .filter(d -> d.getProducto() != null)
                        .map(d -> d.getProducto().getNombre() + " x" + d.getCantidad())
                        .collect(Collectors.joining(", "));
                if (productos.isBlank()) continue; // venta solo de servicio: ya sale como reserva

                String barbero = (v.getBarbero() != null && v.getBarbero().getPersona() != null)
                        ? v.getBarbero().getPersona().getNombre() + " " + v.getBarbero().getPersona().getApellido()
                        : "—";

                BigDecimal totalProductos = v.getDetalles().stream()
                        .filter(d -> d.getProducto() != null)
                        .map(d -> d.getPrecioUnitario().multiply(BigDecimal.valueOf(d.getCantidad())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                HistorialItemDTO dto = HistorialItemDTO.builder()
                        .tipo("COMPRA")
                        .id(v.getVentaId().longValue())
                        .fecha(dia)
                        .horaInicio(v.getFecha().toLocalTime())
                        .estadoReserva(ESTADO_COMPRA)
                        .nombreBarbero(barbero)
                        .nombreServicio(productos)
                        .total(totalProductos)
                        .build();

                entradas.add(new Entrada(v.getFecha(), dto));
            }
        }

        // ---------- ORDEN Y PAGINACIÓN ----------
        entradas.sort(Comparator.comparing(Entrada::orden).reversed());

        int inicio = (int) Math.min(pageable.getOffset(), entradas.size());
        int fin = Math.min(inicio + pageable.getPageSize(), entradas.size());
        List<HistorialItemDTO> pagina = entradas.subList(inicio, fin).stream().map(Entrada::dto).toList();

        return new PageImpl<>(pagina, pageable, entradas.size());
    }
}