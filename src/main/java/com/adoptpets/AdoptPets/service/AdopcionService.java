package com.adoptpets.AdoptPets.service;

import com.adoptpets.AdoptPets.model.Adopcion;
import com.adoptpets.AdoptPets.model.Mascota;
import com.adoptpets.AdoptPets.model.Usuario;
import com.adoptpets.AdoptPets.model.enums.EstadoAdopcion;
import com.adoptpets.AdoptPets.repository.AdopcionRepository;
import com.adoptpets.AdoptPets.repository.MascotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AdopcionService {

    @Autowired
    private AdopcionRepository adopcionRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    public List<Adopcion> listarTodas() {
        return adopcionRepository.findAll();
    }

    public List<Adopcion> listarPendientes() {
        return adopcionRepository.findAdopcionesPendientes();
    }

    public List<Adopcion> listarPorUsuario(Usuario usuario) {
        return adopcionRepository.findByAdoptante(usuario);
    }

    public List<Adopcion> listarPorUsuarioId(Long usuarioId) {
        return adopcionRepository.findByAdoptanteId(usuarioId);
    }

    public Optional<Adopcion> buscarPorId(Long id) {
        return adopcionRepository.findById(id);
    }

    public Adopcion crearSolicitud(Usuario adoptante, Mascota mascota, String observaciones) {
        Adopcion adopcion = Adopcion.builder()
                .adoptante(adoptante)
                .mascota(mascota)
                .fechaSolicitud(LocalDate.now())
                .estadoAdopcion(EstadoAdopcion.pendiente)
                .observaciones(observaciones)
                .formularioEnviado(false)
                .build();

        // Actualizar estado de la mascota
        mascota.setEstadoAdopcion(EstadoAdopcion.valueOf("en proceso"));
        mascotaRepository.save(mascota);

        return adopcionRepository.save(adopcion);
    }

    public Adopcion aprobarAdopcion(Long adopcionId) {
        Adopcion adopcion = adopcionRepository.findById(adopcionId)
                .orElseThrow(() -> new RuntimeException("Adopción no encontrada"));

        adopcion.setEstadoAdopcion(EstadoAdopcion.aprobada);
        adopcion.setFechaAprobacion(LocalDate.now());

        return adopcionRepository.save(adopcion);
    }

    public Adopcion rechazarAdopcion(Long adopcionId, String motivo) {
        Adopcion adopcion = adopcionRepository.findById(adopcionId)
                .orElseThrow(() -> new RuntimeException("Adopción no encontrada"));

        adopcion.setEstadoAdopcion(EstadoAdopcion.rechazada);
        adopcion.setObservaciones(adopcion.getObservaciones() + "\nMotivo rechazo: " + motivo);

        // Devolver mascota a disponible
        Mascota mascota = adopcion.getMascota();
        mascota.setEstadoAdopcion(EstadoAdopcion.valueOf("disponible"));
        mascotaRepository.save(mascota);

        return adopcionRepository.save(adopcion);
    }

    public Adopcion completarAdopcion(Long adopcionId) {
        Adopcion adopcion = adopcionRepository.findById(adopcionId)
                .orElseThrow(() -> new RuntimeException("Adopción no encontrada"));

        adopcion.setEstadoAdopcion(EstadoAdopcion.completada);

        // Actualizar estado de la mascota
        Mascota mascota = adopcion.getMascota();
        mascota.setEstadoAdopcion(EstadoAdopcion.valueOf("adoptada"));
        mascotaRepository.save(mascota);

        return adopcionRepository.save(adopcion);
    }

    public Long contarPorEstado(EstadoAdopcion estado) {
        return adopcionRepository.countByEstado(estado);
    }

    public List<Adopcion> listarPorRefugio(Long refugioId) {
        return adopcionRepository.findByRefugioId(refugioId);
    }
}