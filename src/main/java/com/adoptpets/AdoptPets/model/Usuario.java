package com.adoptpets.AdoptPets.model;

import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "roles")  // ← Excluir la colección
@ToString(exclude = "roles")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(nullable = false, length = 100) // SQL: VARCHAR(100)
    private String nombre;

    @Column(nullable = false, length = 100) // SQL: VARCHAR(100)
    private String apellido;

    @Column(nullable = false, unique = true, length = 150) // SQL: VARCHAR(150)
    private String email;

    @Column(length = 20)
    private String telefono;

    @Column(length = 255) // SQL: VARCHAR(255) - Antes era TEXT
    private String direccion;

    // SQL tiene DEFAULT 'bogota', lo inicializamos aquí para consistencia
    @Column(length = 50)
    private String ciudad = "bogota";

    @Column(length = 20, unique = true)
    private String cedula;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(nullable = false, length = 100) // SQL: VARCHAR(100)
    private String password;

    // SQL: DEFAULT TRUE
    @Column(nullable = false)
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuario_roles",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_rol")
    )
    private Set<Rol> roles = new HashSet<>();
}