package com.adoptpets.AdoptPets.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "mascotas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mascota")
    private Long idMascota;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String especie;

    private String raza;

    @Column(name = "edad_aproximada")
    private Integer edadAproximada;

    @Column(nullable = false)
    private String sexo;

    @Column(nullable = false)
    private String tamano;

    private BigDecimal peso;
    private String color;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "estado_salud", columnDefinition = "TEXT")
    private String estadoSalud;

    @Builder.Default
    private Boolean vacunado = false;

    @Builder.Default
    private Boolean esterilizado = false;

    @Builder.Default
    private Boolean microchip = false;

    @Column(name = "estado_adopcion")
    private String estadoAdopcion; // Se puede mapear como Enum si se prefiere strictness

    @Column(name = "fecha_ingreso")
    private LocalDate fechaIngreso;

    @CreationTimestamp
    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro;

    @ManyToOne
    @JoinColumn(name = "id_refugio")
    private Refugio refugio;

    @OneToMany(mappedBy = "mascota", cascade = CascadeType.ALL)
    private List<GaleriaFoto> fotos;
}