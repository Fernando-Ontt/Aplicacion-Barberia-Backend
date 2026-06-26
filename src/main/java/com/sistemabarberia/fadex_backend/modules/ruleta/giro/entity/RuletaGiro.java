package com.sistemabarberia.fadex_backend.modules.ruleta.giro.entity;

import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import com.sistemabarberia.fadex_backend.modules.fidelizacion.tarjeta.entity.FidelizacionTarjeta;
import com.sistemabarberia.fadex_backend.modules.ruleta.item.entity.RuletaItem;
import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.entity.Ruleta;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ruleta_giro")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuletaGiro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_giro")
    private Integer giroId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tarjeta", nullable = false)
    private FidelizacionTarjeta tarjeta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ruleta", nullable = false)
    private Ruleta ruleta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_item", nullable = false)
    private RuletaItem item;

    @Column(name = "numero_giro", nullable = false)
    private Integer numeroGiro;

    @Column(name = "prob_final", precision = 6, scale = 3)
    private BigDecimal probFinal;

    @Column(name = "prob_aplicada", precision = 6, scale = 3)
    private BigDecimal probAplicada;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @PrePersist
    public void prePersist() {
        this.fecha = LocalDateTime.now();
    }
}