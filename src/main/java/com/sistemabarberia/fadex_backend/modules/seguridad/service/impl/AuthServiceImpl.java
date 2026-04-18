package com.sistemabarberia.fadex_backend.modules.seguridad.service.impl;

import com.sistemabarberia.fadex_backend.modules.seguridad.entity.Usuario;
import com.sistemabarberia.fadex_backend.modules.seguridad.repository.UsuarioRepository;
import com.sistemabarberia.fadex_backend.modules.seguridad.security.CustomUserDetails;
import com.sistemabarberia.fadex_backend.modules.seguridad.security.JwtUtil;
import com.sistemabarberia.fadex_backend.modules.seguridad.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public String authenticate(String username, String password) {
        Usuario usuario = usuarioRepository.findByUser(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (passwordEncoder.matches(password, usuario.getPassword())) {
            CustomUserDetails userDetails = new CustomUserDetails(usuario);
            return jwtUtil.generateToken(userDetails);
        } else {
            throw new RuntimeException("Contraseña incorrecta");
        }
    }

    @Override
    public String login(String username, String password) {
        return authenticate(username, password);
    }

    @Override
    public Usuario getCurrentUser() {
        // Implementar lógica para obtener usuario actual desde SecurityContext
        return null;
    }
}
