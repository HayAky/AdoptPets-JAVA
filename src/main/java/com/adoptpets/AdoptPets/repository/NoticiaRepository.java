package com.adoptpets.AdoptPets.repository;

import com.adoptpets.AdoptPets.model.Noticia;
import com.adoptpets.AdoptPets.model.enums.CategoriaNoticia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NoticiaRepository extends JpaRepository<Noticia, Long> {
    List<Noticia> findByActivaTrue();
    List<Noticia> findByCategoria(CategoriaNoticia categoria);
    List<Noticia> findByActivaTrueOrderByFechaPublicacionDesc();
}