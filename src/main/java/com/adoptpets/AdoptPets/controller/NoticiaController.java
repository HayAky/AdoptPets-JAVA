package com.adoptpets.AdoptPets.controller;

import com.adoptpets.AdoptPets.model.enums.CategoriaNoticia;
import com.adoptpets.AdoptPets.service.NoticiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/noticias")
public class NoticiaController {

    @Autowired
    private NoticiaService noticiaService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("noticias", noticiaService.listarActivas());
        model.addAttribute("categorias", CategoriaNoticia.values());
        return "noticias/lista";
    }

    @GetMapping("/{id}")
    public String verNoticia(@PathVariable Long id, Model model) {
        model.addAttribute("noticia", noticiaService.buscarPorId(id).orElseThrow());
        return "noticias/detalle";
    }

    @GetMapping("/categoria/{categoria}")
    public String porCategoria(@PathVariable CategoriaNoticia categoria, Model model) {
        model.addAttribute("noticias", noticiaService.listarPorCategoria(categoria));
        model.addAttribute("categoriaActual", categoria);
        return "noticias/lista";
    }
}