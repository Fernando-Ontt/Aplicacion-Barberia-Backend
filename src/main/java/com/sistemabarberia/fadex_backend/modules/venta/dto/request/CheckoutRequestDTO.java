package com.sistemabarberia.fadex_backend.modules.venta.dto.request;

import com.sistemabarberia.fadex_backend.modules.pagos.entity.MetodoPago;
import com.sistemabarberia.fadex_backend.modules.venta.entity.TipoComprobante;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutRequestDTO {
    @NotEmpty(message = "El carrito está vacío")
    @Valid
    private List<CheckoutItemRequestDTO> items;

    @NotNull(message = "Método de pago obligatorio")
    private MetodoPago metodoPago;

    // Opcional: si no viene, se usa BOLETA
    private TipoComprobante tipoComprobante;
}
