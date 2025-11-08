package com.gv.mx.catalogos.web;

import com.gv.mx.catalogos.domain.Departamento;
import com.gv.mx.catalogos.service.DepartamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/catalogos/departamentos")
@Tag(name = "Catálogos - Departamentos")
@RequiredArgsConstructor
public class DepartamentoController {

    private final DepartamentoService service;

    @GetMapping
    @Operation(summary = "Lista departamentos (paginado, filtro q)")
    public Page<Departamento> listar(@RequestParam(required = false) String q, Pageable pageable) {
        return service.listar(q, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un departamento por id")
    public Departamento obtener(@PathVariable Long id) { return service.obtener(id); }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    @Operation(summary = "Crea un departamento")
    public Departamento crear(@Valid @RequestBody Departamento body) { return service.crear(body); }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    @Operation(summary = "Actualiza un departamento")
    public Departamento actualizar(@PathVariable Long id, @Valid @RequestBody Departamento body) {
        return service.actualizar(id, body);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    @Operation(summary = "Desactiva (soft delete) un departamento")
    public void desactivar(@PathVariable Long id) { service.desactivar(id); }
}