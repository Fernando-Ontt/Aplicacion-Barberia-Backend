package com.sistemabarberia.fadex_backend.modules.seguridad.controller;

import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.modules.seguridad.dto.request.RegisterRequest;
import com.sistemabarberia.fadex_backend.modules.seguridad.dto.response.UsuarioResponse;
import com.sistemabarberia.fadex_backend.modules.seguridad.service.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final IUsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<ApiResponse<UsuarioResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(
                "Usuarios obtenidos correctamente",
                usuarioService.getAll(pageable)
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponse>> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok(
                "Usuario encontrado",
                usuarioService.getById(id)
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponse>> update(@PathVariable Integer id,
                                                               @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(
                "Usuario actualizado correctamente",
                usuarioService.update(id, request)
        ));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UsuarioResponse>> create(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(
                "Usuario creado correctamente",
                usuarioService.create(request)
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Integer id) {
        usuarioService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Usuario eliminado correctamente", (Void) null));
    }
}