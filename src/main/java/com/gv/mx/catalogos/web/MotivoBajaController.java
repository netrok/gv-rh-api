package com.gv.mx.catalogos.web;

import com.gv.mx.catalogos.domain.MotivoBaja;
import com.gv.mx.catalogos.service.MotivoBajaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/catalogos/motivos-baja")
@Tag(name = "Catálogos - Motivos de Baja")
@RequiredArgsConstructor
public class MotivoBajaController {
    private final MotivoBajaService service;

    @GetMapping
    @Operation(summary="Lista motivos de baja (paginado, filtro q)")
    public Page<MotivoBaja> listar(@RequestParam(required=false) String q,
                                   @ParameterObject @PageableDefault(sort="nombre") Pageable pageable) {
        return service.listar(q, pageable);
    }

    @GetMapping("/{id}") @Operation(summary="Obtiene un motivo de baja por id")
    public MotivoBaja obtener(@PathVariable Long id) { return service.obtener(id); }

    @PostMapping @Operation(summary="Crea un motivo de baja")
    public MotivoBaja crear(@RequestBody MotivoBaja body) { return service.crear(body); }

    @PutMapping("/{id}") @Operation(summary="Actualiza un motivo de baja")
    public MotivoBaja actualizar(@PathVariable Long id, @RequestBody MotivoBaja body) {
        return service.actualizar(id, body);
    }

    @DeleteMapping("/{id}") @Operation(summary="Desactiva (soft delete) un motivo de baja")
    public void desactivar(@PathVariable Long id) { service.desactivar(id); }
}