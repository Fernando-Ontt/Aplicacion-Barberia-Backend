package com.sistemabarberia.fadex_backend.modules.seguridad.service;

import com.sistemabarberia.fadex_backend.modules.seguridad.dto.request.RegisterRequest;
import com.sistemabarberia.fadex_backend.modules.seguridad.dto.response.UsuarioResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IUsuarioService {
    Page<UsuarioResponse> getAll(Pageable pageable);
    UsuarioResponse getById(Integer id);
    UsuarioResponse update(Integer id, RegisterRequest request);
    UsuarioResponse create(RegisterRequest request);
    void delete(Integer id);
}
