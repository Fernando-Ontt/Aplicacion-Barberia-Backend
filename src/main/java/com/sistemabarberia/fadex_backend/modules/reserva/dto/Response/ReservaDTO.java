package com.sistemabarberia.fadex_backend.modules.reserva.dto.Response;

import com.sistemabarberia.fadex_backend.modules.reserva.entity.EstadoReserva;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.TipoReserva;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record ReservaDTO(
        Long reservaId,
        Integer clienteId,
        String clienteNombre,
        String barberoNombre,
        String servicio,
        LocalDate fecha,
        LocalTime horaInicio,
        LocalTime horaFin,
        TipoReserva tipoReserva,
        BigDecimal total,
        EstadoReserva estadoReserva,
        Boolean recordatorioEnviado
) {}
