package com.sistemabarberia.fadex_backend.modules.seguridad.dto.response;

import com.sistemabarberia.fadex_backend.modules.persona.entity.Persona;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {
    private Integer idUsuario;
    private String username;
    private String rol;
}