package com.sistemabarberia.fadex_backend.modules.venta.dto.response;

import com.sistemabarberia.fadex_backend.modules.pagos.entity.MetodoPago;
import com.sistemabarberia.fadex_backend.modules.venta.entity.TipoComprobante;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VentaResponseDTO {

    private Integer ventaId;
    private String numeroCorrelativo;
    private Integer clienteId;
    private String clienteNombre;
    private Integer barberoId;
    private String barberoNombre;
    private LocalDateTime fecha;
    private TipoComprobante tipoComprobante;
    private MetodoPago metodoPago;
    private List<DetalleVentaResponseDTO> detalles;
    private String serieComprobante;
    private String numeroComprobante;
    private BigDecimal subtotal;
    private BigDecimal igv;
    private BigDecimal total;
    private LocalDateTime fechaEmision;
}