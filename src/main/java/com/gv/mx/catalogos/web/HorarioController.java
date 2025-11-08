package com.gv.mx.catalogos.web;

import com.gv.mx.catalogos.domain.Horario;
import com.gv.mx.catalogos.service.HorarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/catalogos/horarios")
@Tag(name = "Catálogos - Horarios")
@RequiredArgsConstructor
public class HorarioController {

    private final HorarioService service;

    @GetMapping
    @Operation(summary = "Lista horarios (paginado, filtro q)")
    public Page<Horario> listar(@RequestParam(required = false) String q, Pageable pageable) {
        return service.listar(q, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un horario por id")
    public Horario obtener(@PathVariable Long id) { return service.obtener(id); }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    @Operation(summary = "Crea un horario")
    public Horario crear(@Valid @RequestBody Horario body) { return service.crear(body); }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    @Operation(summary = "Actualiza un horario")
    public Horario actualizar(@PathVariable Long id, @Valid @RequestBody Horario body) {
        return service.actualizar(id, body);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    @Operation(summary = "Desactiva (soft delete) un horario")
    public void desactivar(@PathVariable Long id) { service.desactivar(id); }
}