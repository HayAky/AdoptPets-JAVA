package com.adoptpets.AdoptPets.repository;

import com.adoptpets.AdoptPets.model.Refugio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefugioRepository extends JpaRepository<Refugio, Long> {

    // Buscar refugios activos
    List<Refugio> findByActivoTrue();

    // Buscar por localidad
    List<Refugio> findByLocalidad(String localidad);

    // Buscar por nombre (búsqueda parcial)
    List<Refugio> findByNombreRefugioContainingIgnoreCase(String nombre);

    // Buscar por email
    Optional<Refugio> findByEmail(String email);

    // Refugios con capacidad disponible
    @Query("SELECT r FROM Refugio r WHERE r.activo = true AND " +
            "(SELECT COUNT(m) FROM Mascota m WHERE m.refugio = r AND m.estadoAdopcion = 'disponible') < r.capacidadMaxima")
    List<Refugio> findRefugiosConCapacidad();

    // Contar mascotas por refugio
    @Query("SELECT COUNT(m) FROM Mascota m WHERE m.refugio.idRefugio = :refugioId")
    Long countMascotasByRefugio(@Param("refugioId") Long refugioId);

    // Refugios por localidad activos
    List<Refugio> findByLocalidadAndActivoTrue(String localidad);
}