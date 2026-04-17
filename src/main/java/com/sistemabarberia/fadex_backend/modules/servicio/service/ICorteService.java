package com.sistemabarberia.fadex_backend.modules.servicio.service;

import com.sistemabarberia.fadex_backend.modules.servicio.dto.request.CorteRequestDTO;
import com.sistemabarberia.fadex_backend.modules.servicio.dto.response.CorteResponseDTO;

import java.util.List;

public interface ICorteService {

    CorteResponseDTO crear(CorteRequestDTO dto);

    List<CorteResponseDTO> listar();

    List<CorteResponseDTO> listarPorCategoria(Integer categoriaId);

    CorteResponseDTO obtenerPorId(Integer id);

    CorteResponseDTO actualizar(Integer id, CorteRequestDTO dto);

    void eliminar(Integer id);
}