package com.adoptpets.AdoptPets.repository;

import com.adoptpets.AdoptPets.model.Adopcion;
import com.adoptpets.AdoptPets.model.Usuario;
import com.adoptpets.AdoptPets.model.enums.EstadoAdopcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AdopcionRepository extends JpaRepository<Adopcion, Long>, JpaSpecificationExecutor<Adopcion> {

    long countByEstadoAdopcion(EstadoAdopcion estado);

    List<Adopcion> findByAdoptante(Usuario adoptante);

    List<Adopcion> findByEstadoAdopcion(EstadoAdopcion estado);

    List<Adopcion> findByAdoptanteAndEstadoAdopcion(Usuario adoptante, EstadoAdopcion estado);

    @Query("SELECT a FROM Adopcion a WHERE a.estadoAdopcion = 'PENDIENTE' ORDER BY a.fechaSolicitud DESC")
    List<Adopcion> findAdopcionesPendientes();

    @Query("SELECT COUNT(a) FROM Adopcion a WHERE a.estadoAdopcion = :estado")
    Long countByEstado(@Param("estado") EstadoAdopcion estado);

    @Query("SELECT a FROM Adopcion a WHERE a.mascota.refugio.idRefugio = :refugioId")
    List<Adopcion> findByRefugioId(@Param("refugioId") Long refugioId);

    @Query("SELECT COUNT(a) FROM Adopcion a WHERE a.fechaSolicitud BETWEEN :inicio AND :fin")
    Long countAdopcionesPorFecha(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    @Query("SELECT a FROM Adopcion a WHERE a.adoptante.idUsuario = :usuarioId ORDER BY a.fechaSolicitud DESC")
    List<Adopcion> findByAdoptanteId(@Param("usuarioId") Long usuarioId);
}