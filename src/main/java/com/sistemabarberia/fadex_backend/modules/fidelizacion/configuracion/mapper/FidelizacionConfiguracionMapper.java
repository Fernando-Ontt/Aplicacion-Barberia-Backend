package com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.mapper;

import com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.dto.request.ConfiguracionRequestDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.dto.response.ConfiguracionResponseDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.entity.FidelizacionConfiguracion;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FidelizacionConfiguracionMapper {
    FidelizacionConfiguracion toEntity(ConfiguracionRequestDTO request);
    ConfiguracionResponseDTO toResponse(FidelizacionConfiguracion entity);
    ConfiguracionResponseDTO toListResponse(FidelizacionConfiguracion entity);

}