package com.adoptpets.AdoptPets.controller;

import com.adoptpets.AdoptPets.model.Mascota;
import com.adoptpets.AdoptPets.model.Refugio;
import com.adoptpets.AdoptPets.model.Usuario;
import com.adoptpets.AdoptPets.model.enums.EstadoAdopcion;
import com.adoptpets.AdoptPets.repository.UsuarioRepository;
import com.adoptpets.AdoptPets.service.AdopcionService;
import com.adoptpets.AdoptPets.service.MascotaService;
import com.adoptpets.AdoptPets.service.RefugioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/refugio")
@PreAuthorize("hasAnyRole('REFUGIO', 'ADMIN')")
public class RefugioController {

    @Autowired
    private RefugioService refugioService;

    @Autowired
    private MascotaService mascotaService;

    @Autowired
    private AdopcionService adopcionService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Nota: Necesitarás agregar este método en RefugioService:
    // public Optional<Refugio> buscarPorUsuario(Usuario usuario)

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Buscar el refugio asociado al usuario
        Refugio refugio = refugioService.buscarPorUsuario(usuario).orElse(null);

        if (refugio != null) {
            // Estadísticas del refugio
            Long totalMascotas = refugioService.contarMascotasPorRefugio(refugio.getIdRefugio());
            Long mascotasDisponibles = refugioService.contarMascotasDisponiblesPorRefugio(refugio.getIdRefugio());
            Long solicitudesPendientes = adopcionService.listarPorRefugio(refugio.getIdRefugio())
                    .stream()
                    .filter(a -> a.getEstadoAdopcion() == EstadoAdopcion.pendiente)
                    .count();
            Long adopcionesCompletadas = adopcionService.listarPorRefugio(refugio.getIdRefugio())
                    .stream()
                    .filter(a -> a.getEstadoAdopcion() == EstadoAdopcion.completada)
                    .count();

            model.addAttribute("refugio", refugio);
            model.addAttribute("totalMascotas", totalMascotas);
            model.addAttribute("mascotasDisponibles", mascotasDisponibles);
            model.addAttribute("solicitudesPendientes", solicitudesPendientes);
            model.addAttribute("adopcionesCompletadas", adopcionesCompletadas);

            // Últimas mascotas
            List<Mascota> ultimasMascotas = mascotaService.buscarPorRefugio(refugio.getIdRefugio())
                    .stream()
                    .limit(5)
                    .toList();
            model.addAttribute("ultimasMascotas", ultimasMascotas);

            // Últimas adopciones pendientes
            List<com.adoptpets.AdoptPets.model.Adopcion> adopcionesPendientes =
                    adopcionService.listarPorRefugio(refugio.getIdRefugio())
                            .stream()
                            .filter(a -> a.getEstadoAdopcion() == EstadoAdopcion.pendiente)
                            .limit(5)
                            .toList();
            model.addAttribute("adopcionesPendientes", adopcionesPendientes);
        } else {
            model.addAttribute("sinRefugio", true);
        }

        model.addAttribute("usuario", usuario);
        return "refugio/dashboard";
    }

    @GetMapping("/mascotas")
    public String listarMascotas(Model model, Authentication auth) {
        Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Refugio refugio = refugioService.buscarPorUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Refugio no encontrado"));

        List<Mascota> mascotas = mascotaService.buscarPorRefugio(refugio.getIdRefugio());
        model.addAttribute("mascotas", mascotas);
        model.addAttribute("refugio", refugio);

        return "refugio/mascotas/lista";
    }

    @GetMapping("/mascotas/nueva")
    public String nuevaMascota(Model model, Authentication auth) {
        Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Refugio refugio = refugioService.buscarPorUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Refugio no encontrado"));

        Mascota mascota = new Mascota();
        mascota.setRefugio(refugio);
        mascota.setEstadoAdopcion(EstadoAdopcion.disponible);

        model.addAttribute("mascota", mascota);
        return "refugio/mascotas/form";
    }

    @GetMapping("/mascotas/editar/{id}")
    public String editarMascota(@PathVariable Long id, Model model, Authentication auth) {
        Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Refugio refugio = refugioService.buscarPorUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Refugio no encontrado"));

        Mascota mascota = mascotaService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));

        // Verificar que la mascota pertenece al refugio
        if (!mascota.getRefugio().getIdRefugio().equals(refugio.getIdRefugio())) {
            throw new RuntimeException("No autorizado");
        }

        model.addAttribute("mascota", mascota);
        return "refugio/mascotas/form";
    }

    @PostMapping("/mascotas/guardar")
    public String guardarMascota(@ModelAttribute Mascota mascota,
                                 Authentication auth,
                                 RedirectAttributes flash) {
        try {
            Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Refugio refugio = refugioService.buscarPorUsuario(usuario)
                    .orElseThrow(() -> new RuntimeException("Refugio no encontrado"));

            // Asegurar que la mascota pertenece al refugio del usuario
            mascota.setRefugio(refugio);

            mascotaService.guardar(mascota);
            flash.addFlashAttribute("success", "Mascota guardada exitosamente");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al guardar mascota: " + e.getMessage());
        }
        return "redirect:/refugio/mascotas";
    }

    @GetMapping("/mascotas/eliminar/{id}")
    public String eliminarMascota(@PathVariable Long id,
                                  Authentication auth,
                                  RedirectAttributes flash) {
        try {
            Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Refugio refugio = refugioService.buscarPorUsuario(usuario)
                    .orElseThrow(() -> new RuntimeException("Refugio no encontrado"));

            Mascota mascota = mascotaService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));

            // Verificar que la mascota pertenece al refugio
            if (!mascota.getRefugio().getIdRefugio().equals(refugio.getIdRefugio())) {
                throw new RuntimeException("No autorizado");
            }

            mascotaService.eliminar(id);
            flash.addFlashAttribute("success", "Mascota eliminada exitosamente");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al eliminar mascota: " + e.getMessage());
        }
        return "redirect:/refugio/mascotas";
    }

    @GetMapping("/adopciones")
    public String listarAdopciones(Model model,
                                   @RequestParam(required = false) String estado,
                                   Authentication auth) {
        Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Refugio refugio = refugioService.buscarPorUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Refugio no encontrado"));

        List<com.adoptpets.AdoptPets.model.Adopcion> adopciones =
                adopcionService.listarPorRefugio(refugio.getIdRefugio());

        // Filtrar por estado si se especifica
        if (estado != null && !estado.isEmpty()) {
            try {
                EstadoAdopcion estadoEnum = EstadoAdopcion.valueOf(estado.toUpperCase());
                adopciones = adopciones.stream()
                        .filter(a -> a.getEstadoAdopcion() == estadoEnum)
                        .toList();
                model.addAttribute("estadoFiltro", estado);
            } catch (IllegalArgumentException e) {
                // Estado inválido, mostrar todas
            }
        }

        model.addAttribute("adopciones", adopciones);
        model.addAttribute("refugio", refugio);

        return "refugio/adopciones/lista";
    }

    @GetMapping("/adopciones/{id}")
    public String verDetalleAdopcion(@PathVariable Long id,
                                     Model model,
                                     Authentication auth) {
        Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Refugio refugio = refugioService.buscarPorUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("Refugio no encontrado"));

        com.adoptpets.AdoptPets.model.Adopcion adopcion = adopcionService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Adopción no encontrada"));

        // Verificar que la adopción pertenece a una mascota del refugio
        if (!adopcion.getMascota().getRefugio().getIdRefugio().equals(refugio.getIdRefugio())) {
            throw new RuntimeException("No autorizado");
        }

        model.addAttribute("adopcion", adopcion);
        return "refugio/adopciones/detalle";
    }

    @PostMapping("/adopciones/aprobar/{id}")
    public String aprobarAdopcion(@PathVariable Long id,
                                  Authentication auth,
                                  RedirectAttributes flash) {
        try {
            Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Refugio refugio = refugioService.buscarPorUsuario(usuario)
                    .orElseThrow(() -> new RuntimeException("Refugio no encontrado"));

            com.adoptpets.AdoptPets.model.Adopcion adopcion = adopcionService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Adopción no encontrada"));

            // Verificar autorización
            if (!adopcion.getMascota().getRefugio().getIdRefugio().equals(refugio.getIdRefugio())) {
                throw new RuntimeException("No autorizado");
            }

            adopcionService.aprobarAdopcion(id);
            flash.addFlashAttribute("success", "Adopción aprobada exitosamente");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al aprobar adopción: " + e.getMessage());
        }
        return "redirect:/refugio/adopciones";
    }

    @PostMapping("/adopciones/rechazar/{id}")
    public String rechazarAdopcion(@PathVariable Long id,
                                   @RequestParam String motivo,
                                   Authentication auth,
                                   RedirectAttributes flash) {
        try {
            Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Refugio refugio = refugioService.buscarPorUsuario(usuario)
                    .orElseThrow(() -> new RuntimeException("Refugio no encontrado"));

            com.adoptpets.AdoptPets.model.Adopcion adopcion = adopcionService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Adopción no encontrada"));

            // Verificar autorización
            if (!adopcion.getMascota().getRefugio().getIdRefugio().equals(refugio.getIdRefugio())) {
                throw new RuntimeException("No autorizado");
            }

            adopcionService.rechazarAdopcion(id, motivo);
            flash.addFlashAttribute("success", "Adopción rechazada");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al rechazar adopción: " + e.getMessage());
        }
        return "redirect:/refugio/adopciones";
    }

    @GetMapping("/perfil")
    public String verPerfil(Model model, Authentication auth) {
        Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Refugio refugio = refugioService.buscarPorUsuario(usuario).orElse(new Refugio());

        model.addAttribute("usuario", usuario);
        model.addAttribute("refugio", refugio);

        return "refugio/perfil";
    }

    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(@ModelAttribute Refugio refugio,
                                   Authentication auth,
                                   RedirectAttributes flash) {
        try {
            Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Si el refugio es nuevo, vincularlo al usuario
            if (refugio.getIdRefugio() == null) {
                refugio.setActivo(true);
            }

            refugioService.guardar(refugio);
            flash.addFlashAttribute("success", "Información del refugio actualizada exitosamente");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al actualizar: " + e.getMessage());
        }
        return "redirect:/refugio/perfil";
    }
}