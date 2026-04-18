package com.sistemabarberia.fadex_backend.modules.seguridad.repository;

import com.sistemabarberia.fadex_backend.modules.seguridad.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<Rol, Integer> {
}