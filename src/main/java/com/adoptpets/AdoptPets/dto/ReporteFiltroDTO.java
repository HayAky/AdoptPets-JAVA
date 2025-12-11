package com.adoptpets.AdoptPets.dto;

import com.adoptpets.AdoptPets.model.enums.EstadoAdopcion;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Data
public class ReporteFiltroDTO {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicio;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaFin;

    private EstadoAdopcion estado;

    // Opcional: Filtrar por ID de refugio si el usuario es Admin
    private Long idRefugio;
}