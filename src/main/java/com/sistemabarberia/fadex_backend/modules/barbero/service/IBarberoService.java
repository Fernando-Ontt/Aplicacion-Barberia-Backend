package com.sistemabarberia.fadex_backend.modules.barbero.service;

import com.sistemabarberia.fadex_backend.modules.barbero.dto.response.BarberoResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IBarberoService {

    //Paginacion
    Page<BarberoResponseDTO> listarBarberos(Pageable pageable);
}
