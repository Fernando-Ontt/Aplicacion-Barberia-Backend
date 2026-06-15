package com.sistemabarberia.fadex_backend.modules.reclamo.repository;

import com.sistemabarberia.fadex_backend.modules.reclamo.entity.Reclamo;
import com.sistemabarberia.fadex_backend.modules.reclamo.entity.enums.EstadoReclamo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface ReclamoRepository extends JpaRepository<Reclamo, Long>, JpaSpecificationExecutor<Reclamo> {
    long countByEstadoReclamo(EstadoReclamo estadoReclamo);
    @Query("""
    SELECT COUNT(r)
    FROM Reclamo r
    WHERE CAST(r.fechaReclamo AS date) = :fecha
""")
    long contarReclamosPorFecha(@Param("fecha") LocalDate fecha);

    @EntityGraph(attributePaths = "adjuntos")
    Optional<Reclamo> findByIdReclamo(Long id);
}