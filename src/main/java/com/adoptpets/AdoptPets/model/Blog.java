package com.adoptpets.AdoptPets.model;

import com.adoptpets.AdoptPets.model.enums.CategoriaBlog;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "blog")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_blog")
    private Long idBlog;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String contenido;

    private String resumen;
    private String autor;

    @Column(name = "fecha_publicacion", nullable = false)
    private LocalDate fechaPublicacion;

    @Builder.Default
    private Boolean activo = true;

    @Enumerated(EnumType.STRING)
    private CategoriaBlog categoria;

    @Column(name = "imagen_url")
    private String imagenUrl;

    @Builder.Default
    private Integer visitas = 0;

    @CreationTimestamp
    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro;

    @OneToMany(mappedBy = "blog", cascade = CascadeType.REMOVE)
    private List<Comentario> comentarios;
}