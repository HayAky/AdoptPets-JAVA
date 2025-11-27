package com.adoptpets.AdoptPets.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String redireccionarRaiz(){
        return "redirect:/main";
    }

    @GetMapping("/main")
    public String welcome(){
        return "main";
    }

    @GetMapping("/error404")
        public String error404(){
        return "error404";
    }
}