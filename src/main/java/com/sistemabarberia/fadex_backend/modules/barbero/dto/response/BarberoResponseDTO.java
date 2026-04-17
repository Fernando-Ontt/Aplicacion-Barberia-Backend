package com.sistemabarberia.fadex_backend.modules.barbero.dto.response;


import com.sistemabarberia.fadex_backend.modules.persona.entity.Persona;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class BarberoResponseDTO {
    private Integer barberoId;
    private Persona persona;
    private Integer experiencia;
    private LocalDate fechaIngreso;
    private String estado;
}
