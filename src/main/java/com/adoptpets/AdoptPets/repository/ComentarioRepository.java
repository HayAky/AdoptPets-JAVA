package com.adoptpets.AdoptPets.repository;

import com.adoptpets.AdoptPets.model.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    List<Comentario> findByNoticiaIdNoticiaAndActivoTrue(Long idNoticia);
    List<Comentario> findByBlogIdBlogAndActivoTrue(Long idBlog);
}