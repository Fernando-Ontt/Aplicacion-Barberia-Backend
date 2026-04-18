package com.sistemabarberia.fadex_backend.modules.servicio.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaResponseDTO {

    private Integer categoriaId;
    private String nombre;
}
