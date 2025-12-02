package com.adoptpets.AdoptPets.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "galeria_fotos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GaleriaFoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_foto")
    private Long idFoto;

    @Column(name = "url_foto", nullable = false)
    private String urlFoto;

    private String descripcion;

    @Column(name = "es_principal")
    @Builder.Default
    private Boolean esPrincipal = false;

    @CreationTimestamp
    @Column(name = "fecha_subida", updatable = false)
    private LocalDateTime fechaSubida;

    @ManyToOne
    @JoinColumn(name = "id_mascota")
    private Mascota mascota;
}