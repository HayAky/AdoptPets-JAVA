package com.adoptpets.AdoptPets.service;

import com.adoptpets.AdoptPets.model.*;
import com.adoptpets.AdoptPets.model.enums.EstadoAdopcion;
import com.adoptpets.AdoptPets.repository.*;

// --- IMPORTS ESPECÍFICOS PARA PDF (OpenPDF) ---
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell; // Usamos PdfPCell, no "Cell" genérico
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
// "Font" de PDF lo usaremos tal cual, así que lo importamos
import com.lowagie.text.Font;

// --- IMPORTS ESPECÍFICOS PARA EXCEL (Apache POI) ---
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
// IMPORTANTE: Importamos explícitamente la Celda de Excel para que gane sobre la de PDF
import org.apache.poi.ss.usermodel.Cell;

// --- OTROS IMPORTS ---
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ReporteService {

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private AdopcionRepository adopcionRepository;

    @Autowired
    private RefugioRepository refugioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ==================== REPORTES PDF ====================

    public byte[] generarReporteMascotasPdf(String estado) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, baos);
            document.open();

            // Título
            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD, new Color(34, 197, 94));
            Paragraph title = new Paragraph("Reporte de Mascotas - AdoptPets", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Fecha
            Font dateFont = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.GRAY);
            Paragraph date = new Paragraph("Generado: " + LocalDate.now().format(formatter), dateFont);
            date.setAlignment(Element.ALIGN_RIGHT);
            document.add(date);
            document.add(new Paragraph(" "));

            // Filtrar mascotas
            List<Mascota> mascotas;
            if (estado != null && !estado.isEmpty()) {
                try {
                    // Convertimos el String del Controller al Enum del Modelo
                    EstadoAdopcion estadoEnum = EstadoAdopcion.valueOf(estado);
                    mascotas = mascotaRepository.findByEstadoAdopcion(estadoEnum);
                } catch (IllegalArgumentException e) {
                    mascotas = mascotaRepository.findAll();
                }
            } else {
                mascotas = mascotaRepository.findAll();
            }

            // Tabla
            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);

            // Headers
            Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
            String[] headers = {"ID", "Nombre", "Especie", "Raza", "Edad", "Sexo", "Refugio", "Estado"};

            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(new Color(34, 197, 94));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(8);
                table.addCell(cell);
            }

            // Datos
            Font dataFont = new Font(Font.HELVETICA, 9);
            for (Mascota m : mascotas) {
                table.addCell(new Phrase(m.getIdMascota().toString(), dataFont));
                table.addCell(new Phrase(m.getNombre(), dataFont));
                table.addCell(new Phrase(m.getEspecie(), dataFont));
                table.addCell(new Phrase(m.getRaza() != null ? m.getRaza() : "-", dataFont));
                table.addCell(new Phrase(m.getEdadAproximada() != null ? m.getEdadAproximada() + " años" : "-", dataFont));
                table.addCell(new Phrase(m.getSexo(), dataFont));
                table.addCell(new Phrase(m.getRefugio() != null ? m.getRefugio().getNombreRefugio() : "-", dataFont));
                table.addCell(new Phrase(m.getEstadoAdopcion().toString(), dataFont));
            }

            document.add(table);

            // Resumen
            document.add(new Paragraph(" "));
            Font summaryFont = new Font(Font.HELVETICA, 12, Font.BOLD);
            Paragraph summary = new Paragraph("Total de mascotas: " + mascotas.size(), summaryFont);
            document.add(summary);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF de mascotas", e);
        }
    }

    public byte[] generarReporteAdopcionesPdf(LocalDate fechaInicio, LocalDate fechaFin) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, baos);
            document.open();

            // Título
            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD, new Color(34, 197, 94));
            Paragraph title = new Paragraph("Reporte de Adopciones - AdoptPets", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Rango de fechas
            if (fechaInicio != null && fechaFin != null) {
                Font dateFont = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.GRAY);
                Paragraph range = new Paragraph("Período: " + fechaInicio.format(formatter) + " - " + fechaFin.format(formatter), dateFont);
                range.setAlignment(Element.ALIGN_CENTER);
                document.add(range);
            }
            document.add(new Paragraph(" "));

            // Filtrar adopciones
            List<Adopcion> adopciones = adopcionRepository.findAll();
            if (fechaInicio != null && fechaFin != null) {
                adopciones = adopciones.stream()
                        .filter(a -> !a.getFechaSolicitud().isBefore(fechaInicio) &&
                                !a.getFechaSolicitud().isAfter(fechaFin))
                        .collect(Collectors.toList());
            }

            // Tabla
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);

            // Headers
            Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
            String[] headers = {"ID", "Adoptante", "Mascota", "Refugio", "Fecha Solicitud", "Estado", "Fecha Aprobación"};

            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(new Color(34, 197, 94));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(8);
                table.addCell(cell);
            }

            // Datos
            Font dataFont = new Font(Font.HELVETICA, 9);
            for (Adopcion a : adopciones) {
                table.addCell(new Phrase(a.getIdAdopcion().toString(), dataFont));
                table.addCell(new Phrase(a.getAdoptante().getNombre() + " " + a.getAdoptante().getApellido(), dataFont));
                table.addCell(new Phrase(a.getMascota().getNombre(), dataFont));
                table.addCell(new Phrase(a.getMascota().getRefugio() != null ? a.getMascota().getRefugio().getNombreRefugio() : "-", dataFont));
                table.addCell(new Phrase(a.getFechaSolicitud().format(formatter), dataFont));
                table.addCell(new Phrase(a.getEstadoAdopcion().toString(), dataFont));
                table.addCell(new Phrase(a.getFechaAprobacion() != null ? a.getFechaAprobacion().format(formatter) : "-", dataFont));
            }

            document.add(table);

            // Estadísticas
            document.add(new Paragraph(" "));
            Font summaryFont = new Font(Font.HELVETICA, 11, Font.BOLD);
            document.add(new Paragraph("ESTADÍSTICAS", summaryFont));
            document.add(new Paragraph("Total de adopciones: " + adopciones.size()));
            document.add(new Paragraph("Pendientes: " + adopciones.stream().filter(a -> a.getEstadoAdopcion() == EstadoAdopcion.pendiente).count()));
            document.add(new Paragraph("Aprobadas: " + adopciones.stream().filter(a -> a.getEstadoAdopcion() == EstadoAdopcion.aprobada).count()));
            document.add(new Paragraph("Completadas: " + adopciones.stream().filter(a -> a.getEstadoAdopcion() == EstadoAdopcion.completada).count()));
            document.add(new Paragraph("Rechazadas: " + adopciones.stream().filter(a -> a.getEstadoAdopcion() == EstadoAdopcion.rechazada).count()));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF de adopciones", e);
        }
    }

    public byte[] generarReporteRefugiosPdf() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD, new Color(34, 197, 94));
            Paragraph title = new Paragraph("Reporte de Refugios - AdoptPets", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            List<Refugio> refugios = refugioRepository.findAll();

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);

            Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
            String[] headers = {"ID", "Nombre", "Localidad", "Responsable", "Teléfono", "Estado"};

            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(new Color(34, 197, 94));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(8);
                table.addCell(cell);
            }

            Font dataFont = new Font(Font.HELVETICA, 9);
            for (Refugio r : refugios) {
                table.addCell(new Phrase(r.getIdRefugio().toString(), dataFont));
                table.addCell(new Phrase(r.getNombreRefugio(), dataFont));
                table.addCell(new Phrase(r.getLocalidad() != null ? r.getLocalidad() : "-", dataFont));
                table.addCell(new Phrase(r.getResponsable() != null ? r.getResponsable() : "-", dataFont));
                table.addCell(new Phrase(r.getTelefono() != null ? r.getTelefono() : "-", dataFont));
                table.addCell(new Phrase(r.getActivo() ? "Activo" : "Inactivo", dataFont));
            }

            document.add(table);

            document.add(new Paragraph(" "));
            Font summaryFont = new Font(Font.HELVETICA, 12, Font.BOLD);
            document.add(new Paragraph("Total de refugios: " + refugios.size(), summaryFont));
            document.add(new Paragraph("Activos: " + refugios.stream().filter(Refugio::getActivo).count()));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF de refugios", e);
        }
    }

    public byte[] generarReporteUsuariosPdf() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD, new Color(34, 197, 94));
            Paragraph title = new Paragraph("Reporte de Usuarios - AdoptPets", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            List<Usuario> usuarios = usuarioRepository.findAll();

            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);

            Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);
            String[] headers = {"ID", "Nombre", "Email", "Teléfono", "Ciudad", "Roles", "Estado"};

            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(new Color(34, 197, 94));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(8);
                table.addCell(cell);
            }

            Font dataFont = new Font(Font.HELVETICA, 9);
            for (Usuario u : usuarios) {
                table.addCell(new Phrase(u.getIdUsuario().toString(), dataFont));
                table.addCell(new Phrase(u.getNombre() + " " + u.getApellido(), dataFont));
                table.addCell(new Phrase(u.getEmail(), dataFont));
                table.addCell(new Phrase(u.getTelefono() != null ? u.getTelefono() : "-", dataFont));
                table.addCell(new Phrase(u.getCiudad() != null ? u.getCiudad() : "-", dataFont));

                String roles = u.getRoles().stream()
                        .map(r -> r.getNombreRol().replace("ROLE_", ""))
                        .collect(Collectors.joining(", "));
                table.addCell(new Phrase(roles, dataFont));

                table.addCell(new Phrase(u.getActivo() ? "Activo" : "Inactivo", dataFont));
            }

            document.add(table);

            document.add(new Paragraph(" "));
            Font summaryFont = new Font(Font.HELVETICA, 12, Font.BOLD);
            document.add(new Paragraph("Total de usuarios: " + usuarios.size(), summaryFont));
            document.add(new Paragraph("Activos: " + usuarios.stream().filter(Usuario::getActivo).count()));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF de usuarios", e);
        }
    }

    // ==================== REPORTES EXCEL ====================

    public byte[] generarReporteMascotasExcel(String estado) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            XSSFSheet sheet = workbook.createSheet("Mascotas");

            // Estilos
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            // Headers
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Nombre", "Especie", "Raza", "Edad", "Sexo", "Tamaño", "Refugio", "Estado", "Fecha Ingreso"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Datos
            List<Mascota> mascotas;
            if (estado != null && !estado.isEmpty()) {
                try {
                    EstadoAdopcion estadoEnum = EstadoAdopcion.valueOf(estado);
                    mascotas = mascotaRepository.findByEstadoAdopcion(estadoEnum);
                } catch (IllegalArgumentException e) {
                    mascotas = mascotaRepository.findAll();
                }
            } else {
                mascotas = mascotaRepository.findAll();
            }

            int rowNum = 1;
            for (Mascota m : mascotas) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(m.getIdMascota());
                row.createCell(1).setCellValue(m.getNombre());
                row.createCell(2).setCellValue(m.getEspecie());
                row.createCell(3).setCellValue(m.getRaza() != null ? m.getRaza() : "-");
                row.createCell(4).setCellValue(m.getEdadAproximada() != null ? m.getEdadAproximada() : 0);
                row.createCell(5).setCellValue(m.getSexo());
                row.createCell(6).setCellValue(m.getTamano());
                row.createCell(7).setCellValue(m.getRefugio() != null ? m.getRefugio().getNombreRefugio() : "-");
                row.createCell(8).setCellValue(m.getEstadoAdopcion().toString());
                row.createCell(9).setCellValue(m.getFechaIngreso() != null ? m.getFechaIngreso().format(formatter) : "-");

                for (int i = 0; i < 10; i++) {
                    row.getCell(i).setCellStyle(dataStyle);
                }
            }

            // Autosize
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando Excel de mascotas", e);
        }
    }

    public byte[] generarReporteAdopcionesExcel(LocalDate fechaInicio, LocalDate fechaFin) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            XSSFSheet sheet = workbook.createSheet("Adopciones");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Adoptante", "Email", "Mascota", "Refugio", "Fecha Solicitud", "Estado", "Fecha Aprobación"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            List<Adopcion> adopciones = adopcionRepository.findAll();
            if (fechaInicio != null && fechaFin != null) {
                adopciones = adopciones.stream()
                        .filter(a -> !a.getFechaSolicitud().isBefore(fechaInicio) &&
                                !a.getFechaSolicitud().isAfter(fechaFin))
                        .collect(Collectors.toList());
            }

            int rowNum = 1;
            for (Adopcion a : adopciones) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(a.getIdAdopcion());
                row.createCell(1).setCellValue(a.getAdoptante().getNombre() + " " + a.getAdoptante().getApellido());
                row.createCell(2).setCellValue(a.getAdoptante().getEmail());
                row.createCell(3).setCellValue(a.getMascota().getNombre());
                row.createCell(4).setCellValue(a.getMascota().getRefugio() != null ? a.getMascota().getRefugio().getNombreRefugio() : "-");
                row.createCell(5).setCellValue(a.getFechaSolicitud().format(formatter));
                row.createCell(6).setCellValue(a.getEstadoAdopcion().toString());
                row.createCell(7).setCellValue(a.getFechaAprobacion() != null ? a.getFechaAprobacion().format(formatter) : "-");

                for (int i = 0; i < 8; i++) {
                    row.getCell(i).setCellStyle(dataStyle);
                }
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando Excel de adopciones", e);
        }
    }

    public byte[] generarReporteRefugiosExcel() {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            XSSFSheet sheet = workbook.createSheet("Refugios");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Nombre", "Localidad", "Dirección", "Responsable", "Teléfono", "Email", "Capacidad", "Estado"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            List<Refugio> refugios = refugioRepository.findAll();

            int rowNum = 1;
            for (Refugio r : refugios) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(r.getIdRefugio());
                row.createCell(1).setCellValue(r.getNombreRefugio());
                row.createCell(2).setCellValue(r.getLocalidad() != null ? r.getLocalidad() : "-");
                row.createCell(3).setCellValue(r.getDireccion());
                row.createCell(4).setCellValue(r.getResponsable() != null ? r.getResponsable() : "-");
                row.createCell(5).setCellValue(r.getTelefono() != null ? r.getTelefono() : "-");
                row.createCell(6).setCellValue(r.getEmail() != null ? r.getEmail() : "-");
                row.createCell(7).setCellValue(r.getCapacidadMaxima() != null ? r.getCapacidadMaxima() : 0);
                row.createCell(8).setCellValue(r.getActivo() ? "Activo" : "Inactivo");

                for (int i = 0; i < 9; i++) {
                    row.getCell(i).setCellStyle(dataStyle);
                }
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando Excel de refugios", e);
        }
    }

    public byte[] generarReporteUsuariosExcel() {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            XSSFSheet sheet = workbook.createSheet("Usuarios");

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Nombre", "Apellido", "Email", "Teléfono", "Ciudad", "Roles", "Estado", "Fecha Registro"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            List<Usuario> usuarios = usuarioRepository.findAll();

            int rowNum = 1;
            for (Usuario u : usuarios) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(u.getIdUsuario());
                row.createCell(1).setCellValue(u.getNombre());
                row.createCell(2).setCellValue(u.getApellido());
                row.createCell(3).setCellValue(u.getEmail());
                row.createCell(4).setCellValue(u.getTelefono() != null ? u.getTelefono() : "-");
                row.createCell(5).setCellValue(u.getCiudad() != null ? u.getCiudad() : "-");

                String roles = u.getRoles().stream()
                        .map(r -> r.getNombreRol().replace("ROLE_", ""))
                        .collect(Collectors.joining(", "));
                row.createCell(6).setCellValue(roles);

                row.createCell(7).setCellValue(u.getActivo() ? "Activo" : "Inactivo");
                row.createCell(8).setCellValue(u.getFechaRegistro().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

                for (int i = 0; i < 9; i++) {
                    row.getCell(i).setCellStyle(dataStyle);
                }
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando Excel de usuarios", e);
        }
    }

    public byte[] generarReporteEstadisticasExcel(LocalDate fechaInicio, LocalDate fechaFin) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // Hoja 1: Estadísticas Generales
            XSSFSheet statsSheet = workbook.createSheet("Estadísticas");
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);

            int rowNum = 0;
            Row titleRow = statsSheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("ESTADÍSTICAS GENERALES - ADOPTPETS");
            titleCell.setCellStyle(headerStyle);

            statsSheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 1));
            rowNum++;

            // Mascotas
            Row mascotasHeader = statsSheet.createRow(rowNum++);
            mascotasHeader.createCell(0).setCellValue("MASCOTAS");
            mascotasHeader.getCell(0).setCellStyle(headerStyle);

            addStatRow(statsSheet, rowNum++, "Total de mascotas", mascotaRepository.count(), dataStyle);
            addStatRow(statsSheet, rowNum++, "Disponibles", mascotaRepository.countByEstadoAdopcion(EstadoAdopcion.disponible), dataStyle);
            addStatRow(statsSheet, rowNum++, "En proceso", mascotaRepository.countByEstadoAdopcion(EstadoAdopcion.en_proceso), dataStyle);

            // Adopciones
            Row adopcionesHeader = statsSheet.createRow(rowNum++);
            adopcionesHeader.createCell(0).setCellValue("ADOPCIONES");
            adopcionesHeader.getCell(0).setCellStyle(headerStyle);

            addStatRow(statsSheet, rowNum++, "Total", adopcionRepository.count(), dataStyle);
            addStatRow(statsSheet, rowNum++, "Pendientes", adopcionRepository.countByEstadoAdopcion(EstadoAdopcion.pendiente), dataStyle);
            addStatRow(statsSheet, rowNum++, "Aprobadas", adopcionRepository.countByEstadoAdopcion(EstadoAdopcion.aprobada), dataStyle);
            addStatRow(statsSheet, rowNum++, "Completadas", adopcionRepository.countByEstadoAdopcion(EstadoAdopcion.completada), dataStyle);
            addStatRow(statsSheet, rowNum++, "Rechazadas", adopcionRepository.countByEstadoAdopcion(EstadoAdopcion.rechazada), dataStyle);

            // Refugios y Usuarios
            Row refugiosHeader = statsSheet.createRow(rowNum++);
            refugiosHeader.createCell(0).setCellValue("REFUGIOS Y USUARIOS");
            refugiosHeader.getCell(0).setCellStyle(headerStyle);

            addStatRow(statsSheet, rowNum++, "Total refugios", refugioRepository.count(), dataStyle);
            addStatRow(statsSheet, rowNum++, "Refugios activos", refugioRepository.findByActivoTrue().size(), dataStyle);
            addStatRow(statsSheet, rowNum++, "Total usuarios", usuarioRepository.count(), dataStyle);

            statsSheet.autoSizeColumn(0);
            statsSheet.autoSizeColumn(1);

            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando Excel de estadísticas", e);
        }
    }

    // Métodos auxiliares para estilos
    private CellStyle createHeaderStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createDataStyle(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private void addStatRow(XSSFSheet sheet, int rowNum, String label, long value, CellStyle style) {
        Row row = sheet.createRow(rowNum);
        Cell labelCell = row.createCell(0);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(style);

        Cell valueCell = row.createCell(1);
        valueCell.setCellValue(value);
        valueCell.setCellStyle(style);
    }
}