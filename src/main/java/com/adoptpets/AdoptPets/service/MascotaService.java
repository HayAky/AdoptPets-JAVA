package com.adoptpets.AdoptPets.service;

import com.adoptpets.AdoptPets.model.Mascota;
import com.adoptpets.AdoptPets.model.enums.EstadoAdopcion;
import com.adoptpets.AdoptPets.repository.MascotaRepository;
import com.adoptpets.AdoptPets.specification.MascotaSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
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

    /**
     * Búsqueda avanzada con múltiples filtros
     */
    public List<Mascota> buscarConFiltros(
            String especie,
            String sexo,
            String tamano,
            String raza,
            Integer edadMin,
            Integer edadMax,
            Boolean vacunado,
            Boolean esterilizado,
            Boolean microchip,
            Long refugioId,
            String busqueda
    ) {
        Specification<Mascota> spec = MascotaSpecification.conFiltros(
                especie, sexo, tamano, raza, edadMin, edadMax,
                vacunado, esterilizado, microchip, refugioId, busqueda
        );
        return mascotaRepository.findAll(spec);
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
        return mascotaRepository.findByEspecieAndEstadoAdopcion(especie, EstadoAdopcion.disponible);
    }

    public List<Mascota> buscar(String termino) {
        return mascotaRepository.buscarMascotas(termino);
    }

    public Long contarDisponibles() {
        return mascotaRepository.countDisponibles();
    }

    public Long contarPorRefugioYEstado(Long refugioId, String estado) {
        EstadoAdopcion enumEstado = EstadoAdopcion.valueOf(estado);
        return mascotaRepository.countByRefugioAndEstado(refugioId, enumEstado);
    }

}