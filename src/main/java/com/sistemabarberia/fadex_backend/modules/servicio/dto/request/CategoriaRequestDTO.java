package com.sistemabarberia.fadex_backend.modules.servicio.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaRequestDTO {

    @NotBlank(message = "Nombre obligatorio")
    @Size(max = 100, message = "Máx 100 caracteres")
    private String nombre;
}