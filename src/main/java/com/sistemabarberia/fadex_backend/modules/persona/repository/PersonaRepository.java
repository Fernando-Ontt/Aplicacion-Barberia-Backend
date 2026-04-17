package com.sistemabarberia.fadex_backend.modules.persona.repository;

import com.sistemabarberia.fadex_backend.modules.persona.entity.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonaRepository extends JpaRepository<Persona,Integer > {
}
