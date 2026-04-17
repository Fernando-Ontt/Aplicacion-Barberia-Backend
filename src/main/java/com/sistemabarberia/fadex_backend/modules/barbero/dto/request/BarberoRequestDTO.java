package com.sistemabarberia.fadex_backend.modules.barbero.dto.request;


import lombok.Data;

import java.time.LocalDate;

@Data
public class BarberoRequestDTO {
    private Integer experiencia;
    private LocalDate fechaIngreso;
    private String estado;
}
