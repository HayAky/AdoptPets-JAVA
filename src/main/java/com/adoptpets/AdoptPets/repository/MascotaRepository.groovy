package com.adoptpets.AdoptPets.repository;

import com.adoptpets.AdoptPets.model.Mascota;
import com.adoptpets.AdoptPets.model.Refugio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MascotaRepository extends JpaRepository<Mascota, Long> {
    List<Mascota> findByEstado(String estado);
    List<Mascota> findByRefugio(Refugio refugio);
    List<Mascota> findByEspecieAndEstado(String especie, String estado);

    @Query("SELECT m FROM Mascota m WHERE m.estado = 'Disponible' ORDER BY m.fechaIngreso DESC")
    List<Mascota> findMascotasDisponibles();

    @Query("SELECT COUNT(m) FROM Mascota m WHERE m.refugio.id = :refugioId AND m.estado = :estado")
    Long countByRefugioAndEstado(Long refugioId, String estado);
}