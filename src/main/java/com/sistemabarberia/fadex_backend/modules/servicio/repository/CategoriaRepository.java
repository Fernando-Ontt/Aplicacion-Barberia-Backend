package com.sistemabarberia.fadex_backend.modules.servicio.repository;

import com.sistemabarberia.fadex_backend.modules.servicio.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    Optional<Categoria> findByNombre(String nombre);

    boolean existsByNombre(String nombre);
}
