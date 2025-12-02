package com.adoptpets.AdoptPets.service;

import com.adoptpets.AdoptPets.model.Seguimiento;
import com.adoptpets.AdoptPets.model.Usuario;
import com.adoptpets.AdoptPets.model.Mascota;
import com.adoptpets.AdoptPets.repository.SeguimientoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SeguimientoService {

    @Autowired
    private SeguimientoRepository seguimientoRepository;

    public List<Seguimiento> listarPorAdoptante(Usuario adoptante) {
        return seguimientoRepository.findByAdoptanteOrderByFechaSeguimientoDesc(adoptante);
    }

    public List<Seguimiento> listarPorMascota(Mascota mascota) {
        return seguimientoRepository.findByMascotaOrderByFechaSeguimientoDesc(mascota);
    }

    public List<Seguimiento> proximosSeguimientos(LocalDate fecha) {
        return seguimientoRepository.findProximosSeguimientos(fecha);
    }

    public List<Seguimiento> seguimientosPendientes() {
        return seguimientoRepository.findSeguimientosPendientes();
    }

    public Optional<Seguimiento> buscarPorId(Long id) {
        return seguimientoRepository.findById(id);
    }

    public Seguimiento guardar(Seguimiento seguimiento) {
        return seguimientoRepository.save(seguimiento);
    }

    public void eliminar(Long id) {
        seguimientoRepository.deleteById(id);
    }
}