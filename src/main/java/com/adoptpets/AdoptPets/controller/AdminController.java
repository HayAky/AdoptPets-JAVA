package com.adoptpets.AdoptPets.controller;

import com.adoptpets.AdoptPets.model.Mascota;
import com.adoptpets.AdoptPets.model.Refugio;
import com.adoptpets.AdoptPets.model.Usuario;
import com.adoptpets.AdoptPets.model.enums.EstadoAdopcion;
import com.adoptpets.AdoptPets.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private MascotaService mascotaService;

    @Autowired
    private AdopcionService adopcionService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RefugioService refugioService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Estadísticas generales
        Long totalMascotas = (long) mascotaService.listarTodas().size();
        Long mascotasDisponibles = mascotaService.contarDisponibles();
        Long solicitudesPendientes = adopcionService.contarPorEstado(EstadoAdopcion.pendiente);
        Long adopcionesCompletadas = adopcionService.contarPorEstado(EstadoAdopcion.completada);
        Long totalUsuarios = usuarioService.contarTodos();
        Long totalRefugios = (long) refugioService.listarTodos().size();

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
    public String listarMascotas(@RequestParam(required = false) Long refugio, Model model) {
        if (refugio != null) {
            model.addAttribute("mascotas", mascotaService.buscarPorRefugio(refugio));
        } else {
            model.addAttribute("mascotas", mascotaService.listarTodas());
        }
        return "admin/mascotas/lista";
    }

    @GetMapping("/mascotas/nueva")
    public String formularioNuevaMascota(Model model) {
        model.addAttribute("mascota", new Mascota());
        model.addAttribute("refugios", refugioService.listarActivos());
        return "admin/mascotas/form";
    }

    @GetMapping("/mascotas/editar/{id}")
    public String formularioEditarMascota(@PathVariable Long id, Model model) {
        Mascota mascota = mascotaService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));
        model.addAttribute("mascota", mascota);
        model.addAttribute("refugios", refugioService.listarActivos());
        return "admin/mascotas/form";
    }

    @PostMapping("/mascotas/guardar")
    public String guardarMascota(@ModelAttribute Mascota mascota, RedirectAttributes flash) {
        if (mascota.getEstadoAdopcion() == null || mascota.getEstadoAdopcion().isEmpty()) {
            mascota.setEstadoAdopcion(EstadoAdopcion.valueOf("disponible"));
        }
        mascotaService.guardar(mascota);
        flash.addFlashAttribute("success", "Mascota guardada exitosamente");
        return "redirect:/admin/mascotas";
    }

    @GetMapping("/mascotas/eliminar/{id}")
    public String eliminarMascota(@PathVariable Long id, RedirectAttributes flash) {
        try {
            mascotaService.eliminar(id);
            flash.addFlashAttribute("success", "Mascota eliminada");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "No se puede eliminar la mascota. Puede tener adopciones asociadas.");
        }
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
        try {
            adopcionService.aprobarAdopcion(id);
            flash.addFlashAttribute("success", "Adopción aprobada exitosamente");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al aprobar la adopción: " + e.getMessage());
        }
        return "redirect:/admin/adopciones/pendientes";
    }

    @PostMapping("/adopciones/rechazar/{id}")
    public String rechazarAdopcion(@PathVariable Long id,
                                   @RequestParam String motivo,
                                   RedirectAttributes flash) {
        try {
            adopcionService.rechazarAdopcion(id, motivo);
            flash.addFlashAttribute("success", "Adopción rechazada");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al rechazar la adopción: " + e.getMessage());
        }
        return "redirect:/admin/adopciones/pendientes";
    }

    @PostMapping("/adopciones/completar/{id}")
    public String completarAdopcion(@PathVariable Long id, RedirectAttributes flash) {
        try {
            adopcionService.completarAdopcion(id);
            flash.addFlashAttribute("success", "Adopción completada exitosamente");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al completar la adopción: " + e.getMessage());
        }
        return "redirect:/admin/adopciones";
    }

    // --- GESTIÓN DE USUARIOS ---
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "admin/usuarios/lista";
    }

    @GetMapping("/usuarios/editar/{id}")
    public String editarUsuario(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        model.addAttribute("usuario", usuario);
        return "admin/usuarios/form";
    }

    @PostMapping("/usuarios/toggle/{id}")
    public String toggleUsuarioActivo(@PathVariable Long id, RedirectAttributes flash) {
        try {
            Usuario usuario = usuarioService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            usuario.setActivo(!usuario.getActivo());
            usuarioService.guardar(usuario);
            flash.addFlashAttribute("success",
                    "Usuario " + (usuario.getActivo() ? "activado" : "desactivado") + " exitosamente");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al actualizar usuario: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    // --- GESTIÓN DE REFUGIOS ---
    @GetMapping("/refugios")
    public String listarRefugios(Model model) {
        model.addAttribute("refugios", refugioService.listarTodos());
        return "admin/refugios/lista";
    }

    @GetMapping("/refugios/nuevo")
    public String formularioNuevoRefugio(Model model) {
        Refugio refugio = new Refugio();
        refugio.setActivo(true);
        model.addAttribute("refugio", refugio);
        return "admin/refugios/form";
    }

    @GetMapping("/refugios/editar/{id}")
    public String editarRefugio(@PathVariable Long id, Model model) {
        Refugio refugio = refugioService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Refugio no encontrado"));
        model.addAttribute("refugio", refugio);
        return "admin/refugios/form";
    }

    @PostMapping("/refugios/guardar")
    public String guardarRefugio(@ModelAttribute Refugio refugio, RedirectAttributes flash) {
        try {
            refugioService.guardar(refugio);
            flash.addFlashAttribute("success", "Refugio guardado exitosamente");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al guardar refugio: " + e.getMessage());
        }
        return "redirect:/admin/refugios";
    }

    @GetMapping("/refugios/eliminar/{id}")
    public String eliminarRefugio(@PathVariable Long id, RedirectAttributes flash) {
        try {
            refugioService.eliminar(id);
            flash.addFlashAttribute("success", "Refugio eliminado");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "No se puede eliminar el refugio. Puede tener mascotas asociadas.");
        }
        return "redirect:/admin/refugios";
    }

    @PostMapping("/refugios/toggle/{id}")
    public String toggleRefugioActivo(@PathVariable Long id, RedirectAttributes flash) {
        try {
            Refugio refugio = refugioService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Refugio no encontrado"));
            refugio.setActivo(!refugio.getActivo());
            refugioService.guardar(refugio);
            flash.addFlashAttribute("success",
                    "Refugio " + (refugio.getActivo() ? "activado" : "desactivado") + " exitosamente");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al actualizar refugio: " + e.getMessage());
        }
        return "redirect:/admin/refugios";
    }
}