package com.sistemabarberia.fadex_backend.modules.seguridad.repository;
import com.sistemabarberia.fadex_backend.modules.seguridad.entity.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermisoRepository extends JpaRepository<Permiso, Integer> {
}
