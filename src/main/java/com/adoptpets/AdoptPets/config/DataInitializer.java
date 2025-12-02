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
import java.util.HashSet;
import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initDatabase(UsuarioRepository usuarioRepo, RolRepository rolRepo) {
        return args -> {

            // 1. BUSCAR O CREAR EL ROL "ROLE_ADMIN"
            Rol rolAdmin = rolRepo.findByNombreRol("ROLE_ADMIN").orElseGet(() -> {
                Rol nuevoRol = new Rol();
                nuevoRol.setNombreRol("ROLE_ADMIN");
                nuevoRol.setDescripcion("Usuario con permisos completos del sistema");
                return rolRepo.save(nuevoRol);
            });

            // 2. BUSCAR O CREAR EL ROL "ROLE_ADOPTANTE"
            Rol rolAdoptante = rolRepo.findByNombreRol("ROLE_ADOPTANTE").orElseGet(() -> {
                Rol nuevoRol = new Rol();
                nuevoRol.setNombreRol("ROLE_ADOPTANTE");
                nuevoRol.setDescripcion("Personas interesadas en adoptar mascotas");
                return rolRepo.save(nuevoRol);
            });

            // 3. BUSCAR O CREAR EL ROL "ROLE_REFUGIO"
            Rol rolRefugio = rolRepo.findByNombreRol("ROLE_REFUGIO").orElseGet(() -> {
                Rol nuevoRol = new Rol();
                nuevoRol.setNombreRol("ROLE_REFUGIO");
                nuevoRol.setDescripcion("Organizaciones que cuidan mascotas para adopción");
                return rolRepo.save(nuevoRol);
            });

            // 4. CREAR EL USUARIO ADMIN (Solo si no existe)
            if (usuarioRepo.findByEmail("admin@mail.com").isEmpty()) {
                Usuario admin = new Usuario();
                admin.setNombre("Admin");
                admin.setApellido("Principal");
                admin.setCedula("000000");
                admin.setCiudad("Bogota");
                admin.setDireccion("N/A");
                admin.setFechaNacimiento(LocalDate.of(1990, 1, 1));
                admin.setActivo(true);
                admin.setEmail("admin@mail.com");
                admin.setPassword(new BCryptPasswordEncoder().encode("andrescasti"));

                // Asignar rol ROLE_ADMIN
                Set<Rol> roles = new HashSet<>();
                roles.add(rolAdmin);
                admin.setRoles(roles);

                usuarioRepo.save(admin);
                System.out.println("--> Usuario 'admin@mail.com' creado con rol ROLE_ADMIN.");
            } else {
                System.out.println("--> El usuario admin ya existe.");
            }
        };
    }
}