package com.adoptpets.AdoptPets.model;

import com.adoptpets.AdoptPets.model.enums.EstadoSaludMascota;
import com.adoptpets.AdoptPets.model.enums.TipoSeguimiento;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "seguimientos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seguimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_seguimiento")
    private Long idSeguimiento;

    @ManyToOne
    @JoinColumn(name = "id_adoptante")
    private Usuario adoptante;

    @ManyToOne
    @JoinColumn(name = "id_mascota")
    private Mascota mascota;

    @Column(name = "fecha_seguimiento", nullable = false)
    private LocalDate fechaSeguimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_seguimiento", nullable = false)
    private TipoSeguimiento tipoSeguimiento;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_mascota")
    @Builder.Default
    private EstadoSaludMascota estadoMascota = EstadoSaludMascota.BUENO;

    @Column(name = "proximo_seguimiento")
    private LocalDate proximoSeguimiento;

    @Column(name = "realizado_por")
    private String realizadoPor;

    @CreationTimestamp
    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro;
}