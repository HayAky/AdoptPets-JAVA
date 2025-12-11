package com.adoptpets.AdoptPets.controller;

import com.adoptpets.AdoptPets.model.Adopcion;
import com.adoptpets.AdoptPets.model.Mascota;
import com.adoptpets.AdoptPets.model.Usuario;
import com.adoptpets.AdoptPets.model.enums.EstadoAdopcion;
import com.adoptpets.AdoptPets.repository.UsuarioRepository;
import com.adoptpets.AdoptPets.service.AdopcionService;
import com.adoptpets.AdoptPets.service.MascotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/adoptante")
@PreAuthorize("hasAnyRole('ADOPTANTE', 'ADMIN')")
public class AdoptanteController {

    @Autowired
    private MascotaService mascotaService;

    @Autowired
    private AdopcionService adopcionService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Adopcion> misAdopciones = adopcionService.listarPorUsuario(usuario);

        Long solicitudesPendientes = misAdopciones.stream()
                .filter(a -> a.getEstadoAdopcion().name().equals("PENDIENTE"))
                .count();

        Long solicitudesAprobadas = misAdopciones.stream()
                .filter(a -> a.getEstadoAdopcion().name().equals("APROBADA"))
                .count();

        Long adopcionesCompletadas = misAdopciones.stream()
                .filter(a -> a.getEstadoAdopcion().name().equals("COMPLETADA"))
                .count();

        model.addAttribute("usuario", usuario);
        model.addAttribute("misAdopciones", misAdopciones);
        model.addAttribute("solicitudesPendientes", solicitudesPendientes);
        model.addAttribute("solicitudesAprobadas", solicitudesAprobadas);
        model.addAttribute("adopcionesCompletadas", adopcionesCompletadas);

        return "adoptante/dashboard";
    }

    @GetMapping("/mascotas")
    public String verMascotasDisponibles(Model model,
                                         @RequestParam(required = false) String especie,
                                         @RequestParam(required = false) String sexo,
                                         @RequestParam(required = false) String busqueda) {
        List<Mascota> mascotas;

        if (busqueda != null && !busqueda.isEmpty()) {
            mascotas = mascotaService.buscar(busqueda);
        } else if (especie != null && !especie.isEmpty()) {
            mascotas = mascotaService.buscarPorEspecie(especie);
        } else {
            mascotas = mascotaService.listarDisponibles();
        }

        // Filtro adicional por sexo si se especifica
        if (sexo != null && !sexo.isEmpty()) {
            String sexoLower = sexo.toLowerCase();
            mascotas = mascotas.stream()
                    .filter(m -> m.getSexo().toLowerCase().equals(sexoLower))
                    .toList();
        }

        model.addAttribute("mascotas", mascotas);
        model.addAttribute("busqueda", busqueda);
        model.addAttribute("especie", especie);
        model.addAttribute("sexo", sexo);

        return "adoptante/mascotas";
    }

    @GetMapping("/mascotas/{id}")
    public String verDetalleMascota(@PathVariable Long id, Model model) {
        Mascota mascota = mascotaService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));

        model.addAttribute("mascota", mascota);
        return "adoptante/mascota-detalle";
    }

    @GetMapping("/adopciones")
    public String misAdopciones(Model model, Authentication auth) {
        Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Adopcion> adopciones = adopcionService.listarPorUsuario(usuario);
        model.addAttribute("adopciones", adopciones);

        return "adoptante/mis-adopciones";
    }

    @GetMapping("/adopciones/{id}")
    public String verDetalleAdopcion(@PathVariable Long id, Model model, Authentication auth) {
        Adopcion adopcion = adopcionService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Adopción no encontrada"));

        Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar que la adopción pertenece al usuario
        if (!adopcion.getAdoptante().getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new RuntimeException("No autorizado");
        }

        model.addAttribute("adopcion", adopcion);
        return "adoptante/adopcion-detalle";
    }

    @PostMapping("/solicitar-adopcion/{mascotaId}")
    public String solicitarAdopcion(@PathVariable Long mascotaId,
                                    @RequestParam String observaciones,
                                    Authentication auth,
                                    RedirectAttributes flash) {
        Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Mascota mascota = mascotaService.buscarPorId(mascotaId)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));

        if (mascota.getEstadoAdopcion() != EstadoAdopcion.disponible) {
            flash.addFlashAttribute("error", "La mascota ya no está disponible");
            return "redirect:/adoptante/mascotas";
        }

        adopcionService.crearSolicitud(usuario, mascota, observaciones);
        flash.addFlashAttribute("success", "Solicitud de adopción enviada exitosamente");

        return "redirect:/adoptante/adopciones";
    }

    @GetMapping("/perfil")
    public String verPerfil(Model model, Authentication auth) {
        Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        model.addAttribute("usuario", usuario);
        return "adoptante/perfil";
    }

    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(@ModelAttribute Usuario usuarioActualizado,
                                   Authentication auth,
                                   RedirectAttributes flash) {
        Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Actualizar solo campos permitidos
        usuario.setNombre(usuarioActualizado.getNombre());
        usuario.setApellido(usuarioActualizado.getApellido());
        usuario.setTelefono(usuarioActualizado.getTelefono());
        usuario.setDireccion(usuarioActualizado.getDireccion());
        usuario.setCiudad(usuarioActualizado.getCiudad());

        usuarioRepository.save(usuario);
        flash.addFlashAttribute("success", "Perfil actualizado exitosamente");

        return "redirect:/adoptante/perfil";
    }
}