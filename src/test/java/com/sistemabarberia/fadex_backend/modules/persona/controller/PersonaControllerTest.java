package com.sistemabarberia.fadex_backend.modules.persona.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistemabarberia.fadex_backend.modules.persona.dto.request.PersonaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.persona.dto.response.PersonaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.persona.service.IPersonaService;
import com.sistemabarberia.fadex_backend.modules.seguridad.security.JwtFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = PersonaController.class,      // Carga solo este controlador
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,// Excluir por clase específica
                classes = JwtFilter.class         // Esta clase en particular
            )
)
//Configura el MockMvc que es el objeto que simula peticiones HTTP sin levantar un servidor
@AutoConfigureMockMvc(addFilters = false)  //cada peticion pasa por Jwt,autenticacion,etc si es true , pero false lo omite

//Anotacion permite definir propedades de configuracion en tests
@TestPropertySource(properties = {
        "spring.flyway.enabled=false" //Inhabilita el flyway para que pueda correr
        // sin la bd del servidor creando sus propios datos en memoria usando H2 (BD simulada en Ram)
})

class PersonaControllerTest {

    @Autowired
    private MockMvc mockMvc; // Simula peticiones HTTP sin levantar servidor real

    @MockBean //Crea objeto falso Y lo registra en Spring
    private IPersonaService personaService; // Mock del servicio

    @Autowired
    private ObjectMapper objectMapper; // Para convertir objetos a JSON

    @Test
    void deberiaListarPersonas() throws Exception {
        // GIVEN
        PersonaResponseDTO dto = PersonaResponseDTO.builder() //se crea una solicitud dto builder pide simular los datos siguientes
                .personaId(1).usuarioId(1).nombre("Juan").apellido("Pérez")
                .telefono("987654321").email("juan@gmail.com") //datos simulados que estaran en memoria
                .build();

        when(personaService.listarPersonas(any())) //cuando el servicio pida llamar a todos con cualquier parametro
                .thenReturn(new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1)); // retornara la paginacion con ellos

        // WHEN + THEN
        mockMvc.perform(get("/personas"))
                .andExpect(status().isOk()) //verifica la respuesta
                .andExpect(jsonPath("$.data.content[0].nombre").value("Juan")); //Verifica el JSON de respuesta
    }

    @Test
    void deberiaBuscarPersonaPorId() throws Exception {
        // GIVEN
        PersonaResponseDTO dto = PersonaResponseDTO.builder()
                .personaId(1).nombre("Juan").build();

        when(personaService.buscarPersona(1)).thenReturn(dto);

        // WHEN + THEN
        mockMvc.perform(get("/personas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nombre").value("Juan"));
    }

    @Test
    void deberiaCrearPersona() throws Exception {
        // GIVEN
        PersonaRequestDTO request = new PersonaRequestDTO();
        request.setUsuarioId(1);
        request.setNombre("Juan");
        request.setApellido("Pérez");
        request.setTelefono("987654321");
        request.setEmail("juan@gmail.com");

        PersonaResponseDTO response = PersonaResponseDTO.builder()
                .personaId(1).nombre("Juan").build();

        when(personaService.crearPersona(any())).thenReturn(response);

        // WHEN + THEN
        mockMvc.perform(post("/personas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.personaId").value(1));
    }

    @Test
    void deberiaEliminarPersona() throws Exception {
        // GIVEN
        PersonaResponseDTO response = PersonaResponseDTO.builder()
                .personaId(1).nombre("Juan").build();

        when(personaService.eliminar(1)).thenReturn(response);

        // WHEN + THEN
        mockMvc.perform(delete("/personas/eliminar/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.personaId").value(1));
    }
}