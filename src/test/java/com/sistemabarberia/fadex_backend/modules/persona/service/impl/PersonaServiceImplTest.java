package com.sistemabarberia.fadex_backend.modules.persona.service.impl;

import com.sistemabarberia.fadex_backend.modules.persona.dto.request.PersonaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.persona.dto.response.PersonaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.persona.entity.Persona;
import com.sistemabarberia.fadex_backend.modules.persona.mapper.PersonaMapper;
import com.sistemabarberia.fadex_backend.modules.persona.repository.PersonaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // 👈 Activa Mockito
class PersonaServiceImplTest {

    @Mock
    private PersonaRepository personaRepository; // 👈 Simulado, no toca BD real

    @Mock
    private PersonaMapper mapper; // 👈 Simulado

    @InjectMocks
    private PersonaServiceImpl personaService; // 👈 El que realmente probamos

    @Test
    void deberiaCrearPersonaCorrectamente() {
        // GIVEN
        PersonaRequestDTO dto = new PersonaRequestDTO();
        dto.setNombre("Juan");
        dto.setApellido("Pérez");
        dto.setTelefono("987654321");
        dto.setEmail("juan@gmail.com");

        Persona entidad = Persona.builder().nombre("Juan").build();
        Persona guardada = Persona.builder().personaId(1).nombre("Juan").build();
        PersonaResponseDTO responseDTO = PersonaResponseDTO.builder()
                .personaId(1).nombre("Juan").build();

        // Definimos qué retorna cada mock
        when(mapper.toEntity(dto)).thenReturn(entidad);
        when(personaRepository.save(entidad)).thenReturn(guardada);
        when(mapper.toResponseDTO(guardada)).thenReturn(responseDTO);

        // WHEN
        PersonaResponseDTO resultado = personaService.crearPersona(dto);

        // THEN
        assertThat(resultado.getPersonaId()).isEqualTo(1);
        assertThat(resultado.getNombre()).isEqualTo("Juan");
        verify(personaRepository, times(1)).save(entidad); // 👈 Verificamos que se llamó
    }

    @Test
    void deberiaBuscarPersonaPorId() {
        // GIVEN
        Persona persona = Persona.builder().personaId(1).nombre("Juan").build();
        PersonaResponseDTO responseDTO = PersonaResponseDTO.builder()
                .personaId(1).nombre("Juan").build();

        when(personaRepository.findById(1)).thenReturn(Optional.of(persona));
        when(mapper.toResponseDTO(persona)).thenReturn(responseDTO);

        // WHEN
        PersonaResponseDTO resultado = personaService.buscarPersona(1);

        // THEN
        assertThat(resultado.getNombre()).isEqualTo("Juan");
    }

    @Test
    void deberiLanzarExcepcionSiPersonaNoExiste() {
        // GIVEN
        when(personaRepository.findById(99)).thenReturn(Optional.empty());

        // THEN
        assertThatThrownBy(() -> personaService.buscarPersona(99))
                .isInstanceOf(RuntimeException.class); // 👈 Tu BusinessException extiende de esta
    }
}