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
        return refugioRepository.findByActivoTrue();
    }

    public List<Refugio> buscarPorLocalidad(String localidad) {
        return refugioRepository.findByLocalidadAndActivoTrue(localidad);
    }

    public List<Refugio> buscarPorNombre(String nombre) {
        return refugioRepository.findByNombreRefugioContainingIgnoreCase(nombre);
    }

    public Optional<Refugio> buscarPorId(Long id) {
        return refugioRepository.findById(id);
    }

    public Optional<Refugio> buscarPorEmail(String email) {
        return refugioRepository.findByEmail(email);
    }

    public Refugio guardar(Refugio refugio) {
        return refugioRepository.save(refugio);
    }

    public void eliminar(Long id) {
        refugioRepository.deleteById(id);
    }

    public void activarDesactivar(Long id, boolean activo) {
        Refugio refugio = refugioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Refugio no encontrado"));
        refugio.setActivo(activo);
        refugioRepository.save(refugio);
    }

    public Long contarMascotasPorRefugio(Long refugioId) {
        return refugioRepository.countMascotasByRefugio(refugioId);
    }

    public List<Refugio> refugiosConCapacidad() {
        return refugioRepository.findRefugiosConCapacidad();
    }
}