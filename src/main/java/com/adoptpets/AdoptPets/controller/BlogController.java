package com.adoptpets.AdoptPets.controller;

import com.adoptpets.AdoptPets.model.enums.CategoriaBlog;
import com.adoptpets.AdoptPets.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/blog")
public class BlogController {

    @Autowired
    private BlogService blogService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("posts", blogService.listarActivos());
        model.addAttribute("categorias", CategoriaBlog.values());
        return "blog/lista";
    }

    @GetMapping("/{id}")
    public String verPost(@PathVariable Long id, Model model) {
        var post = blogService.buscarPorId(id).orElseThrow();
        blogService.incrementarVisitas(id);
        model.addAttribute("post", post);
        return "blog/detalle";
    }


}