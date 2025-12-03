package com.adoptpets.AdoptPets.model;

import com.adoptpets.AdoptPets.model.enums.EstadoAdopcion;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "adopciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Adopcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_adopcion")
    private Long idAdopcion;

    @ManyToOne
    @JoinColumn(name = "id_adoptante")
    private Usuario adoptante;

    @ManyToOne
    @JoinColumn(name = "id_mascota")
    private Mascota mascota;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDate fechaSolicitud;

    @Column(name = "fecha_aprobacion")
    private LocalDate fechaAprobacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_adopcion")
    @Builder.Default
    private EstadoAdopcion estadoAdopcion = EstadoAdopcion.pendiente;

    @Column(name = "url_formulario_descarga")
    private String urlFormularioDescarga;

    @Column(name = "formulario_enviado")
    @Builder.Default
    private Boolean formularioEnviado = false;

    @Column(name = "fecha_envio_formulario")
    private LocalDateTime fechaEnvioFormulario;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @CreationTimestamp
    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro;
}