package com.adoptpets.AdoptPets.service;

import com.adoptpets.AdoptPets.model.Adopcion;
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

    public List<Adopcion> listarPorUsuario(Usuario usuario) {
        return adopcionRepository.findByAdoptante(usuario);
    }

    public List<Adopcion> listarPorEstado(EstadoAdopcion estado) {
        return adopcionRepository.findByEstadoAdopcion(estado);
    }

    public List<Adopcion> listarPendientes() {
        return adopcionRepository.findByEstadoAdopcion(EstadoAdopcion.PENDIENTE);
    }

    public Optional<Adopcion> buscarPorId(Long id) {
        return adopcionRepository.findById(id);
    }

    public Adopcion crearSolicitud(Adopcion adopcion) {
        adopcion.setFechaSolicitud(LocalDate.now());
        adopcion.setEstadoAdopcion(EstadoAdopcion.PENDIENTE);

        // Cambiar estado de la mascota
        var mascota = adopcion.getMascota();
        mascota.setEstadoAdopcion("en proceso");
        mascotaRepository.save(mascota);

        return adopcionRepository.save(adopcion);
    }

    public Adopcion aprobar(Long id) {
        Adopcion adopcion = adopcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Adopción no encontrada"));
        adopcion.setEstadoAdopcion(EstadoAdopcion.APROBADA);
        adopcion.setFechaAprobacion(LocalDate.now());
        return adopcionRepository.save(adopcion);
    }

    public Adopcion rechazar(Long id, String motivo) {
        Adopcion adopcion = adopcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Adopción no encontrada"));
        adopcion.setEstadoAdopcion(EstadoAdopcion.RECHAZADA);
        adopcion.setObservaciones(motivo);

        // Devolver mascota a disponible
        var mascota = adopcion.getMascota();
        mascota.setEstadoAdopcion("disponible");
        mascotaRepository.save(mascota);

        return adopcionRepository.save(adopcion);
    }

    public Adopcion completar(Long id) {
        Adopcion adopcion = adopcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Adopción no encontrada"));
        adopcion.setEstadoAdopcion(EstadoAdopcion.COMPLETADA);

        // Marcar mascota como adoptada
        var mascota = adopcion.getMascota();
        mascota.setEstadoAdopcion("adoptado");
        mascotaRepository.save(mascota);

        return adopcionRepository.save(adopcion);
    }

    public Long contarPorEstado(EstadoAdopcion estado) {
        return adopcionRepository.countByEstadoAdopcion(estado);
    }

    public List<Object[]> estadisticasMensuales(int year) {
        return adopcionRepository.countAdopcionesPorMes(year);
    }
}