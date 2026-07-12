package com.sistemabarberia.fadex_backend.modules.fidelizacion.dsahboard.service;

import com.sistemabarberia.fadex_backend.modules.fidelizacion.dsahboard.dto.response.FidelizacionDashboardAdminResponseDTO;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.dsahboard.dto.response.FidelizacionDashboardClienteResponseDTO;

public interface IFidelizacionDashboardService {
    FidelizacionDashboardClienteResponseDTO obtenerDashboardCliente();
    FidelizacionDashboardAdminResponseDTO obtenerDashboardAdmin();
}