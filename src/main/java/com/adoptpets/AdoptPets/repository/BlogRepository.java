package com.adoptpets.AdoptPets.repository;

import com.adoptpets.AdoptPets.model.Blog;
import com.adoptpets.AdoptPets.model.enums.CategoriaBlog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {

    // Blogs activos
    List<Blog> findByActivoTrueOrderByFechaPublicacionDesc();

    // Por categoría
    List<Blog> findByCategoriaAndActivoTrue(CategoriaBlog categoria);

    // Más visitados
    @Query("SELECT b FROM Blog b WHERE b.activo = true ORDER BY b.visitas DESC")
    List<Blog> findMasVisitados();

    // Buscar por título
    List<Blog> findByTituloContainingIgnoreCaseAndActivoTrue(String titulo);
}