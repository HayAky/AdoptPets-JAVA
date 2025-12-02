package com.adoptpets.AdoptPets.model;

import com.adoptpets.AdoptPets.model.enums.CategoriaNoticia;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "noticias")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Noticia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_noticia")
    private Long idNoticia;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String contenido;

    private String resumen;
    private String autor;

    @Column(name = "fecha_publicacion", nullable = false)
    private LocalDate fechaPublicacion;

    @Builder.Default
    private Boolean activa = true;

    @Enumerated(EnumType.STRING)
    private CategoriaNoticia categoria;

    @Column(name = "imagen_url")
    private String imagenUrl;

    @CreationTimestamp
    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro;

    @OneToMany(mappedBy = "noticia", cascade = CascadeType.REMOVE)
    private List<Comentario> comentarios;
}