package com.sistemabarberia.fadex_backend.modules.servicio.service.impl;

import com.sistemabarberia.fadex_backend.modules.servicio.dto.request.CategoriaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.servicio.dto.response.CategoriaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.servicio.entity.Categoria;
import com.sistemabarberia.fadex_backend.modules.servicio.mapper.CategoriaMapper;
import com.sistemabarberia.fadex_backend.modules.servicio.repository.CategoriaRepository;
import com.sistemabarberia.fadex_backend.modules.servicio.service.ICategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements ICategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    @Override
    public CategoriaResponseDTO crear(CategoriaRequestDTO dto) {

        if (categoriaRepository.existsByNombre(dto.getNombre())) {
            throw new RuntimeException("Categoría ya existe");
        }

        Categoria categoria = categoriaMapper.toEntity(dto);
        return categoriaMapper.toResponse(categoriaRepository.save(categoria));
    }

    @Override
    public List<CategoriaResponseDTO> listar() {
        return categoriaMapper.toResponseList(categoriaRepository.findAll());
    }

    @Override
    public CategoriaResponseDTO obtenerPorId(Integer id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        return categoriaMapper.toResponse(categoria);
    }

    @Override
    public CategoriaResponseDTO actualizar(Integer id, CategoriaRequestDTO dto) {

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        if (!categoria.getNombre().equals(dto.getNombre()) &&
                categoriaRepository.existsByNombre(dto.getNombre())) {
            throw new RuntimeException("Categoría ya existe");
        }

        categoria.setNombre(dto.getNombre());

        return categoriaMapper.toResponse(categoriaRepository.save(categoria));
    }

    @Override
    public void eliminar(Integer id) {

        if (!categoriaRepository.existsById(id)) {
            throw new RuntimeException("Categoría no encontrada");
        }

        categoriaRepository.deleteById(id);
    }
}