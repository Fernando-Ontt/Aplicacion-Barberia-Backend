package com.sistemabarberia.fadex_backend.modules.venta.service.impl;

import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import com.sistemabarberia.fadex_backend.commons.exception.ResourceNotFoundException;
import com.sistemabarberia.fadex_backend.modules.venta.dto.request.CheckoutItemRequestDTO;
import com.sistemabarberia.fadex_backend.modules.venta.dto.request.CheckoutRequestDTO;
import com.sistemabarberia.fadex_backend.modules.venta.service.ICheckoutService;
import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import com.sistemabarberia.fadex_backend.modules.cliente.repository.ClienteRepository;
import com.sistemabarberia.fadex_backend.modules.producto.entity.Producto;
import com.sistemabarberia.fadex_backend.modules.producto.repository.ProductoRepository;
import com.sistemabarberia.fadex_backend.modules.venta.dto.request.DetalleVentaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.venta.dto.request.VentaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.venta.dto.response.VentaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.venta.entity.TipoComprobante;
import com.sistemabarberia.fadex_backend.modules.venta.service.IVentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements ICheckoutService {

    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final IVentaService ventaService;

    @Override
    @Transactional
    public VentaResponseDTO procesar(Integer usuarioId, CheckoutRequestDTO request) {

        Cliente cliente = clienteRepository.findByPersona_Usuario_IdUsuario(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        // Une productos repetidos en una sola línea
        Map<Integer, Integer> cantidades = new LinkedHashMap<>();
        for (CheckoutItemRequestDTO item : request.getItems()) {
            cantidades.merge(item.getProductoId(), item.getCantidad(), Integer::sum);
        }

        List<DetalleVentaRequestDTO> detalles = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entrada : cantidades.entrySet()) {
            Producto producto = productoRepository.findById(Long.valueOf(entrada.getKey()))
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + entrada.getKey()));

            if (!producto.isEstado() || !producto.isPublicado()) {
                throw new BusinessException("El producto no está disponible: " + producto.getNombre(), HttpStatus.BAD_REQUEST);
            }
            if (producto.getPrecio() == null || producto.getPrecio().signum() <= 0) {
                throw new BusinessException("El producto no tiene precio válido: " + producto.getNombre(), HttpStatus.BAD_REQUEST);
            }

            detalles.add(DetalleVentaRequestDTO.builder()
                    .productoId(entrada.getKey())
                    .cantidad(entrada.getValue())
                    .precioUnitario(producto.getPrecio()) // precio SIEMPRE de la BD
                    .build());
        }

        VentaRequestDTO venta = VentaRequestDTO.builder()
                .clienteId(cliente.getClienteId())
                .tipoComprobante(request.getTipoComprobante() != null
                        ? request.getTipoComprobante()
                        : TipoComprobante.BOLETA)
                .metodoPago(request.getMetodoPago())
                .detalles(detalles)
                .build();

        return ventaService.crear(venta);
    }
}