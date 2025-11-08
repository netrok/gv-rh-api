package com.gv.mx.catalogos.web;

import com.gv.mx.catalogos.domain.Puesto;
import com.gv.mx.catalogos.service.PuestoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/catalogos/puestos")
@Tag(name = "Catálogos - Puestos")
@RequiredArgsConstructor
public class PuestoController {

    private final PuestoService service;

    @GetMapping
    @Operation(summary = "Lista puestos (paginado, filtro q y departamentoId)")
    public Page<Puesto> listar(@RequestParam(required = false) String q,
                               @RequestParam(required = false) Long departamentoId,
                               Pageable pageable) {
        return service.listar(q, departamentoId, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un puesto por id")
    public Puesto obtener(@PathVariable Long id) { return service.obtener(id); }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    @Operation(summary = "Crea un puesto")
    public Puesto crear(@Valid @RequestBody Puesto body) { return service.crear(body); }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    @Operation(summary = "Actualiza un puesto")
    public Puesto actualizar(@PathVariable Long id, @Valid @RequestBody Puesto body) {
        return service.actualizar(id, body);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    @Operation(summary = "Desactiva (soft delete) un puesto")
    public void desactivar(@PathVariable Long id) { service.desactivar(id); }
}