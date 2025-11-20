package com.adoptpets.AdoptPets.config;

import com.adoptpets.AdoptPets.model.Usuario;
import com.adoptpets.AdoptPets.model.Rol;
import com.adoptpets.AdoptPets.repository.RolRepository;
import com.adoptpets.AdoptPets.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;

@Configuration
public class DataInitializer {
    @Bean
    public CommandLineRunner initDatabase(UsuarioRepository repo, RolRepository rolRepo) {
        return args -> {
            if(repo.findByEmail("admin@mail.com").isEmpty()){
                Rol rolAdmin = rolRepo.findByNombreRol("administrador")
                        .orElseThrow();
                Usuario admin = new Usuario();
                admin.setNombre("admin");
                admin.setApellido("admin");
                admin.setCedula("000000");
                admin.setCiudad("Bogota");
                admin.setDireccion("N/A");
                admin.setFechaNacimiento(LocalDate.of(1989, 12, 12));
                admin.setActivo(true);
                admin.setEmail("admin@mail.com");
                admin.setPassword(new BCryptPasswordEncoder().encode("12345678"));
                admin.setRol(rolAdmin);
                repo.save(admin);
                System.out.println("Usuario admin creado con exito");
            }else {
                System.out.println("Usuario admin ya existe");
            }
        };
    }
}
