package com.adoptpets.AdoptPets.model;

import com.adoptpets.AdoptPets.model.enums.Especie;
import com.adoptpets.AdoptPets.model.enums.EstadoAdopcion;
import com.adoptpets.AdoptPets.model.enums.Sexo;
import com.adoptpets.AdoptPets.model.enums.Tamano;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "mascotas")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Mascota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mascota")
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Especie especie;

    private String raza;

    @Column(name ="edad_aproximada")
    private Integer edadAproximada;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sexo sexo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "tamaño")
    private Tamano tamano;

    private BigDecimal peso;
    private String color;

    @Column(columnDefinition = "text")
    private String descripcion;

    @Column(columnDefinition = "text")
    private String estadp_salud;

    private boolean vacunado;
    private boolean esterilizado;
    private boolean microchip;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_adopcion")
    private EstadoAdopcion estadoAdopcion;

    @Column(name = "fecha_ingreso")
    private LocalDateTime fechaIngreso;

    @ManyToOne
    @JoinColumn(name = "id_refugio")
    private Refugio refugio;

    @CreationTimestamp
    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro;

}
