package com.sistemabarberia.fadex_backend.modules.venta.service;

import com.sistemabarberia.fadex_backend.modules.venta.dto.request.CheckoutRequestDTO;
import com.sistemabarberia.fadex_backend.modules.venta.dto.response.VentaResponseDTO;

public interface ICheckoutService {
    VentaResponseDTO procesar(Integer usuarioId, CheckoutRequestDTO request);
}
