package com.gv.mx.asistencias.web;

import com.gv.mx.asistencias.application.ChecadaFilter;
import com.gv.mx.asistencias.application.ChecadaService;
import com.gv.mx.asistencias.domain.Checada;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/asistencias/checadas")
@RequiredArgsConstructor
public class ChecadaController {

    private final ChecadaService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RRHH','SUPERVISOR')")
    @Operation(summary = "Registrar checada (ENT/SAL)")
    public Checada registrar(@RequestBody Checada c) {
        return service.registrar(c);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','RRHH','SUPERVISOR')")
    @Operation(summary = "Listar checadas (paginado + filtros)")
    public Page<Checada> listar(
            @RequestParam(required = false) Long empleadoId,
            @RequestParam(required = false) String desde,  // ISO date-time opcional
            @RequestParam(required = false) String hasta,  // ISO date-time opcional
            @RequestParam(required = false) String tipo,   // ENT | SAL
            @ParameterObject Pageable pageable
    ) {
        var f = new ChecadaFilter(
                empleadoId,
                desde != null ? java.time.LocalDateTime.parse(desde) : null,
                hasta != null ? java.time.LocalDateTime.parse(hasta) : null,
                tipo
        );
        return service.listar(f, pageable);
    }
}
