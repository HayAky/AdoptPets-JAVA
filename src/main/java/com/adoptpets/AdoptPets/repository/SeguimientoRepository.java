package com.adoptpets.AdoptPets.repository;

import com.adoptpets.AdoptPets.model.Seguimiento;
import com.adoptpets.AdoptPets.model.Usuario;
import com.adoptpets.AdoptPets.model.Mascota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SeguimientoRepository extends JpaRepository<Seguimiento, Long> {

    // Seguimientos por adoptante
    List<Seguimiento> findByAdoptanteOrderByFechaSeguimientoDesc(Usuario adoptante);

    // Seguimientos por mascota
    List<Seguimiento> findByMascotaOrderByFechaSeguimientoDesc(Mascota mascota);

    // Próximos seguimientos
    @Query("SELECT s FROM Seguimiento s WHERE s.proximoSeguimiento <= :fecha ORDER BY s.proximoSeguimiento ASC")
    List<Seguimiento> findProximosSeguimientos(LocalDate fecha);

    // Seguimientos pendientes (con fecha próxima pero no realizados)
    @Query("SELECT s FROM Seguimiento s WHERE s.proximoSeguimiento < CURRENT_DATE")
    List<Seguimiento> findSeguimientosPendientes();
}