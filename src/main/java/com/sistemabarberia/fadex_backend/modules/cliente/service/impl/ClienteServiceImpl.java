package com.sistemabarberia.fadex_backend.modules.cliente.service.impl;

import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import com.sistemabarberia.fadex_backend.modules.barbero.repository.BarberoRepository;
import com.sistemabarberia.fadex_backend.modules.cliente.dto.request.ClienteRequestDTO;
import com.sistemabarberia.fadex_backend.modules.cliente.dto.response.ClienteResponseDTO;
import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import com.sistemabarberia.fadex_backend.modules.cliente.mapper.ClienteMapper;
import com.sistemabarberia.fadex_backend.modules.cliente.repository.ClienteRepository;
import com.sistemabarberia.fadex_backend.modules.cliente.service.IClienteService;
import com.sistemabarberia.fadex_backend.modules.persona.entity.Persona;
import com.sistemabarberia.fadex_backend.modules.persona.repository.PersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ClienteServiceImpl implements IClienteService {
    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private BarberoRepository barberoRepository;

    @Autowired
    private PersonaRepository personaRepository;
    @Autowired
    private ClienteMapper mapper;


    //LISTAR
    @Override
    public Page<ClienteResponseDTO> listarClientes(Pageable pageable) {
        return clienteRepository.findAll(pageable)
                .map(mapper::toResponseDTO);
    }


    //CREAR
    @Override
    public ClienteResponseDTO crearCliente(ClienteRequestDTO dto) {
        //Verifica que la persona existe
        Persona persona = personaRepository.findById(dto.getPersonaId())
                .orElseThrow(() -> new BusinessException(
                        "Persona no encontrada con id: " + dto.getPersonaId(),
                        HttpStatus.NOT_FOUND
                ));
        //Verifica que la persona no está ya asignada a otro Cliente
        if (clienteRepository.existsByPersona_PersonaId(dto.getPersonaId())) {
            throw new BusinessException(
                    "Esta persona ya está registrada como Cliente",
                    HttpStatus.CONFLICT
            );
        }
        //Verifica que la persona no está ya asignada a un barbero
        if (barberoRepository.existsByPersona_PersonaId(dto.getPersonaId())) {
            throw new BusinessException(
                    "Esta persona ya está registrada como Barbero",
                    HttpStatus.CONFLICT
            );
        }

        Cliente cliente = mapper.toEntity(dto, persona);
        Cliente guardado = clienteRepository.save(cliente);
        return mapper.toResponseDTO(guardado);
    }

    //Eliminar
    @Override
    public ClienteResponseDTO eliminar(Integer id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        ClienteResponseDTO dto = mapper.toResponseDTO(cliente);

        /*-----ELIMINA CLIENTE DIRECTAMENTE PERO NO SU PERSONA-----
        clienteRepository.delete(cliente);
        return dto;*/

        Integer idPersona = cliente.getPersona().getPersonaId();
        personaRepository.deleteById(idPersona); // BORRAS LA PERSONA (esto elimina cliente automáticamente por CASCADE)
        return dto;
    }

    //Buscar
    @Override
    public ClienteResponseDTO buscarCliente(Integer id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Cliente no encontrado con id: " + id,
                        HttpStatus.NOT_FOUND
                ));
        return mapper.toResponseDTO(cliente);
    }


}

