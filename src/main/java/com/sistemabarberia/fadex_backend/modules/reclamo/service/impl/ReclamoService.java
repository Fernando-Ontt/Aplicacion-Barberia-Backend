package com.sistemabarberia.fadex_backend.modules.reclamo.service.impl;

import com.sistemabarberia.fadex_backend.auth.usuario.Entity.Usuario;
import com.sistemabarberia.fadex_backend.auth.usuario.Repository.UsuarioRepository;
import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import com.sistemabarberia.fadex_backend.commons.exception.ResourceNotFoundException;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.commons.storage.FileStorageService;
import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import com.sistemabarberia.fadex_backend.modules.cliente.repository.ClienteRepository;
import com.sistemabarberia.fadex_backend.modules.reclamo.dto.ReclamoFiltro;
import com.sistemabarberia.fadex_backend.modules.reclamo.dto.ReclamoResumen;
import com.sistemabarberia.fadex_backend.modules.reclamo.dto.request.ReclamoPublicoRequest;
import com.sistemabarberia.fadex_backend.modules.reclamo.dto.request.ReclamoRequest;
import com.sistemabarberia.fadex_backend.modules.reclamo.dto.request.ReclamoSolucionRequest;
import com.sistemabarberia.fadex_backend.modules.reclamo.dto.response.ReclamoAdjuntoResponse;
import com.sistemabarberia.fadex_backend.modules.reclamo.dto.response.ReclamoResponse;
import com.sistemabarberia.fadex_backend.modules.reclamo.entity.Reclamo;
import com.sistemabarberia.fadex_backend.modules.reclamo.entity.ReclamoAdjunto;
import com.sistemabarberia.fadex_backend.modules.reclamo.entity.enums.EstadoReclamo;
import com.sistemabarberia.fadex_backend.modules.reclamo.repository.ReclamoAdjuntoRepository;
import com.sistemabarberia.fadex_backend.modules.reclamo.repository.ReclamoRepository;
import com.sistemabarberia.fadex_backend.modules.reclamo.service.IReclamoEmailService;
import com.sistemabarberia.fadex_backend.modules.reclamo.service.IReclamoService;
import com.sistemabarberia.fadex_backend.modules.reclamo.specs.ReclamoSpecification;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.Reserva;
import com.sistemabarberia.fadex_backend.modules.reserva.repository.ReservaRepository;
import com.sistemabarberia.fadex_backend.modules.venta.entity.Venta;
import com.sistemabarberia.fadex_backend.modules.venta.repository.VentaRepository;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReclamoService implements IReclamoService {
    private final ReclamoRepository reclamoRepository;
    private final ReclamoAdjuntoRepository reclamoAdjuntoRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReservaRepository reservaRepository;
    private final VentaRepository ventaRepository;
    private final IReclamoEmailService emailService;
    private final FileStorageService fileStorageService;


}
