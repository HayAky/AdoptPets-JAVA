package com.adoptpets.AdoptPets.controller;

import com.adoptpets.AdoptPets.model.Rol;
import com.adoptpets.AdoptPets.model.Usuario;
import com.adoptpets.AdoptPets.service.UsuarioService;
import com.adoptpets.AdoptPets.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.Set;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
                        @RequestParam(required = false) String logout,
                        Model model){
        if (error != null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
        }
        if (logout != null) {
            model.addAttribute("mensaje", "Has cerrado sesión exitosamente");
        }
        return "login";
    }

    @GetMapping("/register")
    public String formularioRegistro(@RequestParam(required = false) String tipo, Model model){
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("tipo", tipo != null ? tipo : "adoptante");
        return "register";
    }

    @PostMapping("/register")
    public String registrar(@ModelAttribute("usuario") Usuario usuario,
                            @RequestParam String confirmPassword,
                            @RequestParam(required = false) String tipoRegistro,
                            Model model,
                            RedirectAttributes flash) {
        try {
            // Validar email
            if (usuarioService.existeEmail(usuario.getEmail())) {
                model.addAttribute("error", "El email ya está registrado");
                model.addAttribute("tipo", tipoRegistro);
                return "register";
            }

            // Validar contraseñas
            if (!usuario.getPassword().equals(confirmPassword)) {
                model.addAttribute("error", "Las contraseñas no coinciden");
                model.addAttribute("tipo", tipoRegistro);
                return "register";
            }

            // Validar longitud de contraseña
            if (usuario.getPassword().length() < 6) {
                model.addAttribute("error", "La contraseña debe tener al menos 6 caracteres");
                model.addAttribute("tipo", tipoRegistro);
                return "register";
            }

            // Encriptar contraseña
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

            // Asignar rol según tipo de registro
            Rol rol;
            if ("refugio".equals(tipoRegistro)) {
                rol = rolRepository.findByNombreRol("ROLE_REFUGIO")
                        .orElseThrow(() -> new RuntimeException("Rol REFUGIO no encontrado"));
            } else {
                rol = rolRepository.findByNombreRol("ROLE_ADOPTANTE")
                        .orElseThrow(() -> new RuntimeException("Rol ADOPTANTE no encontrado"));
            }

            Set<Rol> roles = new HashSet<>();
            roles.add(rol);
            usuario.setRoles(roles);

            // Establecer valores por defecto
            if (usuario.getCiudad() == null || usuario.getCiudad().isEmpty()) {
                usuario.setCiudad("Bogotá");
            }
            usuario.setActivo(true);

            usuarioService.guardar(usuario);

            if ("refugio".equals(tipoRegistro)) {
                flash.addFlashAttribute("success", "Registro de refugio exitoso. Inicia sesión y completa tu perfil");
            } else {
                flash.addFlashAttribute("success", "Registro exitoso. Ya puedes iniciar sesión");
            }
            return "redirect:/login";

        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar usuario: " + e.getMessage());
            model.addAttribute("tipo", tipoRegistro);
            return "register";
        }
    }

    @GetMapping("/perfil")
    public String perfil(Model model, Authentication auth) {
        if (auth == null) {
            return "redirect:/login";
        }

        Usuario usuario = usuarioService.buscarPorEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        model.addAttribute("usuario", usuario);

        // Redirigir según el rol
        if (usuario.getRoles().stream().anyMatch(r -> r.getNombreRol().equals("ROLE_ADMIN"))) {
            return "redirect:/admin/dashboard";
        } else if (usuario.getRoles().stream().anyMatch(r -> r.getNombreRol().equals("ROLE_REFUGIO"))) {
            return "redirect:/refugio/dashboard";
        } else {
            return "redirect:/adoptante/perfil";
        }
    }

    @GetMapping("/cambiar-password")
    public String formularioCambiarPassword(Authentication auth, Model model) {
        if (auth == null) {
            return "redirect:/login";
        }
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        model.addAttribute("usuario", usuario);
        return "cambiar-password";
    }

    @PostMapping("/cambiar-password")
    public String cambiarPassword(@RequestParam String passwordActual,
                                  @RequestParam String passwordNueva,
                                  @RequestParam String confirmPassword,
                                  Authentication auth,
                                  RedirectAttributes flash) {
        try {
            Usuario usuario = usuarioService.buscarPorEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Verificar contraseña actual
            if (!passwordEncoder.matches(passwordActual, usuario.getPassword())) {
                flash.addFlashAttribute("error", "La contraseña actual es incorrecta");
                return "redirect:/cambiar-password";
            }

            // Validar nueva contraseña
            if (!passwordNueva.equals(confirmPassword)) {
                flash.addFlashAttribute("error", "Las contraseñas nuevas no coinciden");
                return "redirect:/cambiar-password";
            }

            if (passwordNueva.length() < 6) {
                flash.addFlashAttribute("error", "La contraseña debe tener al menos 6 caracteres");
                return "redirect:/cambiar-password";
            }

            // Actualizar contraseña
            usuario.setPassword(passwordEncoder.encode(passwordNueva));
            usuarioService.guardar(usuario);

            flash.addFlashAttribute("success", "Contraseña actualizada exitosamente");
            return "redirect:/perfil";

        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al cambiar contraseña: " + e.getMessage());
            return "redirect:/cambiar-password";
        }
    }
}