package com.adoptpets.AdoptPets.controller;

import com.adoptpets.AdoptPets.service.MascotaService;
import com.adoptpets.AdoptPets.repository.RefugioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/mascotas")
public class MascotaController {

    @Autowired
    private MascotaService mascotaService;

    @Autowired
    private RefugioRepository refugioRepository;

    @GetMapping
    public String listarMascotasDisponibles(Model model) {
        model.addAttribute("mascotas", mascotaService.listarDisponibles());
        model.addAttribute("refugios", refugioRepository.findByActivoTrue());
        return "mascotas/lista";
    }

    @GetMapping("/{id}")
    public String verDetalle(@PathVariable Long id, Model model) {
        var mascota = mascotaService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));
        model.addAttribute("mascota", mascota);
        return "mascotas/detalle";
    }

    @GetMapping("/buscar")
    public String buscar(
            @RequestParam(required = false) String especie,
            @RequestParam(required = false) String tamano,
            @RequestParam(required = false) Long refugioId,
            Model model) {

        var mascotas = mascotaService.listarDisponibles();

        // Filtrar por especie
        if (especie != null && !especie.isEmpty()) {
            mascotas = mascotas.stream()
                    .filter(m -> m.getEspecie().equalsIgnoreCase(especie))
                    .toList();
        }

        // Filtrar por tamaño
        if (tamano != null && !tamano.isEmpty()) {
            mascotas = mascotas.stream()
                    .filter(m -> m.getTamano().equalsIgnoreCase(tamano))
                    .toList();
        }

        // Filtrar por refugio
        if (refugioId != null) {
            mascotas = mascotas.stream()
                    .filter(m -> m.getRefugio().getIdRefugio().equals(refugioId))
                    .toList();
        }

        model.addAttribute("mascotas", mascotas);
        model.addAttribute("refugios", refugioRepository.findByActivoTrue());
        return "mascotas/lista";
    }
}