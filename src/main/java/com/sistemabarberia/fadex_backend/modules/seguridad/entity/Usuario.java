package com.sistemabarberia.fadex_backend.modules.seguridad.entity;
import com.sistemabarberia.fadex_backend.modules.persona.entity.Persona;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuario")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "qr_token")
    private String qrToken;

    @Column(name = "usuario")
    private String user;

    @Column(name = "password")
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol")
    private Rol rol;

}
