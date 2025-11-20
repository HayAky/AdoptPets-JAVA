package com.adoptpets.AdoptPets.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.adoptpets.AdoptPets.model.Rol;
import org.springframework.web.bind.annotation.GetMapping;

@Entity
@Table(name = "usuarios")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(length = 20)
    private String telefono;

    @Column(columnDefinition = "text")
    private String direccion;

    @Column(length = 50)
    private String ciudad;

    @Column(length = 20, unique = true)
    private String cedula;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @ManyToOne
    @JoinColumn(name = "id_rol")
    private Rol rol;

    private Boolean activo;

    @CreationTimestamp
    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(nullable = false)
    private String password;



}
