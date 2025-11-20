package com.adoptpets.AdoptPets.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.adoptpets.AdoptPets.model.Rol;
public interface RolRepository extends JpaRepository<Rol,Long>{
    Optional<Rol> findByNombreRol(String rolName);
}
