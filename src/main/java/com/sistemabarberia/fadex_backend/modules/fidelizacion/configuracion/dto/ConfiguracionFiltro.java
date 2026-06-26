package com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionFiltro {
    private Boolean activa;
    private Integer categoriaId;
    private Integer ruletaId;
    private String categoriaNombre;
    private String ruletaNombre;
    private Integer metaDesde;
    private Integer metaHasta;
}
