package com.adoptpets.AdoptPets.controller;

import com.adoptpets.AdoptPets.model.Usuario;
import com.adoptpets.AdoptPets.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UsuarioController {
    @Autowired
    private UsuarioRepository repo;

    @GetMapping("/")
    public String redireccionarRaiz(){
        return "redirect:/welcome";
    }

    @GetMapping("/welcome")
    public String welcome(){
    return "welcome";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("registro")
    public String formularioRegistro(Model model){
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(@ModelAttribute Usuario usuario){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        usuario.setPassword(new BCryptPasswordEncoder().encode(usuario.getPassword()));
        repo.save(usuario);
        return "redirect:/login";
    }

    @GetMapping("/home")
    public String home(Model model, Authentication auth){
        model.addAttribute("rol", auth.getAuthorities().toString());
        return "home";
    }

    @GetMapping("/perfil")
    public String perfil(Model model, Authentication auth) {
        String correo = auth.getName();
        Usuario usuario = repo.findByEmail(correo).orElseThrow();
        model.addAttribute("usuario", usuario);
        return "form";
    }


}
