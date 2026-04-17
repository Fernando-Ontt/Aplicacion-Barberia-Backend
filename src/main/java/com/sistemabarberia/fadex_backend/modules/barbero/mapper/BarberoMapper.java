package com.sistemabarberia.fadex_backend.modules.barbero.mapper;

import com.sistemabarberia.fadex_backend.modules.barbero.dto.response.BarberoResponseDTO;
import com.sistemabarberia.fadex_backend.modules.barbero.entity.Barbero;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface BarberoMapper {

    @Mapping(source = "barberoId", target = "barberoId")
    BarberoResponseDTO toResponseDTO(Barbero barbero);

}
