package com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.entity;

import com.sistemabarberia.fadex_backend.commons.shared.AuditableEntity;
import com.sistemabarberia.fadex_backend.modules.categoria.entity.Categoria;
import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table( name = "fidelizacion_tarjeta",  uniqueConstraints = { @UniqueConstraint( name = "uq_tarjeta", columnNames = {"id_cliente", "id_categoria"} ) } )
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FidelizacionTarjeta extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tarjeta")
    private Integer tarjetaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @Builder.Default
    @Column(nullable = false)
    private Integer progreso = 0;

    @Builder.Default
    @Column(name = "giros_disponibles", nullable = false)
    private Integer girosDisponibles = 0;

    @Builder.Default
    @Column(name = "total_giros", nullable = false)
    private Integer totalGiros = 0;

    @Builder.Default
    @Column(name = "ciclo_activo", nullable = false)
    private Boolean cicloActivo = true;
}