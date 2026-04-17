package com.sistemabarberia.fadex_backend.modules.cliente.service;

import com.sistemabarberia.fadex_backend.modules.barbero.dto.request.BarberoRequestDTO;
import com.sistemabarberia.fadex_backend.modules.barbero.dto.response.BarberoResponseDTO;
import com.sistemabarberia.fadex_backend.modules.cliente.dto.request.ClienteRequestDTO;
import com.sistemabarberia.fadex_backend.modules.cliente.dto.response.ClienteResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IClienteService {

    //Paginacion
    Page<ClienteResponseDTO> listarClientes(Pageable pageable);


    //Crear
    ClienteResponseDTO crearCliente(ClienteRequestDTO dto);

    //Eliminar
    ClienteResponseDTO eliminar(Integer id);

    //Buscar
    ClienteResponseDTO buscarCliente(Integer id);
}
