package com.adoptpets.AdoptPets.repository;

import com.adoptpets.AdoptPets.model.Refugio;
import com.adoptpets.AdoptPets.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefugioRepository extends JpaRepository<Refugio, Long> {

    /**
     * Busca refugios activos
     */
    List<Refugio> findByActivoTrue();

    /**
     * Busca refugios por localidad
     */
    List<Refugio> findByLocalidad(String localidad);

    /**
     * Busca refugios activos por localidad
     */
    List<Refugio> findByLocalidadAndActivoTrue(String localidad);

    /**
     * Busca todos los refugios activos ordenados por nombre
     */
    @Query("SELECT r FROM Refugio r WHERE r.activo = true ORDER BY r.nombreRefugio")
    List<Refugio> findAllActivos();

    /**
     * Cuenta las mascotas de un refugio
     */
    @Query("SELECT COUNT(m) FROM Mascota m WHERE m.refugio.idRefugio = :refugioId")
    Long countMascotasByRefugio(@Param("refugioId") Long refugioId);

    /**
     * Cuenta las mascotas disponibles de un refugio
     */
    @Query("SELECT COUNT(m) FROM Mascota m WHERE m.refugio.idRefugio = :refugioId AND m.estadoAdopcion = 'disponible'")
    Long countMascotasDisponiblesByRefugio(@Param("refugioId") Long refugioId);

    /**
     * Busca un refugio por email
     * Este método es útil para vincular el refugio con el usuario
     */
    Optional<Refugio> findByEmail(String email);

    /**
     * Busca un refugio por responsable
     */
    Optional<Refugio> findByResponsable(String responsable);

    /**
     * Busca refugios por nombre (búsqueda parcial)
     */
    @Query("SELECT r FROM Refugio r WHERE LOWER(r.nombreRefugio) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Refugio> buscarPorNombre(@Param("nombre") String nombre);

    /**
     * Verifica si existe un refugio con ese email
     */
    boolean existsByEmail(String email);

    /**
     * Busca refugios con capacidad disponible
     */
    @Query("SELECT r FROM Refugio r WHERE r.activo = true AND " +
            "(r.capacidadMaxima IS NULL OR " +
            "(SELECT COUNT(m) FROM Mascota m WHERE m.refugio.idRefugio = r.idRefugio AND m.estadoAdopcion = 'disponible') < r.capacidadMaxima)")
    List<Refugio> findRefugiosConCapacidad();

    /**
     * Obtiene estadísticas de un refugio
     */
    @Query("SELECT COUNT(m) FROM Mascota m WHERE m.refugio.idRefugio = :refugioId AND m.estadoAdopcion = :estado")
    Long contarMascotasPorEstado(@Param("refugioId") Long refugioId, @Param("estado") String estado);

    /**
     * Busca refugios por teléfono
     */
    Optional<Refugio> findByTelefono(String telefono);
}