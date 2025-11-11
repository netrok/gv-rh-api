// src/main/java/com/gv/mx/vacaciones/web/VacacionesController.java
package com.gv.mx.vacaciones.web;

import com.gv.mx.vacaciones.application.VacacionesService;
import com.gv.mx.vacaciones.dto.PeriodoDTO;
import com.gv.mx.vacaciones.dto.SaldoDTO;
import com.gv.mx.vacaciones.dto.SolicitudDTO;
import com.gv.mx.vacaciones.web.dto.DisponibilidadDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/vacaciones")
@Tag(name = "Vacaciones")
@SecurityRequirement(name = "bearerAuth")
public class VacacionesController {

    private final VacacionesService service;

    public VacacionesController(VacacionesService service) {
        this.service = service;
    }

    // ------- Periodo -------
    @PostMapping("/periodo")
    @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    @Operation(summary = "Crea/actualiza periodo vacacional por año")
    public PeriodoDTO upsertPeriodo(@RequestBody PeriodoDTO dto) {
        return service.upsertPeriodo(dto);
    }

    // ------- Saldo -------
    @PostMapping("/saldo/{empleadoId}/{anio}")
    @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    @Operation(summary = "Asigna saldo de vacaciones a empleado en año")
    public SaldoDTO asignarSaldo(@PathVariable Long empleadoId,
                                 @PathVariable Integer anio,
                                 @RequestParam Integer dias) {
        return service.asignarSaldo(empleadoId, anio, dias);
    }

    // ------- Solicitudes -------
    @GetMapping("/solicitudes")
    @Operation(summary = "Lista solicitudes")
    public Page<SolicitudDTO> listar(@ParameterObject Pageable pageable) {
        return service.listarSolicitudes(pageable);
    }

    @PostMapping("/solicitudes")
    @Operation(summary = "Crear solicitud (calcula días hábiles)")
    public SolicitudDTO crear(@RequestParam Long empleadoId,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
                              @RequestParam(required = false) String motivo,
                              @RequestParam(required = false) String adjunto) {
        return service.crearSolicitud(empleadoId, desde, hasta, motivo, adjunto);
    }

    @PostMapping("/solicitudes/{id}/aprobar")
    @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    @Operation(summary = "Aprueba una solicitud")
    public SolicitudDTO aprobar(@PathVariable Long id) {
        return service.aprobar(id);
    }

    @PostMapping("/solicitudes/{id}/rechazar")
    @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    @Operation(summary = "Rechaza una solicitud")
    public SolicitudDTO rechazar(@PathVariable Long id,
                                 @RequestParam(required = false) String motivo) {
        return service.rechazar(id, motivo);
    }

    @PostMapping("/solicitudes/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    @Operation(summary = "Cancela una solicitud")
    public SolicitudDTO cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }

    // ------- Disponibilidad -------
    @GetMapping("/disponibilidad")
    @Operation(
            summary = "Disponibilidad anual del empleado (asignados/disfrutados/pendientes)",
            description = "Excluye fines de semana y feriados oficializados en calendario."
    )
    public DisponibilidadDTO disponibilidad(@RequestParam Long empleadoId,
                                            @RequestParam Integer anio) {
        return service.disponibilidad(empleadoId, anio);
    }

    // ------- Exportaciones -------
    @GetMapping(
            value = "/solicitudes/export/xlsx",
            produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    )
    @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    @Operation(summary = "Exportar solicitudes a XLSX")
    public void exportXlsx(HttpServletResponse resp,
                           @RequestParam(required = false) Integer anio,
                           @RequestParam(required = false) String estado) throws Exception {
        var rows = service.buscarSolicitudes(anio, estado);
        resp.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        resp.setHeader("Content-Disposition", "attachment; filename=solicitudes.xlsx");
        com.gv.mx.vacaciones.application.export.SolicitudXlsxExporter.write(rows, resp.getOutputStream());
    }

    @GetMapping(value = "/solicitudes/export/pdf", produces = "application/pdf")
    @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    @Operation(summary = "Exportar solicitudes a PDF")
    public void exportPdf(HttpServletResponse resp,
                          @RequestParam(required = false) Integer anio,
                          @RequestParam(required = false) String estado) throws Exception {
        var rows = service.buscarSolicitudes(anio, estado);
        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "attachment; filename=solicitudes.pdf");
        com.gv.mx.vacaciones.application.export.SolicitudPdfExporter.write(rows, resp.getOutputStream());
    }
}
