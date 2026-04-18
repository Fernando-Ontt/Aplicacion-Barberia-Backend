package com.sistemabarberia.fadex_backend.modules.seguridad.repository;

import com.sistemabarberia.fadex_backend.modules.seguridad.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByUser(String user);
}