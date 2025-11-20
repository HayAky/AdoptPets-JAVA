package com.adoptpets.AdoptPets.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "refugios")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Refugio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_refugio")
    private Long idRefugio;

    @Column(name = "nombre_refugio", nullable = false)
    private String nombreRefugio;

    @Column(columnDefinition = "text", nullable = false)
    private String direccion;

    @Column
    private String telefono;

    @Column
    private String email;

    @Column
    private String responsable;

    @Column(name = "capacidad_maxima")
    private Integer capacidadMaxima;

    @Column
    private String localidad;

    @Column(columnDefinition = "text")
    private String descripcion;

    private Boolean activo= true;

    @CreationTimestamp
    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro;
}
