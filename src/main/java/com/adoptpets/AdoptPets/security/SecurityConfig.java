package com.adoptpets.AdoptPets.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider; // <-- CAMBIO
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

    private final UserDetailsServiceImpl userDetailsService; // <-- CAMBIO

    // ===== CONSTRUCTOR =====
    public SecurityConfig(UserDetailsServiceImpl userDetailsService) { // <-- CAMBIO
        this.userDetailsService = userDetailsService;
    }

    // ===== PASSWORD ENCODER =====
    @Bean
    public BCryptPasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    // ===== AUTHENTICATION PROVIDER =====
    @Bean
    public DaoAuthenticationProvider authenticationProvider() { // <-- CAMBIO
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService); // <-- CAMBIO
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // ===== FILTER CHAIN =====
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // REGISTRAR PROVIDER
                .authenticationProvider(authenticationProvider()) // <-- CAMBIO

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/main",
                                "/contactenos",
                                "/contactenos/**",
                                "/refugios",
                                "/register",
                                "/login",
                                "/about",
                                "/adoptar",
                                "/css/**",
                                "/js/**",
                                "/img/**"
                        ).permitAll()

                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/adoptante/**").hasAnyRole("ADOPTANTE", "ADMIN")
                        .requestMatchers("/refugio/**").hasAnyRole("REFUGIO", "ADMIN")
                        .requestMatchers("/perfil").authenticated()
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("email")
                        .successHandler((request, response, authentication) -> {

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
                        .maximumSessions(1)
                        .expiredUrl("/login?expired")
                )

                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/error/403")
                );

        return http.build();
    }
}
