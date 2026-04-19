package com.sistemabarberia.fadex_backend.modules.seguridad.service.impl;

import com.sistemabarberia.fadex_backend.modules.persona.entity.Persona;
import com.sistemabarberia.fadex_backend.modules.persona.repository.PersonaRepository;
import com.sistemabarberia.fadex_backend.modules.seguridad.dto.request.RegisterRequest;
import com.sistemabarberia.fadex_backend.modules.seguridad.dto.response.UsuarioResponse;
import com.sistemabarberia.fadex_backend.modules.seguridad.entity.Rol;
import com.sistemabarberia.fadex_backend.modules.seguridad.entity.Usuario;
import com.sistemabarberia.fadex_backend.modules.seguridad.repository.RolRepository;
import com.sistemabarberia.fadex_backend.modules.seguridad.repository.UsuarioRepository;
import com.sistemabarberia.fadex_backend.modules.seguridad.service.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements IUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final PersonaRepository personaRepository;

    @Override
    public Page<UsuarioResponse> getAll(Pageable pageable) {
        return usuarioRepository.findAll(pageable)
                .map(this::toResponse);
    }

    @Override
    public UsuarioResponse getById(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return toResponse(usuario);
    }

    @Override
    public UsuarioResponse update(Integer id, RegisterRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Rol rol = rolRepository.findById(request.getIdRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        usuario.setUser(request.getUsername());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(rol);

        usuarioRepository.save(usuario);
        return toResponse(usuario);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuarioRepository.delete(usuario);
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .idUsuario(usuario.getIdUsuario())
                .username(usuario.getUser())
                .rol(usuario.getRol().getNombre())
                .persona(usuario.getPersona())
                .build();
    }
    @Override
    public UsuarioResponse create(RegisterRequest request) {
        if (usuarioRepository.findByUser(request.getUsername()).isPresent()) {
            throw new RuntimeException("El usuario ya existe");
        }

        Rol rol = rolRepository.findById(request.getIdRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        Persona persona = personaRepository.findById(request.getIdPersona())
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));

        Usuario usuario = Usuario.builder()
                .user(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(rol)
                .persona(persona)
                .build();

        return toResponse(usuarioRepository.save(usuario));
    }
}
