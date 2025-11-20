package com.adoptpets.AdoptPets.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.adoptpets.AdoptPets.model.Usuario;

public interface UsuarioRepository  extends JpaRepository<Usuario, Long>{
    Optional<Usuario> findByEmail(String correo);
}