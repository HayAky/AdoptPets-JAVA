package com.adoptpets.AdoptPets.service;

import com.adoptpets.AdoptPets.model.GaleriaFoto;
import com.adoptpets.AdoptPets.model.Mascota;
import com.adoptpets.AdoptPets.repository.GaleriaFotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GaleriaFotoService {

    @Autowired
    private GaleriaFotoRepository galeriaFotoRepository;

    public List<GaleriaFoto> listarTodas() {
        return galeriaFotoRepository.findAll();
    }

    public List<GaleriaFoto> listarPorMascota(Mascota mascota) {
        return galeriaFotoRepository.findByMascota(mascota);
    }

    public Optional<GaleriaFoto> buscarFotoPrincipal(Mascota mascota) {
        return galeriaFotoRepository.findByMascotaAndEsPrincipalTrue(mascota);
    }

    public Optional<GaleriaFoto> buscarPorId(Long id) {
        return galeriaFotoRepository.findById(id);
    }

    public GaleriaFoto guardar(GaleriaFoto foto) {
        return galeriaFotoRepository.save(foto);
    }

    public void eliminar(Long id) {
        galeriaFotoRepository.deleteById(id);
    }
}