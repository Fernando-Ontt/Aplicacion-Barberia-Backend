package com.sistemabarberia.fadex_backend.modules.cliente.controller;

import com.sistemabarberia.fadex_backend.modules.cliente.service.IClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class ClienteController {

    @Autowired
    private IClienteService clienteService;

    /*CRUD BASICO*/


    /*AVANZADOS*/

}
