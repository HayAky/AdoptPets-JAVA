package com.adoptpets.AdoptPets.repository;

import com.adoptpets.AdoptPets.model.Blog;
import com.adoptpets.AdoptPets.model.enums.CategoriaBlog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    List<Blog> findByActivoTrue();
    List<Blog> findByCategoria(CategoriaBlog categoria);
    List<Blog> findByActivoTrueOrderByFechaPublicacionDesc();
}