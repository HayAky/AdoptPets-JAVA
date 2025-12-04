package com.adoptpets.AdoptPets.service;

import com.adoptpets.AdoptPets.model.Refugio;
import com.adoptpets.AdoptPets.repository.RefugioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RefugioService {

    @Autowired
    private RefugioRepository refugioRepository;

    public List<Refugio> listarTodos() {
        return refugioRepository.findAll();
    }

    public List<Refugio> listarActivos() {
        return refugioRepository.findAllActivos();
    }

    public Optional<Refugio> buscarPorId(Long id) {
        return refugioRepository.findById(id);
    }

    public Refugio guardar(Refugio refugio) {
        return refugioRepository.save(refugio);
    }

    public void eliminar(Long id) {
        refugioRepository.deleteById(id);
    }

    public List<Refugio> buscarPorLocalidad(String localidad) {
        return refugioRepository.findByLocalidadAndActivoTrue(localidad);
    }

    public Long contarMascotasPorRefugio(Long refugioId) {
        return refugioRepository.countMascotasByRefugio(refugioId);
    }

    public Long contarMascotasDisponiblesPorRefugio(Long refugioId) {
        return refugioRepository.countMascotasDisponiblesByRefugio(refugioId);
    }
}