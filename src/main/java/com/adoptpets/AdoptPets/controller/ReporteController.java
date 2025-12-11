package com.adoptpets.AdoptPets.controller;

import com.adoptpets.AdoptPets.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin/reportes")

public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @GetMapping
    public String mostrarReportes(Model model) {
        model.addAttribute("fechaInicio", LocalDate.now().minusMonths(1));
        model.addAttribute("fechaFin", LocalDate.now());

        model.addAttribute("total", 0);
        model.addAttribute("disponibles", 0);
        model.addAttribute("adoptadas", 0);

        return "admin/reportes/reportes";
    }

    // ===== REPORTES PDF =====

    @GetMapping("/mascotas/pdf")
    public ResponseEntity<byte[]> reporteMascotasPdf(
            @RequestParam(required = false) String estado) {

        byte[] pdfBytes = reporteService.generarReporteMascotasPdf(estado);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "reporte_mascotas.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @GetMapping("/adopciones/pdf")
    public ResponseEntity<byte[]> reporteAdopcionesPdf(
            @RequestParam(required = false) LocalDate fechaInicio,
            @RequestParam(required = false) LocalDate fechaFin) {

        byte[] pdfBytes = reporteService.generarReporteAdopcionesPdf(fechaInicio, fechaFin);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "reporte_adopciones.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @GetMapping("/refugios/pdf")
    public ResponseEntity<byte[]> reporteRefugiosPdf() {
        byte[] pdfBytes = reporteService.generarReporteRefugiosPdf();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "reporte_refugios.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @GetMapping("/usuarios/pdf")
    public ResponseEntity<byte[]> reporteUsuariosPdf() {
        byte[] pdfBytes = reporteService.generarReporteUsuariosPdf();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "reporte_usuarios.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    // ===== REPORTES EXCEL =====

    @GetMapping("/mascotas/excel")
    public ResponseEntity<byte[]> reporteMascotasExcel(
            @RequestParam(required = false) String estado) {

        byte[] excelBytes = reporteService.generarReporteMascotasExcel(estado);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "reporte_mascotas.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);
    }

    @GetMapping("/adopciones/excel")
    public ResponseEntity<byte[]> reporteAdopcionesExcel(
            @RequestParam(required = false) LocalDate fechaInicio,
            @RequestParam(required = false) LocalDate fechaFin) {

        byte[] excelBytes = reporteService.generarReporteAdopcionesExcel(fechaInicio, fechaFin);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "reporte_adopciones.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);
    }

    @GetMapping("/refugios/excel")
    public ResponseEntity<byte[]> reporteRefugiosExcel() {
        byte[] excelBytes = reporteService.generarReporteRefugiosExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "reporte_refugios.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);
    }

    @GetMapping("/usuarios/excel")
    public ResponseEntity<byte[]> reporteUsuariosExcel() {
        byte[] excelBytes = reporteService.generarReporteUsuariosExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "reporte_usuarios.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);
    }

    @GetMapping("/estadisticas/excel")
    public ResponseEntity<byte[]> reporteEstadisticasExcel(
            @RequestParam(required = false) LocalDate fechaInicio,
            @RequestParam(required = false) LocalDate fechaFin) {

        byte[] excelBytes = reporteService.generarReporteEstadisticasExcel(fechaInicio, fechaFin);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "reporte_estadisticas.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);
    }
}