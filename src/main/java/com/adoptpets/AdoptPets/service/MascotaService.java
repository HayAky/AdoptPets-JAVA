package com.adoptpets.AdoptPets.service;

import com.adoptpets.AdoptPets.model.Mascota;
import com.adoptpets.AdoptPets.model.Refugio;
import com.adoptpets.AdoptPets.repository.MascotaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MascotaService {

    @Autowired
    private MascotaRepository mascotaRepository;

    public List<Mascota> listarTodas() {
        return mascotaRepository.findAll();
    }

    public List<Mascota> listarDisponibles() {
        return mascotaRepository.findByEstado("disponible");
    }

    public List<Mascota> listarPorRefugio(Refugio refugio) {
        return mascotaRepository.findByRefugio(refugio);
    }

    public List<Mascota> buscarPorEspecieYEstado(String especie, String estado) {
        return mascotaRepository.findByEspecieAndEstado(especie, estado);
    }

    public Optional<Mascota> buscarPorId(Long id) {
        return mascotaRepository.findById(id);
    }

    public Mascota guardar(Mascota mascota) {
        return mascotaRepository.save(mascota);
    }

    public void eliminar(Long id) {
        mascotaRepository.deleteById(id);
    }

    public Long contarPorRefugioYEstado(Long refugioId, String estado) {
        return mascotaRepository.countByRefugioAndEstado(refugioId, estado);
    }

    public void cambiarEstado(Long id, String nuevoEstado) {
        Mascota mascota = mascotaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));
        mascota.setEstadoAdopcion(nuevoEstado);
        mascotaRepository.save(mascota);
    }
}