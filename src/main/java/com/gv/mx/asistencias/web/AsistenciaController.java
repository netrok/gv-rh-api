package com.gv.mx.asistencias.web;

import com.gv.mx.asistencias.application.AsistenciaFilter;
import com.gv.mx.asistencias.application.AsistenciaService;
import com.gv.mx.asistencias.domain.Asistencia;
import com.gv.mx.asistencias.domain.AsistenciaEstado;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/asistencias")
@RequiredArgsConstructor
public class AsistenciaController {

    private final AsistenciaService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','RRHH','SUPERVISOR')")
    @Operation(summary = "Listar asistencias (paginado + filtros)")
    public Page<Asistencia> listar(
            @RequestParam(required = false) Long empleadoId,
            @RequestParam(required = false) String fechaDesde, // yyyy-MM-dd
            @RequestParam(required = false) String fechaHasta, // yyyy-MM-dd
            @RequestParam(required = false) AsistenciaEstado estado,
            @ParameterObject Pageable pageable
    ) {
        var f = new AsistenciaFilter(
                empleadoId,
                fechaDesde != null ? LocalDate.parse(fechaDesde) : null,
                fechaHasta != null ? LocalDate.parse(fechaHasta) : null,
                estado
        );
        return service.listar(f, pageable);
    }

    @PostMapping("/generar")
    @PreAuthorize("hasAnyRole('ADMIN','RRHH','SUPERVISOR')")
    @Operation(summary = "Genera/recalcula la asistencia de un empleado para una fecha a partir de checadas")
    public Asistencia generar(
            @RequestParam Long empleadoId,
            @RequestParam String fecha // yyyy-MM-dd
    ) {
        return service.generarParaFecha(empleadoId, LocalDate.parse(fecha));
    }
}
