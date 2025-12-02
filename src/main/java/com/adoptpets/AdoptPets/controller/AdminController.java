package com.adoptpets.AdoptPets.controller;

import com.adoptpets.AdoptPets.model.enums.EstadoAdopcion;
import com.adoptpets.AdoptPets.service.AdopcionService;
import com.adoptpets.AdoptPets.service.MascotaService;
import com.adoptpets.AdoptPets.repository.RefugioRepository;
import com.adoptpets.AdoptPets.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdopcionService adopcionService;

    @Autowired
    private MascotaService mascotaService;

    @Autowired
    private RefugioRepository refugioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Estadísticas generales
        Long totalMascotas = mascotaService.listarTodas().size();
        Long mascotasDisponibles = (long) mascotaService.listarDisponibles().size();
        Long adopcionesPendientes = adopcionService.contarPorEstado(EstadoAdopcion.PENDIENTE);
        Long adopcionesCompletadas = adopcionService.contarPorEstado(EstadoAdopcion.COMPLETADA);
        Long totalRefugios = refugioRepository.count();
        Long totalUsuarios = usuarioRepository.count();

        model.addAttribute("totalMascotas", totalMascotas);
        model.addAttribute("mascotasDisponibles", mascotasDisponibles);
        model.addAttribute("adopcionesPendientes", adopcionesPendientes);
        model.addAttribute("adopcionesCompletadas", adopcionesCompletadas);
        model.addAttribute("totalRefugios", totalRefugios);
        model.addAttribute("totalUsuarios", totalUsuarios);

        // Adopciones recientes
        model.addAttribute("adopcionesRecientes", adopcionService.listarPorEstado(EstadoAdopcion.PENDIENTE));

        // Estadísticas mensuales
        int yearActual = LocalDate.now().getYear();
        model.addAttribute("estadisticasMensuales", adopcionService.estadisticasMensuales(yearActual));

        return "admin/dashboard";
    }

    @GetMapping("/mascotas")
    public String listarMascotas(Model model) {
        model.addAttribute("mascotas", mascotaService.listarTodas());
        return "admin/mascotas";
    }

    @GetMapping("/mascotas/nueva")
    public String nuevaMascota(Model model) {
        model.addAttribute("refugios", refugioRepository.findByActivoTrue());
        return "admin/mascota-form";
    }

    @GetMapping("/mascotas/editar/{id}")
    public String editarMascota(@PathVariable Long id, Model model) {
        model.addAttribute("mascota", mascotaService.buscarPorId(id).orElseThrow());
        model.addAttribute("refugios", refugioRepository.findByActivoTrue());
        return "admin/mascota-form";
    }

    @GetMapping("/adopciones")
    public String listarAdopciones(Model model) {
        model.addAttribute("adopciones", adopcionService.listarTodas());
        return "admin/adopciones";
    }

    @PostMapping("/adopciones/{id}/aprobar")
    public String aprobarAdopcion(@PathVariable Long id) {
        adopcionService.aprobar(id);
        return "redirect:/admin/adopciones";
    }

    @PostMapping("/adopciones/{id}/rechazar")
    public String rechazarAdopcion(@PathVariable Long id, @RequestParam String motivo) {
        adopcionService.rechazar(id, motivo);
        return "redirect:/admin/adopciones";
    }

    @GetMapping("/refugios")
    public String listarRefugios(Model model) {
        model.addAttribute("refugios", refugioRepository.findAll());
        return "admin/refugios";
    }

    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "admin/usuarios";
    }

    @GetMapping("/reportes")
    public String reportes(Model model) {
        // Datos para reportes
        Map<String, Object> reporteData = new HashMap<>();
        reporteData.put("totalAdopciones", adopcionService.listarTodas().size());
        reporteData.put("tasaExito", calcularTasaExito());

        model.addAttribute("reporteData", reporteData);
        return "admin/reportes";
    }

    private double calcularTasaExito() {
        long total = adopcionService.listarTodas().size();
        long completadas = adopcionService.contarPorEstado(EstadoAdopcion.COMPLETADA);
        return total > 0 ? (completadas * 100.0 / total) : 0;
    }
}