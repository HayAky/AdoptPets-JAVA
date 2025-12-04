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
    public CommandLineRunner initDatabase(UsuarioRepository usuarioRepo,
                                          RolRepository rolRepo,
                                          BCryptPasswordEncoder passwordEncoder) {
        return args -> {

            System.out.println("=== Iniciando configuración de base de datos ===");

            // 1. BUSCAR O CREAR EL ROL "ROLE_ADMIN"
            Rol rolAdmin = rolRepo.findByNombreRol("ROLE_ADMIN").orElseGet(() -> {
                Rol nuevoRol = new Rol();
                nuevoRol.setNombreRol("ROLE_ADMIN");
                nuevoRol.setDescripcion("Usuario con permisos completos del sistema");
                System.out.println("-> Creando rol ROLE_ADMIN");
                return rolRepo.save(nuevoRol);
            });

            // 2. BUSCAR O CREAR EL ROL "ROLE_ADOPTANTE"
            Rol rolAdoptante = rolRepo.findByNombreRol("ROLE_ADOPTANTE").orElseGet(() -> {
                Rol nuevoRol = new Rol();
                nuevoRol.setNombreRol("ROLE_ADOPTANTE");
                nuevoRol.setDescripcion("Personas interesadas en adoptar mascotas");
                System.out.println("-> Creando rol ROLE_ADOPTANTE");
                return rolRepo.save(nuevoRol);
            });

            // 3. BUSCAR O CREAR EL ROL "ROLE_REFUGIO"
            Rol rolRefugio = rolRepo.findByNombreRol("ROLE_REFUGIO").orElseGet(() -> {
                Rol nuevoRol = new Rol();
                nuevoRol.setNombreRol("ROLE_REFUGIO");
                nuevoRol.setDescripcion("Organizaciones que cuidan mascotas para adopción");
                System.out.println("-> Creando rol ROLE_REFUGIO");
                return rolRepo.save(nuevoRol);
            });

            // 4. CREAR EL USUARIO ADMIN (Solo si no existe)
            if (usuarioRepo.findByEmail("admin@adoptpets.com").isEmpty()) {
                Usuario admin = new Usuario();
                admin.setNombre("Admin");
                admin.setApellido("Sistema");
                admin.setCedula("1000000000");
                admin.setCiudad("Bogotá");
                admin.setDireccion("Oficina Central");
                admin.setTelefono("601-000-0000");
                admin.setFechaNacimiento(LocalDate.of(1990, 1, 1));
                admin.setActivo(true);
                admin.setEmail("admin@adoptpets.com");
                admin.setPassword(passwordEncoder.encode("admin123"));

                // Asignar rol ROLE_ADMIN
                Set<Rol> roles = new HashSet<>();
                roles.add(rolAdmin);
                admin.setRoles(roles);

                usuarioRepo.save(admin);
                System.out.println("===========================================");
                System.out.println("USUARIO ADMINISTRADOR CREADO");
                System.out.println("Email: admin@adoptpets.com");
                System.out.println("Password: admin123");
                System.out.println("===========================================");
            } else {
                System.out.println("-> El usuario admin ya existe.");
            }

            System.out.println("=== Configuración completada ===");
        };
    }
}