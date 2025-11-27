package com.adoptpets.AdoptPets.controller;

import com.adoptpets.AdoptPets.model.Rol;
import com.adoptpets.AdoptPets.model.Usuario;
import com.adoptpets.AdoptPets.repository.RolRepository;
import com.adoptpets.AdoptPets.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


@Controller
public class UsuarioController {
    @Autowired
    private UsuarioRepository repo;
    @Autowired
    private RolRepository rolRepository;


    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/register")
    public String formularioRegistro(Model model){
        model.addAttribute("usuario", new Usuario());
        return "register";
    }

    @PostMapping("/register")
    public String registrar(@ModelAttribute("usuario") Usuario usuario, Model model) {
        if (repo.existsByEmail(usuario.getEmail())) {
            model.addAttribute("error", "El email ya existe");
            return "register";
        }

        usuario.setPassword(new BCryptPasswordEncoder().encode(usuario.getPassword()));

        Rol rolUsuario = rolRepository.findByNombreRol("ROLE_ADOPTANTE")
                        .orElseThrow(() -> new RuntimeException("Rol por defecto no encontrado"));

        Set<Rol> roles = new HashSet<>();
        roles.add(rolUsuario);
        usuario.setRoles(roles);

        repo.save(usuario);
        return "redirect:/login";
    }

    @GetMapping("/perfil")
    public String perfil(Model model, Authentication auth) {
        String correo = auth.getName();
        Usuario usuario = repo.findByEmail(correo).orElseThrow();
        model.addAttribute("usuario", usuario);
        return "form";
    }
}
