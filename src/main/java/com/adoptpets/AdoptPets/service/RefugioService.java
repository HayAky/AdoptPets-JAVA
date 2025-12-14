package com.adoptpets.AdoptPets.service;

import com.adoptpets.AdoptPets.model.Refugio;
import com.adoptpets.AdoptPets.model.Usuario;
import com.adoptpets.AdoptPets.repository.RefugioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RefugioService {

    @Autowired
    private RefugioRepository refugioRepository;

    /**
     * Lista todos los refugios
     */
    public List<Refugio> listarTodos() {
        return refugioRepository.findAll();
    }

    /**
     * Lista solo los refugios activos
     */
    public List<Refugio> listarActivos() {
        return refugioRepository.findAllActivos();
    }

    /**
     * Busca un refugio por ID
     */
    public Optional<Refugio> buscarPorId(Long id) {
        return refugioRepository.findById(id);
    }

    /**
     * Guarda o actualiza un refugio
     */
    public Refugio guardar(Refugio refugio) {
        return refugioRepository.save(refugio);
    }

    /**
     * Elimina un refugio por ID
     */
    public void eliminar(Long id) {
        refugioRepository.deleteById(id);
    }

    /**
     * Busca refugios por localidad
     */
    public List<Refugio> buscarPorLocalidad(String localidad) {
        return refugioRepository.findByLocalidadAndActivoTrue(localidad);
    }

    /**
     * Cuenta el total de mascotas de un refugio
     */
    public Long contarMascotasPorRefugio(Long refugioId) {
        return refugioRepository.countMascotasByRefugio(refugioId);
    }

    /**
     * Cuenta las mascotas disponibles de un refugio
     */
    public Long contarMascotasDisponiblesPorRefugio(Long refugioId) {
        return refugioRepository.countMascotasDisponiblesByRefugio(refugioId);
    }

    /**
     * Busca un refugio por email (vinculado al usuario)
     * Este método es clave para conectar el usuario refugio con su refugio
     */
    public Optional<Refugio> buscarPorEmail(String email) {
        return refugioRepository.findByEmail(email);
    }

    /**
     * Busca un refugio por usuario
     * Utiliza el email del usuario para encontrar el refugio asociado
     */
    public Optional<Refugio> buscarPorUsuario(Usuario usuario) {
        return refugioRepository.findByEmail(usuario.getEmail());
    }

    /**
     * Busca un refugio por responsable
     */
    public Optional<Refugio> buscarPorResponsable(String responsable) {
        return refugioRepository.findByResponsable(responsable);
    }

    /**
     * Busca refugios por nombre (búsqueda parcial)
     */
    public List<Refugio> buscarPorNombre(String nombre) {
        return refugioRepository.buscarPorNombre(nombre);
    }

    /**
     * Verifica si existe un refugio con ese email
     */
    public boolean existePorEmail(String email) {
        return refugioRepository.existsByEmail(email);
    }

    /**
     * Lista refugios con capacidad disponible
     */
    public List<Refugio> listarConCapacidadDisponible() {
        return refugioRepository.findRefugiosConCapacidad();
    }

    /**
     * Cuenta mascotas por estado en un refugio
     */
    public Long contarMascotasPorEstado(Long refugioId, String estado) {
        return refugioRepository.contarMascotasPorEstado(refugioId, estado);
    }

    /**
     * Activa o desactiva un refugio
     */
    public Refugio toggleActivo(Long refugioId) {
        Refugio refugio = refugioRepository.findById(refugioId)
                .orElseThrow(() -> new RuntimeException("Refugio no encontrado"));
        refugio.setActivo(!refugio.getActivo());
        return refugioRepository.save(refugio);
    }

    /**
     * Verifica si un refugio tiene capacidad disponible
     */
    public boolean tieneCapacidadDisponible(Long refugioId) {
        Refugio refugio = refugioRepository.findById(refugioId)
                .orElseThrow(() -> new RuntimeException("Refugio no encontrado"));

        // Si no tiene capacidad máxima definida, siempre tiene capacidad
        if (refugio.getCapacidadMaxima() == null) {
            return true;
        }

        Long mascotasActuales = contarMascotasPorRefugio(refugioId);
        return mascotasActuales < refugio.getCapacidadMaxima();
    }

    /**
     * Obtiene el porcentaje de ocupación del refugio
     */
    public Double obtenerPorcentajeOcupacion(Long refugioId) {
        Refugio refugio = refugioRepository.findById(refugioId)
                .orElseThrow(() -> new RuntimeException("Refugio no encontrado"));

        if (refugio.getCapacidadMaxima() == null || refugio.getCapacidadMaxima() == 0) {
            return 0.0;
        }

        Long mascotasActuales = contarMascotasPorRefugio(refugioId);
        return (mascotasActuales.doubleValue() / refugio.getCapacidadMaxima()) * 100;
    }

    /**
     * Busca refugio por teléfono
     */
    public Optional<Refugio> buscarPorTelefono(String telefono) {
        return refugioRepository.findByTelefono(telefono);
    }

    /**
     * Obtiene estadísticas completas del refugio
     */
    public RefugioEstadisticas obtenerEstadisticas(Long refugioId) {
        RefugioEstadisticas stats = new RefugioEstadisticas();
        stats.setTotalMascotas(contarMascotasPorRefugio(refugioId));
        stats.setMascotasDisponibles(contarMascotasDisponiblesPorRefugio(refugioId));
        stats.setMascotasEnProceso(contarMascotasPorEstado(refugioId, "en_proceso"));
        stats.setMascotasAdoptadas(contarMascotasPorEstado(refugioId, "adoptada"));

        Refugio refugio = buscarPorId(refugioId).orElse(null);
        if (refugio != null) {
            stats.setCapacidadMaxima(refugio.getCapacidadMaxima());
            stats.setPorcentajeOcupacion(obtenerPorcentajeOcupacion(refugioId));
        }

        return stats;
    }

    /**
     * Clase interna para estadísticas del refugio
     */
    public static class RefugioEstadisticas {
        private Long totalMascotas;
        private Long mascotasDisponibles;
        private Long mascotasEnProceso;
        private Long mascotasAdoptadas;
        private Integer capacidadMaxima;
        private Double porcentajeOcupacion;

        // Getters y Setters
        public Long getTotalMascotas() { return totalMascotas; }
        public void setTotalMascotas(Long totalMascotas) { this.totalMascotas = totalMascotas; }

        public Long getMascotasDisponibles() { return mascotasDisponibles; }
        public void setMascotasDisponibles(Long mascotasDisponibles) { this.mascotasDisponibles = mascotasDisponibles; }

        public Long getMascotasEnProceso() { return mascotasEnProceso; }
        public void setMascotasEnProceso(Long mascotasEnProceso) { this.mascotasEnProceso = mascotasEnProceso; }

        public Long getMascotasAdoptadas() { return mascotasAdoptadas; }
        public void setMascotasAdoptadas(Long mascotasAdoptadas) { this.mascotasAdoptadas = mascotasAdoptadas; }

        public Integer getCapacidadMaxima() { return capacidadMaxima; }
        public void setCapacidadMaxima(Integer capacidadMaxima) { this.capacidadMaxima = capacidadMaxima; }

        public Double getPorcentajeOcupacion() { return porcentajeOcupacion; }
        public void setPorcentajeOcupacion(Double porcentajeOcupacion) { this.porcentajeOcupacion = porcentajeOcupacion; }
    }
}