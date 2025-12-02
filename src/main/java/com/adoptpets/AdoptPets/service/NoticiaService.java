package com.adoptpets.AdoptPets.service;

import com.adoptpets.AdoptPets.model.Noticia;
import com.adoptpets.AdoptPets.model.enums.CategoriaNoticia;
import com.adoptpets.AdoptPets.repository.NoticiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NoticiaService {

    @Autowired
    private NoticiaRepository noticiaRepository;

    public List<Noticia> listarActivas() {
        return noticiaRepository.findByActivaTrueOrderByFechaPublicacionDesc();
    }

    public List<Noticia> listarPorCategoria(CategoriaNoticia categoria) {
        return noticiaRepository.findByCategoriaAndActivaTrue(categoria);
    }

    public Optional<Noticia> buscarPorId(Long id) {
        return noticiaRepository.findById(id);
    }

    public Noticia guardar(Noticia noticia) {
        return noticiaRepository.save(noticia);
    }

    public void eliminar(Long id) {
        noticiaRepository.deleteById(id);
    }

    public List<Noticia> buscarPorTitulo(String titulo) {
        return noticiaRepository.findByTituloContainingIgnoreCaseAndActivaTrue(titulo);
    }
}