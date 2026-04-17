package com.sistemabarberia.fadex_backend.modules.barbero.controller;


import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.modules.barbero.dto.response.BarberoResponseDTO;
import com.sistemabarberia.fadex_backend.modules.barbero.service.IBarberoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/barberos")
public class BarberoController {

    @Autowired
    private IBarberoService barberoService;

    /*CRUD BASICO*/
    @GetMapping
    public ResponseEntity<ApiResponse<BarberoResponseDTO>> listarBarberos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BarberoResponseDTO> result = barberoService.listarBarberos(pageable);
        return ResponseEntity.ok(ApiResponse.ok("Barberos obtenidos correctamente", result));
    }

    /*AVANZADOS*/
}
