package com.gv.mx.catalogos.web;

import com.gv.mx.catalogos.domain.Turno;
import com.gv.mx.catalogos.service.TurnoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/catalogos/turnos")
@Tag(name = "Catálogos - Turnos")
@RequiredArgsConstructor
public class TurnoController {

    private final TurnoService service;

    @GetMapping
    @Operation(summary = "Lista turnos (paginado, filtro q)")
    public Page<Turno> listar(@RequestParam(required = false) String q, Pageable pageable) {
        return service.listar(q, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un turno por id")
    public Turno obtener(@PathVariable Long id) { return service.obtener(id); }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    @Operation(summary = "Crea un turno")
    public Turno crear(@Valid @RequestBody Turno body) { return service.crear(body); }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    @Operation(summary = "Actualiza un turno")
    public Turno actualizar(@PathVariable Long id, @Valid @RequestBody Turno body) {
        return service.actualizar(id, body);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    @Operation(summary = "Desactiva (soft delete) un turno")
    public void desactivar(@PathVariable Long id) { service.desactivar(id); }
}