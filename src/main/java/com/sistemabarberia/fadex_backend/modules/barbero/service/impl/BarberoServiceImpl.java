package com.sistemabarberia.fadex_backend.modules.barbero.service.impl;


import com.sistemabarberia.fadex_backend.modules.barbero.dto.response.BarberoResponseDTO;
import com.sistemabarberia.fadex_backend.modules.barbero.mapper.BarberoMapper;
import com.sistemabarberia.fadex_backend.modules.barbero.repository.BarberoRepository;
import com.sistemabarberia.fadex_backend.modules.barbero.service.IBarberoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BarberoServiceImpl implements IBarberoService {

    @Autowired
    private BarberoRepository repository;

    @Autowired
    private BarberoMapper mapper;


    @Override
    public Page<BarberoResponseDTO> listarBarberos(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toResponseDTO);
    }
}
