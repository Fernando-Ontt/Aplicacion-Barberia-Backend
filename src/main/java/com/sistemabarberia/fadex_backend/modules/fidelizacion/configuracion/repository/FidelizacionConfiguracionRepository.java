package com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.repository;

import com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.entity.FidelizacionConfiguracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FidelizacionConfiguracionRepository extends JpaRepository<FidelizacionConfiguracion,Integer>, JpaSpecificationExecutor<FidelizacionConfiguracion> {
    Optional<FidelizacionConfiguracion> findByCategoriaCategoriaIdAndActivaTrue(Integer categoriaId);
    Optional<FidelizacionConfiguracion> findByCategoriaCategoriaId(Integer categoriaId);
    boolean existsByCategoriaCategoriaId(Integer categoriaId);

}