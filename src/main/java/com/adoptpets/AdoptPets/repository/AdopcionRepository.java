package com.adoptpets.AdoptPets.repository;

import com.adoptpets.AdoptPets.model.Adopcion;
import com.adoptpets.AdoptPets.model.Usuario;
import com.adoptpets.AdoptPets.model.enums.EstadoAdopcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AdopcionRepository extends JpaRepository<Adopcion, Long> {

    // Buscar adopciones por adoptante
    List<Adopcion> findByAdoptante(Usuario adoptante);

    // Buscar por estado
    List<Adopcion> findByEstadoAdopcion(EstadoAdopcion estado);

    // Buscar adopciones pendientes de un usuario
    List<Adopcion> findByAdoptanteAndEstadoAdopcion(Usuario adoptante, EstadoAdopcion estado);

    // Contar adopciones por estado
    Long countByEstadoAdopcion(EstadoAdopcion estado);

    // Adopciones por rango de fechas
    @Query("SELECT a FROM Adopcion a WHERE a.fechaSolicitud BETWEEN :inicio AND :fin")
    List<Adopcion> findByFechaSolicitudBetween(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    // Estadísticas mensuales
    @Query("SELECT MONTH(a.fechaSolicitud), COUNT(a) FROM Adopcion a WHERE YEAR(a.fechaSolicitud) = :year GROUP BY MONTH(a.fechaSolicitud)")
    List<Object[]> countAdopcionesPorMes(@Param("year") int year);

    // Adopciones recientes
    @Query("SELECT a FROM Adopcion a ORDER BY a.fechaSolicitud DESC")
    List<Adopcion> findRecientes();

    // Verificar si una mascota tiene adopciones pendientes
    @Query("SELECT COUNT(a) > 0 FROM Adopcion a WHERE a.mascota.idMascota = :mascotaId AND a.estadoAdopcion = 'PENDIENTE'")
    boolean existeAdopcionPendientePorMascota(@Param("mascotaId") Long mascotaId);
}