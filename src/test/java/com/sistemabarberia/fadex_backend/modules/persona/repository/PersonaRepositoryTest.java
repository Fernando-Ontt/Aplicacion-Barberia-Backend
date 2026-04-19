package com.sistemabarberia.fadex_backend.modules.persona.repository;

import com.sistemabarberia.fadex_backend.modules.persona.entity.Persona;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest // 👈 Levanta solo la capa JPA con BD H2 en memoria
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
        "spring.flyway.enabled=false"  // 👈 Desactiva Flyway en tests
})
class PersonaRepositoryTest {

    @Autowired
    private PersonaRepository personaRepository;

    private Persona persona;

    @BeforeEach
    void setUp() {
        // Dato de prueba reutilizable
        persona = Persona.builder()
                .nombre("Juan")
                .apellido("Pérez")
                .telefono("987654321")
                .email("juan@gmail.com")
                .build();
    }

    @Test
    void deberiaGuardarPersonaCorrectamente() {
        // WHEN
        Persona guardado = personaRepository.save(persona);

        // THEN
        assertThat(guardado.getPersonaId()).isNotNull();
        assertThat(guardado.getNombre()).isEqualTo("Juan");
        assertThat(guardado.getEmail()).isEqualTo("juan@gmail.com");
    }

    @Test
    void deberiaBuscarPersonaPorId() {
        // GIVEN
        Persona guardado = personaRepository.save(persona);

        // WHEN
        Optional<Persona> resultado = personaRepository.findById(guardado.getPersonaId());

        // THEN
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getNombre()).isEqualTo("Juan");
    }

    @Test
    void deberiaEliminarPersona() {
        // GIVEN
        Persona guardado = personaRepository.save(persona);

        // WHEN
        personaRepository.delete(guardado);
        Optional<Persona> resultado = personaRepository.findById(guardado.getPersonaId());

        // THEN
        assertThat(resultado).isEmpty(); // 👈 Ya no existe
    }

    @Test
    void deberiaListarTodasLasPersonas() {
        // GIVEN
        personaRepository.save(persona);
        personaRepository.save(Persona.builder()
                .nombre("Ana").apellido("Lopez")
                .telefono("912345678").email("ana@gmail.com")
                .build());

        // WHEN
        var lista = personaRepository.findAll();

        // THEN
        assertThat(lista).hasSize(2);
    }
}