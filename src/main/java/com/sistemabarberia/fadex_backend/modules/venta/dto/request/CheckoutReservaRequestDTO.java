package com.sistemabarberia.fadex_backend.modules.venta.dto.request;

import com.sistemabarberia.fadex_backend.modules.pagos.entity.MetodoPago;
import com.sistemabarberia.fadex_backend.modules.venta.entity.TipoComprobante;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutReservaRequestDTO {

    @NotNull(message = "La reserva es obligatoria")
    private Long reservaId;

    @NotNull(message = "Método de pago obligatorio")
    private MetodoPago metodoPago;

    // Opcional: si no viene, se usa BOLETA
    private TipoComprobante tipoComprobante;
}