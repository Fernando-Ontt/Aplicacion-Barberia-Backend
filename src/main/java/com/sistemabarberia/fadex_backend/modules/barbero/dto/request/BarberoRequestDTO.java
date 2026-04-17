package com.sistemabarberia.fadex_backend.modules.barbero.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class BarberoRequestDTO {

    @NotNull(message = "El personaId es obligatorio")
    @Positive(message = "El personaId debe ser un número positivo")
    private Integer personaId;

    @NotNull(message = "La experiencia es obligatoria")
    @Min(value = 0, message = "La experiencia no puede ser negativa")
    @Max(value = 60, message = "La experiencia no puede superar 60 años")
    private Integer experiencia;

    private boolean ocupado;
}