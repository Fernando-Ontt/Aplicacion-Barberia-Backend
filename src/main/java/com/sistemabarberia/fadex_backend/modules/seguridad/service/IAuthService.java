package com.sistemabarberia.fadex_backend.modules.seguridad.service;

import com.sistemabarberia.fadex_backend.modules.seguridad.entity.Usuario;

public interface IAuthService {

    String authenticate(String username, String password);

    String login(String username, String password);

    Usuario getCurrentUser();
}
