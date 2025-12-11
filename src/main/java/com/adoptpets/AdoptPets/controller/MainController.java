package com.adoptpets.AdoptPets.controller;

import com.adoptpets.AdoptPets.service.MascotaService;
import com.adoptpets.AdoptPets.service.RefugioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

    @Autowired
    private MascotaService mascotaService;

    @Autowired
    private RefugioService refugioService;

    @GetMapping("/")
    public String redireccionarRaiz(){
        return "redirect:/main";
    }

    @GetMapping("/main")
    public String welcome(Model model){
        Long mascotasDisponibles = mascotaService.contarDisponibles();
        model.addAttribute("mascotasDisponibles", mascotasDisponibles);
        return "main";
    }

    @GetMapping("/adoptar")
    public String verMascotasPublico(
            Model model,
            @RequestParam(required = false) String especie,
            @RequestParam(required = false) String sexo,
            @RequestParam(required = false) String tamano,
            @RequestParam(required = false) String raza,
            @RequestParam(required = false) Integer edadMin,
            @RequestParam(required = false) Integer edadMax,
            @RequestParam(required = false) Boolean vacunado,
            @RequestParam(required = false) Boolean esterilizado,
            @RequestParam(required = false) Boolean microchip,
            @RequestParam(required = false) Long refugioId,
            @RequestParam(required = false) String busqueda) {

        // Usar filtros avanzados
        var mascotas = mascotaService.buscarConFiltros(
                especie, sexo, tamano, raza, edadMin, edadMax,
                vacunado, esterilizado, microchip, refugioId, busqueda
        );

        // Lista de refugios para el filtro
        var refugios = refugioService.listarActivos();

        model.addAttribute("mascotas", mascotas);
        model.addAttribute("refugios", refugios);
        model.addAttribute("especie", especie);
        model.addAttribute("sexo", sexo);
        model.addAttribute("tamano", tamano);
        model.addAttribute("raza", raza);
        model.addAttribute("edadMin", edadMin);
        model.addAttribute("edadMax", edadMax);
        model.addAttribute("vacunado", vacunado);
        model.addAttribute("esterilizado", esterilizado);
        model.addAttribute("microchip", microchip);
        model.addAttribute("refugioId", refugioId);
        model.addAttribute("busqueda", busqueda);

        return "mascotas";
    }

    @GetMapping("/contacto")
    public String contacto(){
        return "contacto";
    }

    @GetMapping("/error404")
    public String error404(){
        return "error404";
    }

    @GetMapping("/error/403")
    public String error403(){
        return "error403";
    }
}