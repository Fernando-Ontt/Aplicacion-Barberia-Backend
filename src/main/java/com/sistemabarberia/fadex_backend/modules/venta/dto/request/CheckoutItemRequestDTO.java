package com.sistemabarberia.fadex_backend.modules.venta.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutItemRequestDTO {

    @NotNull(message = "Producto obligatorio")
    private Integer productoId;

    @NotNull(message = "Cantidad obligatoria")
    @Min(value = 1, message = "Cantidad mínima 1")
    private Integer cantidad;
}
