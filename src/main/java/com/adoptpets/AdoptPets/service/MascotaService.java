package com.adoptpets.AdoptPets.service;

import com.adoptpets.AdoptPets.model.Mascota;
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
        return mascotaRepository.findMascotasDisponibles();
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

    public List<Mascota> buscarPorRefugio(Long refugioId) {
        return mascotaRepository.findByRefugioId(refugioId);
    }

    public List<Mascota> buscarPorEspecie(String especie) {
        return mascotaRepository.findByEspecieAndEstadoAdopcion(especie, "disponible");
    }

    public List<Mascota> buscar(String termino) {
        return mascotaRepository.buscarMascotas(termino);
    }

    public Long contarDisponibles() {
        return mascotaRepository.countDisponibles();
    }

    public Long contarPorRefugioYEstado(Long refugioId, String estado) {
        return mascotaRepository.countByRefugioAndEstado(refugioId, estado);
    }
}