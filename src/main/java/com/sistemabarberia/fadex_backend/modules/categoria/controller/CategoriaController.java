package com.sistemabarberia.fadex_backend.modules.categoria.controller;

import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.modules.categoria.dto.CategoriaFiltro;
import com.sistemabarberia.fadex_backend.modules.categoria.dto.request.CategoriaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.categoria.dto.response.CategoriaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.categoria.service.ICategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final ICategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoriaResponseDTO>>> listar(CategoriaFiltro filtro) {
        List<CategoriaResponseDTO> data = categoriaService.listarConFiltro(filtro);
        return ResponseEntity.ok(ApiResponse.ok("Categorías filtradas correctamente", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoriaResponseDTO>> obtenerPorId(@PathVariable Long id) {
        CategoriaResponseDTO data = categoriaService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.ok("Categoría encontrada", data));
    }

}