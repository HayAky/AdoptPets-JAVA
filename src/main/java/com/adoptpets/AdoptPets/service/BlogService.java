package com.adoptpets.AdoptPets.service;

import com.adoptpets.AdoptPets.model.Blog;
import com.adoptpets.AdoptPets.model.enums.CategoriaBlog;
import com.adoptpets.AdoptPets.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BlogService {

    @Autowired
    private BlogRepository blogRepository;

    public List<Blog> listarTodos() {
        return blogRepository.findAll();
    }

    public List<Blog> listarActivos() {
        return blogRepository.findByActivoTrue();
    }

    public List<Blog> listarActivosOrdenados() {
        return blogRepository.findByActivoTrueOrderByFechaPublicacionDesc();
    }

    public List<Blog> listarPorCategoria(CategoriaBlog categoria) {
        return blogRepository.findByCategoria(categoria);
    }

    public Optional<Blog> buscarPorId(Long id) {
        return blogRepository.findById(id);
    }

    public Blog guardar(Blog blog) {
        return blogRepository.save(blog);
    }

    public void eliminar(Long id) {
        blogRepository.deleteById(id);
    }

    public void incrementarVisitas(Long id) {
        blogRepository.findById(id).ifPresent(blog -> {
            blog.setVisitas(blog.getVisitas() + 1);
            blogRepository.save(blog);
        });
    }
}