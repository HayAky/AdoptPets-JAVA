package com.adoptpets.AdoptPets.repository;

import com.adoptpets.AdoptPets.model.GaleriaFoto;
import com.adoptpets.AdoptPets.model.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GaleriaFotoRepository extends JpaRepository<GaleriaFoto, Long> {

    // Fotos de una mascota
    List<GaleriaFoto> findByMascota(Mascota mascota);

    // Foto principal de una mascota
    Optional<GaleriaFoto> findByMascotaAndEsPrincipalTrue(Mascota mascota);

    // Contar fotos de una mascota
    Long countByMascota(Mascota mascota);
}