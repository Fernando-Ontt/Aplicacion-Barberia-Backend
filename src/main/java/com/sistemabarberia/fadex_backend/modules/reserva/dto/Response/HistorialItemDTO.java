package com.sistemabarberia.fadex_backend.modules.reserva.dto.Response;

import com.sistemabarberia.fadex_backend.modules.reserva.entity.TipoReserva;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialItemDTO {
    private String tipo;              // RESERVA | COMPRA
    private Long id;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String estadoReserva;     // EstadoReserva.name() o "PAGADA"
    private TipoReserva tipoReserva;  // null en compras
    private String nombreBarbero;
    private String nombreServicio;    // servicio, o productos en compras
    private BigDecimal total;
    private String observacion;
}
