package com.sistemabarberia.fadex_backend.auth.usuario.Repository;

import com.sistemabarberia.fadex_backend.auth.usuario.Entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByuser(String username);
}
