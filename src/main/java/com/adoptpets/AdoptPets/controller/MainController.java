package com.adoptpets.AdoptPets.controller;

import com.adoptpets.AdoptPets.service.MascotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

    @Autowired
    private MascotaService mascotaService;

    @GetMapping("/")
    public String redireccionarRaiz(){
        return "redirect:/main";
    }

    @GetMapping("/main")
    public String welcome(Model model){
        // Estadísticas para la página principal
        Long mascotasDisponibles = mascotaService.contarDisponibles();
        model.addAttribute("mascotasDisponibles", mascotasDisponibles);
        return "main";
    }

    @GetMapping("/adoptar")
    public String verMascotasPublico(Model model,
                                     @RequestParam(required = false) String especie,
                                     @RequestParam(required = false) String busqueda) {
        if (busqueda != null && !busqueda.isEmpty()) {
            model.addAttribute("mascotas", mascotaService.buscar(busqueda));
        } else if (especie != null && !especie.isEmpty()) {
            model.addAttribute("mascotas", mascotaService.buscarPorEspecie(especie));
        } else {
            model.addAttribute("mascotas", mascotaService.listarDisponibles());
        }

        model.addAttribute("especie", especie);
        model.addAttribute("busqueda", busqueda);

        return "public/mascotas";
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