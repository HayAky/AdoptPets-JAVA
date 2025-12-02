package com.adoptpets.AdoptPets.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "comentarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comentario")
    private Long idComentario;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "tipo_contenido", nullable = false)
    private String tipoContenido; // "noticia" o "blog"

    @ManyToOne
    @JoinColumn(name = "id_noticia")
    private Noticia noticia;

    @ManyToOne
    @JoinColumn(name = "id_blog")
    private Blog blog;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String comentario;

    @Builder.Default
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "fecha_comentario", updatable = false)
    private LocalDateTime fechaComentario;
}