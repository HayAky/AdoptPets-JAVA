package com.adoptpets.AdoptPets.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ContactoController {

    @GetMapping("/contactenos")
    public String mostrarFormularioContacto() {
        return "contactenos";
    }
}