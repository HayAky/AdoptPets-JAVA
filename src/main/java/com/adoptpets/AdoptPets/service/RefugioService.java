package com.adoptpets.AdoptPets.service;

import com.adoptpets.AdoptPets.model.Refugio;
import com.adoptpets.AdoptPets.repository.RefugioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RefugioService {

    private final RefugioRepository refugioRepository;

    public RefugioService(RefugioRepository refugioRepository) {
        this.refugioRepository = refugioRepository;
    }

    public List<Refugio> obtenerActivos() {
        return refugioRepository.findByActivoTrue();
    }

    public List<Refugio> obtenerPorLocalidad(String localidad) {
        return refugioRepository.findByLocalidad(localidad);
    }

    public List<Refugio> obtenerActivosPorLocalidad(String localidad) {
        return refugioRepository.findByLocalidadAndActivoTrue(localidad);
    }

    public List<Refugio> obtenerTodosActivosOrdenados() {
        return refugioRepository.findAllActivos();
    }

    public Long contarMascotas(Long refugioId) {
        return refugioRepository.countMascotasByRefugio(refugioId);
    }

    public Long contarMascotasDisponibles(Long refugioId) {
        return refugioRepository.countMascotasDisponiblesByRefugio(refugioId);
    }

    public Refugio guardar(Refugio refugio) {
        return refugioRepository.save(refugio);
    }

    public Refugio obtenerPorId(Long id) {
        return refugioRepository.findById(id).orElse(null);
    }

    public void eliminar(Long id) {
        refugioRepository.deleteById(id);
    }
}
