package com.adoptpets.AdoptPets.controller;

import com.adoptpets.AdoptPets.model.Mascota;
import com.adoptpets.AdoptPets.model.Refugio;
import com.adoptpets.AdoptPets.model.Usuario;
import com.adoptpets.AdoptPets.model.Rol;
import com.adoptpets.AdoptPets.model.enums.EstadoAdopcion;
import com.adoptpets.AdoptPets.service.*;
import com.adoptpets.AdoptPets.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private MascotaService mascotaService;

    @Autowired
    private AdopcionService adopcionService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RefugioService refugioService;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Estadísticas generales
        Long totalMascotas = (long) mascotaService.listarTodas().size();
        Long mascotasDisponibles = mascotaService.contarDisponibles();
        Long solicitudesPendientes = adopcionService.contarPorEstado(EstadoAdopcion.pendiente);
        Long adopcionesCompletadas = adopcionService.contarPorEstado(EstadoAdopcion.completada);
        Long totalUsuarios = usuarioService.contarTodos();
        Long totalRefugios = (long) refugioService.listarTodos().size();

        model.addAttribute("totalMascotas", totalMascotas);
        model.addAttribute("mascotasDisponibles", mascotasDisponibles);
        model.addAttribute("solicitudesPendientes", solicitudesPendientes);
        model.addAttribute("adopcionesCompletadas", adopcionesCompletadas);
        model.addAttribute("totalUsuarios", totalUsuarios);
        model.addAttribute("totalRefugios", totalRefugios);

        // Listas recientes
        model.addAttribute("adopcionesPendientes", adopcionService.listarPendientes());
        model.addAttribute("ultimasMascotas", mascotaService.listarDisponibles().stream().limit(5).toList());

        return "admin/dashboard";
    }

    // --- GESTIÓN DE MASCOTAS ---
    @GetMapping("/mascotas")
    public String listarMascotas(@RequestParam(required = false) Long refugio, Model model) {
        if (refugio != null) {
            model.addAttribute("mascotas", mascotaService.buscarPorRefugio(refugio));
        } else {
            model.addAttribute("mascotas", mascotaService.listarTodas());
        }
        return "admin/mascotas/lista";
    }

    @GetMapping("/mascotas/nueva")
    public String formularioNuevaMascota(Model model) {
        model.addAttribute("mascota", new Mascota());
        model.addAttribute("refugios", refugioService.listarActivos());
        return "admin/mascotas/form";
    }

    @GetMapping("/mascotas/editar/{id}")
    public String formularioEditarMascota(@PathVariable Long id, Model model) {
        Mascota mascota = mascotaService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));
        model.addAttribute("mascota", mascota);
        model.addAttribute("refugios", refugioService.listarActivos());
        return "admin/mascotas/form";
    }

    @PostMapping("/mascotas/guardar")
    public String guardarMascota(@ModelAttribute Mascota mascota, RedirectAttributes flash) {
        if (mascota.getEstadoAdopcion() == null || mascota.getEstadoAdopcion().isEmpty()) {
            mascota.setEstadoAdopcion(EstadoAdopcion.valueOf("disponible"));
        }
        mascotaService.guardar(mascota);
        flash.addFlashAttribute("success", "Mascota guardada exitosamente");
        return "redirect:/admin/mascotas";
    }

    @GetMapping("/mascotas/eliminar/{id}")
    public String eliminarMascota(@PathVariable Long id, RedirectAttributes flash) {
        try {
            mascotaService.eliminar(id);
            flash.addFlashAttribute("success", "Mascota eliminada");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "No se puede eliminar la mascota. Puede tener adopciones asociadas.");
        }
        return "redirect:/admin/mascotas";
    }

    // --- GESTIÓN DE ADOPCIONES ---
    @GetMapping("/adopciones")
    public String listarAdopciones(Model model) {
        model.addAttribute("adopciones", adopcionService.listarTodas());
        return "admin/adopciones/lista";
    }

    @GetMapping("/adopciones/pendientes")
    public String adopcionesPendientes(Model model) {
        model.addAttribute("adopciones", adopcionService.listarPendientes());
        return "admin/adopciones/pendientes";
    }

    @PostMapping("/adopciones/aprobar/{id}")
    public String aprobarAdopcion(@PathVariable Long id, RedirectAttributes flash) {
        try {
            adopcionService.aprobarAdopcion(id);
            flash.addFlashAttribute("success", "Adopción aprobada exitosamente");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al aprobar la adopción: " + e.getMessage());
        }
        return "redirect:/admin/adopciones/pendientes";
    }

    @PostMapping("/adopciones/rechazar/{id}")
    public String rechazarAdopcion(@PathVariable Long id,
                                   @RequestParam String motivo,
                                   RedirectAttributes flash) {
        try {
            adopcionService.rechazarAdopcion(id, motivo);
            flash.addFlashAttribute("success", "Adopción rechazada");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al rechazar la adopción: " + e.getMessage());
        }
        return "redirect:/admin/adopciones/pendientes";
    }

    @PostMapping("/adopciones/completar/{id}")
    public String completarAdopcion(@PathVariable Long id, RedirectAttributes flash) {
        try {
            adopcionService.completarAdopcion(id);
            flash.addFlashAttribute("success", "Adopción completada exitosamente");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al completar la adopción: " + e.getMessage());
        }
        return "redirect:/admin/adopciones";
    }

    @GetMapping("/adopciones/detalle/{id}")
    public String verDetalleAdopcion(@PathVariable Long id, Model model) {
        com.adoptpets.AdoptPets.model.Adopcion adopcion = adopcionService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Solicitud de Adopción no encontrada con ID: " + id));
        model.addAttribute("adopcion", adopcion);
        return "admin/adopciones/detalle";
    }

    // --- GESTIÓN DE USUARIOS ADOPTANTES ---
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        java.util.List<Usuario> usuarios = usuarioService.listarTodos();
        model.addAttribute("usuarios", usuarios);

        // 1. Calcular cantidad de activos
        long cantidadActivos = usuarios.stream()
                .filter(Usuario::getActivo)
                .count();
        model.addAttribute("cantidadActivos", cantidadActivos);

        // 2. Calcular cantidad de Adoptantes
        long cantidadAdoptantes = usuarios.stream()
                .filter(u -> u.getRoles().stream()
                        .anyMatch(r -> "ROLE_ADOPTANTE".equals(r.getNombreRol())))
                .count();
        model.addAttribute("cantidadAdoptantes", cantidadAdoptantes);

        // 3. Filtrar la lista de Refugios (usuarios con rol REFUGIO)
        java.util.List<Usuario> listaRefugios = usuarios.stream()
                .filter(u -> u.getRoles().stream()
                        .anyMatch(r -> "ROLE_REFUGIO".equals(r.getNombreRol())))
                .toList();

        model.addAttribute("listaRefugios", listaRefugios);
        model.addAttribute("cantidadRefugios", listaRefugios.size());

        // 4. NUEVO: Obtener todos los refugios para mostrar la información completa
        java.util.List<Refugio> refugios = refugioService.listarTodos();
        model.addAttribute("refugios", refugios);

        Map<String, Refugio> refugioPorEmail = refugios.stream()
                .collect(Collectors.toMap(Refugio::getEmail, r -> r));

        model.addAttribute("refugioPorEmail", refugioPorEmail);

        return "admin/usuarios/lista";
    }

    @GetMapping("/usuarios/editar/{id}")
    public String editarUsuario(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        model.addAttribute("usuario", usuario);
        return "admin/usuarios/form";
    }

    @PostMapping("/usuarios/actualizar")
    public String actualizarUsuario(@ModelAttribute Usuario usuarioActualizado, RedirectAttributes flash) {
        try {
            Usuario usuario = usuarioService.buscarPorId(usuarioActualizado.getIdUsuario())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            usuario.setNombre(usuarioActualizado.getNombre());
            usuario.setApellido(usuarioActualizado.getApellido());
            usuario.setTelefono(usuarioActualizado.getTelefono());
            usuario.setDireccion(usuarioActualizado.getDireccion());
            usuario.setCiudad(usuarioActualizado.getCiudad());
            usuario.setCedula(usuarioActualizado.getCedula());
            usuario.setFechaNacimiento(usuarioActualizado.getFechaNacimiento());
            usuario.setActivo(usuarioActualizado.getActivo());

            usuarioService.guardar(usuario);
            flash.addFlashAttribute("success", "Usuario actualizado exitosamente");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al actualizar usuario: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/usuarios/resetear-password/{id}")
    public String resetearPassword(@PathVariable Long id, RedirectAttributes flash) {
        try {
            Usuario usuario = usuarioService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            usuario.setPassword(passwordEncoder.encode("123456"));
            usuarioService.guardar(usuario);

            flash.addFlashAttribute("success", "Contraseña reseteada a: 123456");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al resetear contraseña: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/usuarios/toggle/{id}")
    public String toggleUsuarioActivo(@PathVariable Long id, RedirectAttributes flash) {
        try {
            Usuario usuario = usuarioService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            usuario.setActivo(!usuario.getActivo());
            usuarioService.guardar(usuario);
            flash.addFlashAttribute("success",
                    "Usuario " + (usuario.getActivo() ? "activado" : "desactivado") + " exitosamente");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al actualizar usuario: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    // --- GESTIÓN DE USUARIOS REFUGIO ---
    @GetMapping("/usuarios/refugio/nuevo")
    public String formularioNuevoUsuarioRefugio(Model model) {
        Usuario usuario = new Usuario();
        usuario.setActivo(true);
        model.addAttribute("usuario", usuario);
        return "refugio-completo-form";
    }

    @GetMapping("/usuarios/refugio/editar/{id}")
    public String formularioEditarUsuarioRefugio(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        model.addAttribute("usuario", usuario);
        return "refugio-completo-form";
    }

    @PostMapping("/usuarios/refugio/guardar")
    public String guardarUsuarioRefugio(@ModelAttribute Usuario usuario,
                                        @RequestParam(required = false) String password,
                                        RedirectAttributes flash) {
        try {
            // Si es nuevo usuario
            if (usuario.getIdUsuario() == null) {
                // Validar email único
                if (usuarioService.existeEmail(usuario.getEmail())) {
                    flash.addFlashAttribute("error", "El email ya está registrado");
                    return "redirect:/admin/usuarios";
                }

                // Validar contraseña
                if (password == null || password.length() < 6) {
                    flash.addFlashAttribute("error", "La contraseña debe tener al menos 6 caracteres");
                    return "redirect:/admin/usuarios";
                }

                // Encriptar contraseña
                usuario.setPassword(passwordEncoder.encode(password));

                // Asignar rol REFUGIO
                Rol rolRefugio = rolRepository.findByNombreRol("ROLE_REFUGIO")
                        .orElseThrow(() -> new RuntimeException("Rol REFUGIO no encontrado"));
                Set<Rol> roles = new HashSet<>();
                roles.add(rolRefugio);
                usuario.setRoles(roles);
            } else {
                // Si está editando, mantener la contraseña anterior si no se proporciona una nueva
                Usuario usuarioExistente = usuarioService.buscarPorId(usuario.getIdUsuario())
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                if (password != null && !password.isEmpty() && password.length() >= 6) {
                    usuario.setPassword(passwordEncoder.encode(password));
                } else {
                    usuario.setPassword(usuarioExistente.getPassword());
                }
                usuario.setRoles(usuarioExistente.getRoles());
            }

            usuarioService.guardar(usuario);
            flash.addFlashAttribute("success", "Usuario refugio guardado exitosamente");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al guardar usuario: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    // --- GESTIÓN DE REFUGIOS ---
    @GetMapping("/refugios")
    public String listarRefugios(Model model) {
        model.addAttribute("refugios", refugioService.listarTodos());
        return "admin/refugios/lista";
    }

    @GetMapping("/refugios/nuevo")
    public String formularioNuevoRefugio(Model model) {
        Refugio refugio = new Refugio();
        refugio.setActivo(true);
        model.addAttribute("refugio", refugio);
        return "admin/refugios/form";
    }

    @GetMapping("/refugios/editar/{id}")
    public String editarRefugio(@PathVariable Long id, Model model) {
        Refugio refugio = refugioService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Refugio no encontrado"));
        model.addAttribute("refugio", refugio);
        return "admin/refugios/form";
    }

    @PostMapping("/refugios/guardar")
    public String guardarRefugio(@ModelAttribute Refugio refugio, RedirectAttributes flash) {
        try {
            refugioService.guardar(refugio);
            flash.addFlashAttribute("success", "Refugio guardado exitosamente");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al guardar refugio: " + e.getMessage());
        }
        return "redirect:/admin/refugios";
    }

    @GetMapping("/refugios/eliminar/{id}")
    public String eliminarRefugio(@PathVariable Long id, RedirectAttributes flash) {
        try {
            refugioService.eliminar(id);
            flash.addFlashAttribute("success", "Refugio eliminado");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "No se puede eliminar el refugio. Puede tener mascotas asociadas.");
        }
        return "redirect:/admin/refugios";
    }

    @PostMapping("/refugios/toggle/{id}")
    public String toggleRefugioActivo(@PathVariable Long id, RedirectAttributes flash) {
        try {
            Refugio refugio = refugioService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Refugio no encontrado"));
            refugio.setActivo(!refugio.getActivo());
            refugioService.guardar(refugio);
            flash.addFlashAttribute("success",
                    "Refugio " + (refugio.getActivo() ? "activado" : "desactivado") + " exitosamente");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al actualizar refugio: " + e.getMessage());
        }
        return "redirect:/admin/refugios";
    }

    @GetMapping("/usuarios/refugio/crear-completo")
    public String formularioCrearRefugioCompleto(Model model) {
        Usuario usuario = new Usuario();
        usuario.setActivo(true);

        Refugio refugio = new Refugio();
        refugio.setActivo(true);

        model.addAttribute("usuario", usuario);
        model.addAttribute("refugio", refugio);

        return "admin/usuarios/refugio-completo-form";
    }

    /**
     * Guarda tanto el usuario como el refugio
     */
    @PostMapping("/usuarios/refugio/guardar-completo")
    public String guardarRefugioCompleto(
            @ModelAttribute("usuario") Usuario usuario,
            @ModelAttribute("refugio") Refugio refugio,
            @RequestParam(required = false) String password,
            RedirectAttributes flash) {
        try {
            // Validaciones
            if (usuario.getEmail() == null || usuario.getEmail().isEmpty()) {
                flash.addFlashAttribute("error", "El email es obligatorio");
                return "redirect:/admin/usuarios";
            }


            if (usuarioService.existeEmail(usuario.getEmail())) {
                flash.addFlashAttribute("error", "El email ya está registrado");
                return "redirect:/admin/usuarios";
            }

            if (usuario.getCedula() != null && usuarioService.existeCedula(usuario.getCedula())) {
                flash.addFlashAttribute("error", "La cédula ya está registrada");
                return "redirect:/admin/usuarios";
            }

            if (refugioService.existePorEmail(usuario.getEmail())) {
                flash.addFlashAttribute("error", "Ya existe un refugio con ese email");
                return "redirect:/admin/usuarios";
            }

            if (password == null || password.length() < 6) {
                flash.addFlashAttribute("error", "La contraseña debe tener al menos 6 caracteres");
                return "redirect:/admin/usuarios";
            }

            // 1. Crear el usuario
            usuario.setPassword(passwordEncoder.encode(password));
            Rol rolRefugio = rolRepository.findByNombreRol("ROLE_REFUGIO")
                    .orElseThrow(() -> new RuntimeException("Rol REFUGIO no encontrado"));
            Set<Rol> roles = new HashSet<>();
            roles.add(rolRefugio);
            usuario.setRoles(roles);
            usuario.setActivo(true);

            Usuario usuarioGuardado = usuarioService.guardar(usuario);

            // 2. Crear el refugio vinculado por email
            refugio.setEmail(usuarioGuardado.getEmail());
            refugio.setActivo(true);

            // Si no se especificó responsable, usar el nombre del usuario
            if (refugio.getResponsable() == null || refugio.getResponsable().isEmpty()) {
                refugio.setResponsable(usuario.getNombre() + " " + usuario.getApellido());
            }

            // Si no se especificó teléfono del refugio, usar el del usuario
            if (refugio.getTelefono() == null || refugio.getTelefono().isEmpty()) {
                refugio.setTelefono(usuario.getTelefono());
            }

            refugioService.guardar(refugio);

            flash.addFlashAttribute("success",
                    "Refugio y usuario creados exitosamente. Email: " + usuario.getEmail());

        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al crear refugio: " + e.getMessage());
        }

        return "redirect:/admin/usuarios";
    }

    /**
     * Formulario para editar un refugio existente
     */
    @GetMapping("/usuarios/refugio/editar-completo/{id}")
    public String formularioEditarRefugioCompleto(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Buscar el refugio por email
        Refugio refugio = refugioService.buscarPorEmail(usuario.getEmail())
                .orElse(new Refugio());

        model.addAttribute("usuario", usuario);
        model.addAttribute("refugio", refugio);

        return "admin/usuarios/refugio-completo-form";
    }

    /**
     * Actualiza tanto el usuario como el refugio
     */
    @PostMapping("/usuarios/refugio/actualizar-completo")
    public String actualizarRefugioCompleto(
            @ModelAttribute("usuario") Usuario usuarioActualizado,
            @ModelAttribute("refugio") Refugio refugioActualizado,
            @RequestParam(required = false) String password,
            RedirectAttributes flash) {
        try {
            // 1. Actualizar usuario
            Usuario usuario = usuarioService.buscarPorId(usuarioActualizado.getIdUsuario())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            usuario.setNombre(usuarioActualizado.getNombre());
            usuario.setApellido(usuarioActualizado.getApellido());
            usuario.setTelefono(usuarioActualizado.getTelefono());
            usuario.setDireccion(usuarioActualizado.getDireccion());
            usuario.setCiudad(usuarioActualizado.getCiudad());
            usuario.setCedula(usuarioActualizado.getCedula());
            usuario.setFechaNacimiento(usuarioActualizado.getFechaNacimiento());
            usuario.setActivo(usuarioActualizado.getActivo());

            // Actualizar contraseña si se proporcionó
            if (password != null && !password.isEmpty() && password.length() >= 6) {
                usuario.setPassword(passwordEncoder.encode(password));
            }

            usuarioService.guardar(usuario);

            // 2. Actualizar o crear refugio
            Refugio refugio = refugioService.buscarPorEmail(usuario.getEmail())
                    .orElse(new Refugio());

            refugio.setNombreRefugio(refugioActualizado.getNombreRefugio());
            refugio.setDireccion(refugioActualizado.getDireccion());
            refugio.setTelefono(refugioActualizado.getTelefono());
            refugio.setEmail(usuario.getEmail()); // Mantener sincronizado
            refugio.setResponsable(refugioActualizado.getResponsable());
            refugio.setLocalidad(refugioActualizado.getLocalidad());
            refugio.setCapacidadMaxima(refugioActualizado.getCapacidadMaxima());
            refugio.setDescripcion(refugioActualizado.getDescripcion());
            refugio.setActivo(refugioActualizado.getActivo());

            refugioService.guardar(refugio);

            flash.addFlashAttribute("success", "Usuario y refugio actualizados exitosamente");

        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al actualizar: " + e.getMessage());
        }

        return "redirect:/admin/usuarios";
    }

    /**
     * Elimina tanto el usuario como el refugio asociado
     */
    @GetMapping("/usuarios/refugio/eliminar-completo/{id}")
    public String eliminarRefugioCompleto(@PathVariable Long id, RedirectAttributes flash) {
        try {
            Usuario usuario = usuarioService.buscarPorId(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Buscar y eliminar el refugio si existe
            refugioService.buscarPorEmail(usuario.getEmail()).ifPresent(refugio -> {
                try {
                    refugioService.eliminar(refugio.getIdRefugio());
                } catch (Exception e) {
                    throw new RuntimeException("No se puede eliminar el refugio. Tiene mascotas asociadas.");
                }
            });

            // Eliminar el usuario
            usuarioService.eliminar(id);

            flash.addFlashAttribute("success", "Usuario refugio y refugio eliminados exitosamente");

        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al eliminar: " + e.getMessage());
        }

        return "redirect:/admin/usuarios";
    }
}