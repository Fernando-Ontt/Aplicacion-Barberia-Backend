package com.sistemabarberia.fadex_backend.modules.seguridad.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RegisterRequest {
    private String username;
    private String password;
    private Integer idRol;
    private Integer idPersona;
}
