package com.adoptpets.AdoptPets.controller;

import com.adoptpets.AdoptPets.model.Mascota;
import com.adoptpets.AdoptPets.model.Refugio;
import com.adoptpets.AdoptPets.model.Usuario;
import com.adoptpets.AdoptPets.model.enums.EstadoAdopcion;
import com.adoptpets.AdoptPets.repository.RefugioRepository;
import com.adoptpets.AdoptPets.repository.UsuarioRepository;
import com.adoptpets.AdoptPets.service.AdopcionService;
import com.adoptpets.AdoptPets.service.MascotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private MascotaService mascotaService;

    @Autowired
    private AdopcionService adopcionService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RefugioRepository refugioRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Estadísticas generales
        Long totalMascotas = (long) mascotaService.listarTodas().size();
        Long mascotasDisponibles = mascotaService.contarDisponibles();
        Long solicitudesPendientes = adopcionService.contarPorEstado(EstadoAdopcion.pendiente);
        Long adopcionesCompletadas = adopcionService.contarPorEstado(EstadoAdopcion.completada);
        Long totalUsuarios = usuarioRepository.count();
        Long totalRefugios = refugioRepository.count();

        model.addAttribute("totalMascotas", totalMascotas);
        model.addAttribute("mascotasDisponibles", mascotasDisponibles);
        model.addAttribute("solicitudesPendientes", solicitudesPendientes);
        model.addAttribute("adopcionesCompletadas", adopcionesCompletadas);
        model.addAttribute("totalUsuarios", totalUsuarios);
        model.addAttribute("totalRefugios", totalRefugios);

        // Listas recientes
        model.addAttribute("adopcionesPendientes", adopcionService.listarPendientes());
        model.addAttribute("ultimasMascotas", mascotaService.listarDisponibles().stream().limit(5).toList());

        return "admin/dashboard";
    }

    // --- GESTIÓN DE MASCOTAS ---
    @GetMapping("/mascotas")
    public String listarMascotas(Model model) {
        model.addAttribute("mascotas", mascotaService.listarTodas());
        return "admin/mascotas/lista";
    }

    @GetMapping("/mascotas/nueva")
    public String formularioNuevaMascota(Model model) {
        model.addAttribute("mascota", new Mascota());
        model.addAttribute("refugios", refugioRepository.findAllActivos());
        return "admin/mascotas/form";
    }

    @GetMapping("/mascotas/editar/{id}")
    public String formularioEditarMascota(@PathVariable Long id, Model model) {
        Mascota mascota = mascotaService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));
        model.addAttribute("mascota", mascota);
        model.addAttribute("refugios", refugioRepository.findAllActivos());
        return "admin/mascotas/form";
    }

    @PostMapping("/mascotas/guardar")
    public String guardarMascota(@ModelAttribute Mascota mascota, RedirectAttributes flash) {
        mascotaService.guardar(mascota);
        flash.addFlashAttribute("success", "Mascota guardada exitosamente");
        return "redirect:/admin/mascotas";
    }

    @GetMapping("/mascotas/eliminar/{id}")
    public String eliminarMascota(@PathVariable Long id, RedirectAttributes flash) {
        mascotaService.eliminar(id);
        flash.addFlashAttribute("success", "Mascota eliminada");
        return "redirect:/admin/mascotas";
    }

    // --- GESTIÓN DE ADOPCIONES ---
    @GetMapping("/adopciones")
    public String listarAdopciones(Model model) {
        model.addAttribute("adopciones", adopcionService.listarTodas());
        return "admin/adopciones/lista";
    }

    @GetMapping("/adopciones/pendientes")
    public String adopcionesPendientes(Model model) {
        model.addAttribute("adopciones", adopcionService.listarPendientes());
        return "admin/adopciones/pendientes";
    }

    @PostMapping("/adopciones/aprobar/{id}")
    public String aprobarAdopcion(@PathVariable Long id, RedirectAttributes flash) {
        adopcionService.aprobarAdopcion(id);
        flash.addFlashAttribute("success", "Adopción aprobada");
        return "redirect:/admin/adopciones/pendientes";
    }

    @PostMapping("/adopciones/rechazar/{id}")
    public String rechazarAdopcion(@PathVariable Long id,
                                   @RequestParam String motivo,
                                   RedirectAttributes flash) {
        adopcionService.rechazarAdopcion(id, motivo);
        flash.addFlashAttribute("success", "Adopción rechazada");
        return "redirect:/admin/adopciones/pendientes";
    }

    // --- GESTIÓN DE USUARIOS ---
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "admin/usuarios/lista";
    }

    @GetMapping("/usuarios/editar/{id}")
    public String editarUsuario(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        model.addAttribute("usuario", usuario);
        return "admin/usuarios/form";
    }

    // --- GESTIÓN DE REFUGIOS ---
    @GetMapping("/refugios")
    public String listarRefugios(Model model) {
        model.addAttribute("refugios", refugioRepository.findAll());
        return "admin/refugios/lista";
    }

    @GetMapping("/refugios/nuevo")
    public String formularioNuevoRefugio(Model model) {
        model.addAttribute("refugio", new Refugio());
        return "admin/refugios/form";
    }

    @PostMapping("/refugios/guardar")
    public String guardarRefugio(@ModelAttribute Refugio refugio, RedirectAttributes flash) {
        refugioRepository.save(refugio);
        flash.addFlashAttribute("success", "Refugio guardado exitosamente");
        return "redirect:/admin/refugios";
    }
}