package com.adoptpets.AdoptPets.repository;

import com.adoptpets.AdoptPets.model.Mascota;
import com.adoptpets.AdoptPets.model.Refugio;
import com.adoptpets.AdoptPets.model.enums.EstadoAdopcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Long>, JpaSpecificationExecutor<Mascota> {

    List<Mascota> findByEstadoAdopcion(EstadoAdopcion estado);

    List<Mascota> findByRefugio(Refugio refugio);

    List<Mascota> findByEspecieAndEstadoAdopcion(String especie, EstadoAdopcion estado);

    long countByEstadoAdopcion(EstadoAdopcion estado);

    @Query("SELECT m FROM Mascota m WHERE m.estadoAdopcion = com.adoptpets.AdoptPets.model.enums.EstadoAdopcion.disponible ORDER BY m.fechaIngreso DESC")
    List<Mascota> findMascotasDisponibles();

    @Query("SELECT COUNT(m) FROM Mascota m WHERE m.refugio.idRefugio = :refugioId AND m.estadoAdopcion = :estado")
    Long countByRefugioAndEstado(@Param("refugioId") Long refugioId,
                                 @Param("estado") EstadoAdopcion estado);

    @Query("SELECT m FROM Mascota m WHERE m.refugio.idRefugio = :refugioId ORDER BY m.fechaIngreso DESC")
    List<Mascota> findByRefugioId(@Param("refugioId") Long refugioId);

    @Query("SELECT COUNT(m) FROM Mascota m WHERE m.estadoAdopcion = com.adoptpets.AdoptPets.model.enums.EstadoAdopcion.disponible")
    Long countDisponibles();

    @Query("SELECT m FROM Mascota m WHERE LOWER(m.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
            "OR LOWER(m.especie) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
            "OR LOWER(m.raza) LIKE LOWER(CONCAT('%', :busqueda, '%'))")
    List<Mascota> buscarMascotas(@Param("busqueda") String busqueda);
}