package com.sistemabarberia.fadex_backend.modules.servicio.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CorteResponseDTO {

    private Integer corteId;
    private String nombre;
    private BigDecimal precio;
    private Integer categoriaId;
    private String categoriaNombre;
}