package com.adoptpets.AdoptPets.controller;

import com.adoptpets.AdoptPets.model.Adopcion;
import com.adoptpets.AdoptPets.model.Usuario;
import com.adoptpets.AdoptPets.service.AdopcionService;
import com.adoptpets.AdoptPets.service.MascotaService;
import com.adoptpets.AdoptPets.repository.UsuarioRepository;
import com.adoptpets.AdoptPets.repository.SeguimientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/adoptante")
public class AdoptanteController {

    @Autowired
    private AdopcionService adopcionService;

    @Autowired
    private MascotaService mascotaService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SeguimientoRepository seguimientoRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        Usuario usuario = obtenerUsuarioActual(auth);

        // Mis adopciones
        var misAdopciones = adopcionService.listarPorUsuario(usuario);
        model.addAttribute("misAdopciones", misAdopciones);

        // Seguimientos
        var seguimientos = seguimientoRepository.findByAdoptanteOrderByFechaSeguimientoDesc(usuario);
        model.addAttribute("seguimientos", seguimientos);

        // Estadísticas personales
        model.addAttribute("totalSolicitudes", misAdopciones.size());
        model.addAttribute("solicitudesPendientes",
                misAdopciones.stream().filter(a -> a.getEstadoAdopcion().name().equals("PENDIENTE")).count());

        return "adoptante/dashboard";
    }

    @GetMapping("/mascotas-disponibles")
    public String mascotasDisponibles(Model model) {
        model.addAttribute("mascotas", mascotaService.listarDisponibles());
        return "adoptante/mascotas-disponibles";
    }

    @GetMapping("/mascota/{id}")
    public String verMascota(@PathVariable Long id, Model model) {
        model.addAttribute("mascota", mascotaService.buscarPorId(id).orElseThrow());
        return "adoptante/mascota-detalle";
    }

    @PostMapping("/solicitar-adopcion/{mascotaId}")
    public String solicitarAdopcion(
            @PathVariable Long mascotaId,
            @RequestParam String observaciones,
            Authentication auth) {

        Usuario usuario = obtenerUsuarioActual(auth);
        var mascota = mascotaService.buscarPorId(mascotaId).orElseThrow();

        Adopcion adopcion = Adopcion.builder()
                .adoptante(usuario)
                .mascota(mascota)
                .observaciones(observaciones)
                .build();

        adopcionService.crearSolicitud(adopcion);
        return "redirect:/adoptante/mis-solicitudes";
    }

    @GetMapping("/mis-solicitudes")
    public String misSolicitudes(Model model, Authentication auth) {
        Usuario usuario = obtenerUsuarioActual(auth);
        model.addAttribute("solicitudes", adopcionService.listarPorUsuario(usuario));
        return "adoptante/mis-solicitudes";
    }

    @GetMapping("/mis-seguimientos")
    public String misSeguimientos(Model model, Authentication auth) {
        Usuario usuario = obtenerUsuarioActual(auth);
        model.addAttribute("seguimientos",
                seguimientoRepository.findByAdoptanteOrderByFechaSeguimientoDesc(usuario));
        return "adoptante/mis-seguimientos";
    }

    private Usuario obtenerUsuarioActual(Authentication auth) {
        String email = auth.getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}