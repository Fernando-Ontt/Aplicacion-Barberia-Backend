package com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionResponseDTO {
    private Integer configuracionId;
    private Integer categoriaId;
    private String categoriaNombre;
    private Boolean activa;
    private Integer meta;
    private Boolean mostrarSiempre;
    private Boolean crearTarjetaAutomatica;
    private Integer ruletaId;
    private String ruletaNombre;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}