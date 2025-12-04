package com.adoptpets.AdoptPets.repository;

import com.adoptpets.AdoptPets.model.Seguimiento;
import com.adoptpets.AdoptPets.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface SeguimientoRepository extends JpaRepository<Seguimiento, Long> {
    List<Seguimiento> findByAdoptante(Usuario adoptante);

    @Query("SELECT s FROM Seguimiento s WHERE s.proximoSeguimiento <= :fecha")
    List<Seguimiento> findSeguimientosPendientes(@Param("fecha") LocalDate fecha);

    @Query("SELECT s FROM Seguimiento s WHERE s.adoptante.idUsuario = :usuarioId ORDER BY s.fechaSeguimiento DESC")
    List<Seguimiento> findByAdoptanteId(@Param("usuarioId") Long usuarioId);
}