package com.sistemabarberia.fadex_backend.modules.venta.entity;

import com.sistemabarberia.fadex_backend.modules.barbero.entity.Barbero;
import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(
        name = "venta",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_venta_serie_numero",
                columnNames = {"serie_comprobante", "numero_comprobante"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private Integer ventaId;

    @Column(name = "numero_correlativo", unique = true, length = 20)
    private String numeroCorrelativo;

    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_barbero")
    private Barbero barbero;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_comprobante")
    private TipoComprobante tipoComprobante;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detalles;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistorialVenta> historial;

    @Column(name = "serie_comprobante", nullable = false, length = 10)
    private String serieComprobante;

    @Column(name = "numero_comprobante", nullable = false, length = 20)
    private String numeroComprobante;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "igv", nullable = false, precision = 10, scale = 2)
    private BigDecimal igv;

    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDateTime fechaEmision;

    @PrePersist
    void prePersist() {
        LocalDateTime ahora = LocalDateTime.now();
        if (fecha == null) fecha = ahora;
        if (fechaEmision == null) fechaEmision = fecha;
        if (subtotal == null) subtotal = BigDecimal.ZERO;
        if (igv == null) igv = BigDecimal.ZERO;
        if (total == null) total = BigDecimal.ZERO;
    }
}