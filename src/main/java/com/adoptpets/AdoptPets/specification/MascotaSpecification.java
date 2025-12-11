package com.adoptpets.AdoptPets.specification;

import com.adoptpets.AdoptPets.model.Mascota;
import com.adoptpets.AdoptPets.model.enums.EstadoAdopcion;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class MascotaSpecification {

    public static Specification<Mascota> conFiltros(
            String especie,
            String sexo,
            String tamano,
            String raza,
            Integer edadMin,
            Integer edadMax,
            Boolean vacunado,
            Boolean esterilizado,
            Boolean microchip,
            Long refugioId,
            String busqueda
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtro por estado: solo disponibles por defecto
            predicates.add(criteriaBuilder.equal(
                    root.get("estadoAdopcion"),
                    EstadoAdopcion.disponible
            ));

            // Filtro por especie
            if (especie != null && !especie.isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("especie")),
                        especie.toLowerCase()
                ));
            }

            // Filtro por sexo
            if (sexo != null && !sexo.isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("sexo")),
                        sexo.toLowerCase()
                ));
            }

            // Filtro por tamaño
            if (tamano != null && !tamano.isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("tamano")),
                        tamano.toLowerCase()
                ));
            }

            // Filtro por raza
            if (raza != null && !raza.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("raza")),
                        "%" + raza.toLowerCase() + "%"
                ));
            }

            // Filtro por edad mínima
            if (edadMin != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("edadAproximada"),
                        edadMin
                ));
            }

            // Filtro por edad máxima
            if (edadMax != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("edadAproximada"),
                        edadMax
                ));
            }

            // Filtro por vacunación
            if (vacunado != null && vacunado) {
                predicates.add(criteriaBuilder.isTrue(root.get("vacunado")));
            }

            // Filtro por esterilización
            if (esterilizado != null && esterilizado) {
                predicates.add(criteriaBuilder.isTrue(root.get("esterilizado")));
            }

            // Filtro por microchip
            if (microchip != null && microchip) {
                predicates.add(criteriaBuilder.isTrue(root.get("microchip")));
            }

            // Filtro por refugio
            if (refugioId != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("refugio").get("idRefugio"),
                        refugioId
                ));
            }

            // Búsqueda por texto (nombre, especie o raza)
            if (busqueda != null && !busqueda.isEmpty()) {
                String searchPattern = "%" + busqueda.toLowerCase() + "%";
                Predicate nombreMatch = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("nombre")),
                        searchPattern
                );
                Predicate especieMatch = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("especie")),
                        searchPattern
                );
                Predicate razaMatch = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("raza")),
                        searchPattern
                );

                predicates.add(criteriaBuilder.or(nombreMatch, especieMatch, razaMatch));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}