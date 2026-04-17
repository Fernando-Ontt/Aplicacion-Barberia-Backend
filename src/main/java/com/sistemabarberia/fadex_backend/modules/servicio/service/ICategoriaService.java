package com.sistemabarberia.fadex_backend.modules.servicio.service;

import com.sistemabarberia.fadex_backend.modules.servicio.dto.request.CategoriaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.servicio.dto.response.CategoriaResponseDTO;

import java.util.List;

public interface ICategoriaService {

    CategoriaResponseDTO crear(CategoriaRequestDTO dto);

    List<CategoriaResponseDTO> listar();

    CategoriaResponseDTO obtenerPorId(Integer id);

    CategoriaResponseDTO actualizar(Integer id, CategoriaRequestDTO dto);

    void eliminar(Integer id);
}