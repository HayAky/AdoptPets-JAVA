package com.adoptpets.AdoptPets.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Entity
@Table(name = "refugios")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Refugio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_refugio")
    private Long idRefugio;

    @Column(name = "nombre_refugio", nullable = false)
    private String nombreRefugio;

    @Column(nullable = false)
    private String direccion;

    private String telefono;
    private String email;
    private String responsable;

    @Column(name = "capacidad_maxima")
    private Integer capacidadMaxima;

    private String localidad;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Builder.Default
    private Boolean activo= true;

    @CreationTimestamp
    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro;

    @OneToMany(mappedBy = "refugio")
    private List<Mascota> mascotas;
}
