package com.adoptpets.AdoptPets.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas
                        .requestMatchers(
                                "/",
                                "/main",
                                "/contactenos",
                                "/contactenos/**",
                                "/register",
                                "/login",
                                "/about",                             
                                "/adoptar",
                                "/css/**",
                                "/js/**",
                                "/img/**"
                        ).permitAll()

                        // Rutas de administrador
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Rutas de adoptante (incluye admin)
                        .requestMatchers("/adoptante/**").hasAnyRole("ADOPTANTE", "ADMIN")

                        // Rutas de refugio
                        .requestMatchers("/refugio/**").hasAnyRole("REFUGIO", "ADMIN")

                        // Perfil accesible para usuarios autenticados
                        .requestMatchers("/perfil").authenticated()

                        // Todas las demás rutas requieren autenticación
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("email")
                        .successHandler((request, response, authentication) -> {
                            // Redireccionar según el rol
                            if (authentication.getAuthorities().stream()
                                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                                response.sendRedirect("/admin/dashboard");
                            } else if (authentication.getAuthorities().stream()
                                    .anyMatch(a -> a.getAuthority().equals("ROLE_REFUGIO"))) {
                                response.sendRedirect("/refugio/dashboard");
                            } else {
                                response.sendRedirect("/adoptante/dashboard");
                            }
                        })
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .invalidSessionUrl("/login?expired")
                        .maximumSessions(1)
                        .expiredUrl("/login?expired")
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/error/403")
                );

        return http.build();
    }

}