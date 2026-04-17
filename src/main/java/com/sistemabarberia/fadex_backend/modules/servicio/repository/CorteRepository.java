package com.sistemabarberia.fadex_backend.modules.servicio.repository;

import com.sistemabarberia.fadex_backend.modules.servicio.entity.Corte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CorteRepository extends JpaRepository<Corte, Integer> {

    Optional<Corte> findByNombre(String nombre);

    boolean existsByNombre(String nombre);

    List<Corte> findByCategoria_CategoriaId(Integer categoriaId);
}
