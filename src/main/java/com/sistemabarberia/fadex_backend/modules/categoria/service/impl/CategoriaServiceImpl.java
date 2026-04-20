package com.sistemabarberia.fadex_backend.modules.categoria.service.impl;

import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import com.sistemabarberia.fadex_backend.modules.categoria.dto.CategoriaFiltro;
import com.sistemabarberia.fadex_backend.modules.categoria.dto.response.CategoriaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.categoria.entity.Categoria;
import com.sistemabarberia.fadex_backend.modules.categoria.mapper.CategoriaMapper;
import com.sistemabarberia.fadex_backend.modules.categoria.repository.CategoriaRepository;
import com.sistemabarberia.fadex_backend.modules.categoria.service.ICategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements ICategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    @Override
    public List<CategoriaResponseDTO> listar() {
        Map<Long, CategoriaResponseDTO> mapa = construirArbol();
        return mapa.values().stream().filter(cat -> cat.getPadreId() == null).toList();
    }

    @Override
    public List<CategoriaResponseDTO> listarConFiltro(CategoriaFiltro filtro) {
        Map<Long, CategoriaResponseDTO> mapa = construirArbol();
        List<CategoriaResponseDTO> raiz = mapa.values().stream().filter(cat -> cat.getPadreId() == null).toList();
        if (filtro == null) return raiz;
        return filtrarArbol(raiz, filtro);
    }

    @Override
    public CategoriaResponseDTO obtenerPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Categoría no encontrada", HttpStatus.NOT_FOUND));
        Map<Long, CategoriaResponseDTO> mapa = construirArbol();
        if (categoria.getPadre() != null) {
            return mapa.get(categoria.getPadre().getId());
        }
        return mapa.get(id);
    }

    private Map<Long, CategoriaResponseDTO> construirArbol() {
        List<Categoria> categorias = categoriaRepository.findByEstadoTrue();
        List<CategoriaResponseDTO> dtos = categoriaMapper.toResponseList(categorias);
        Map<Long, CategoriaResponseDTO> mapa = new HashMap<>();
        for (CategoriaResponseDTO dto : dtos) {
            dto.setSubcategorias(new ArrayList<>());
            mapa.put(dto.getId(), dto);
        }
        for (CategoriaResponseDTO dto : dtos) {
            if (dto.getPadreId() != null) {
                CategoriaResponseDTO padre = mapa.get(dto.getPadreId());
                if (padre != null) {
                    padre.getSubcategorias().add(dto);
                }
            }
        }
        return mapa;
    }

    private List<CategoriaResponseDTO> filtrarArbol(List<CategoriaResponseDTO> lista, CategoriaFiltro filtro) {
        List<CategoriaResponseDTO> resultado = new ArrayList<>();
        for (CategoriaResponseDTO cat : lista) {
            boolean cumple = true;
            if (filtro.getPadreId() != null && (cat.getPadreId() == null || !cat.getPadreId().equals(filtro.getPadreId()))) {
                cumple = false;
            }
            if (filtro.getNombre() != null && !cat.getNombre().toLowerCase().contains(filtro.getNombre().toLowerCase().trim())) {
                cumple = false;
            }
            if (filtro.getEstado() != null && cat.isEstado() != filtro.getEstado()) {
                cumple = false;
            }
            if (filtro.getTipo() != null && !cat.getTipo().equals(filtro.getTipo())) {
                cumple = false;
            }
            List<CategoriaResponseDTO> hijosFiltrados = filtrarArbol(cat.getSubcategorias(), filtro);
            if (cumple) {
                cat.setSubcategorias(hijosFiltrados);
                resultado.add(cat);
            }
        }
        return resultado;
    }

}