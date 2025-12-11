package com.adoptpets.AdoptPets.repository;

import com.adoptpets.AdoptPets.model.Refugio;
import com.adoptpets.AdoptPets.model.enums.EstadoAdopcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefugioRepository extends JpaRepository<Refugio, Long> {

    List<Refugio> findByActivoTrue();

    List<Refugio> findByLocalidad(String localidad);

    List<Refugio> findByLocalidadAndActivoTrue(String localidad);

    @Query("SELECT r FROM Refugio r WHERE r.activo = true ORDER BY r.nombreRefugio")
    List<Refugio> findAllActivos();

    @Query("SELECT COUNT(m) FROM Mascota m WHERE m.refugio.idRefugio = :refugioId")
    Long countMascotasByRefugio(@Param("refugioId") Long refugioId);

    @Query("SELECT COUNT(m) FROM Mascota m WHERE m.refugio.idRefugio = :refugioId AND m.estadoAdopcion = 'disponible'")
    Long countMascotasDisponiblesByRefugio(@Param("refugioId") Long refugioId);
}