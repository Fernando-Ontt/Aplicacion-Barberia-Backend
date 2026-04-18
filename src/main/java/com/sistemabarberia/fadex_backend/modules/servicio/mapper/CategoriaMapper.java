package com.sistemabarberia.fadex_backend.modules.servicio.mapper;

import com.sistemabarberia.fadex_backend.modules.servicio.dto.request.CategoriaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.servicio.dto.response.CategoriaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.servicio.entity.Categoria;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {

    @Mapping(target = "categoriaId", ignore = true)
    Categoria toEntity(CategoriaRequestDTO dto);

    CategoriaResponseDTO toResponse(Categoria categoria);

    List<CategoriaResponseDTO> toResponseList(List<Categoria> categorias);
}