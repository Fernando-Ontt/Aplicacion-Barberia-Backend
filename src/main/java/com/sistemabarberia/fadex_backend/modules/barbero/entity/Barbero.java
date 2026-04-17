package com.sistemabarberia.fadex_backend.modules.barbero.entity;

import com.sistemabarberia.fadex_backend.modules.persona.entity.Persona;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name="barbero")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Barbero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_barbero")
    private Integer barberoId;


    @ManyToOne
    @JoinColumn(name = "id_persona")
    private Persona persona;

    @Column(name = "experiencia")
    private Integer experiencia;
    @Column(name = "fecha_ingreso")
    private LocalDate fechaIngreso;
    @Column(name = "estado")
    private String estado;

}
