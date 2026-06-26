package com.sistemabarberia.fadex_backend.modules.fidelizacion.configuracion.entity;
import com.sistemabarberia.fadex_backend.modules.categoria.entity.Categoria;
//import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.entity.Ruleta;
import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.entity.Ruleta;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table( name = "fidelizacion_configuracion", uniqueConstraints = { @UniqueConstraint( name = "uq_fidelizacion_configuracion_categoria",  columnNames = "id_categoria") })

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FidelizacionConfiguracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_configuracion")
    private Integer configuracionId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activa = true;

    @Column(nullable = false)
    private Integer meta;

    @Column(name = "mostrar_siempre", nullable = false)
    @Builder.Default
    private Boolean mostrarSiempre = false;

    @Column(name = "crear_tarjeta_automatica", nullable = false)
    @Builder.Default
    private Boolean crearTarjetaAutomatica = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ruleta")
    private Ruleta ruleta;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}