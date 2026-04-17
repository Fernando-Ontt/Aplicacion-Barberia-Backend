package com.sistemabarberia.fadex_backend.modules.persona.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonPropertyOrder({"personaId", "nombre", "apellido", "telefono", "email"})
public class PersonaResponseDTO {
    private Integer personaId;
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
}
