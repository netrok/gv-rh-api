package com.gv.mx.catalogos.web;

import com.gv.mx.catalogos.domain.TipoContrato;
import com.gv.mx.catalogos.service.TipoContratoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/catalogos/tipos-contrato")
@Tag(name = "Catálogos - Tipos de Contrato")
@RequiredArgsConstructor
public class TipoContratoController {

    private final TipoContratoService service;

    @GetMapping
    @Operation(summary="Lista tipos de contrato (paginado, filtro q)")
    public Page<TipoContrato> listar(@RequestParam(required=false) String q,
                                     @ParameterObject @PageableDefault(sort = "nombre") Pageable pageable) {
        return service.listar(q, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary="Obtiene un tipo de contrato por id")
    public TipoContrato obtener(@PathVariable Long id) { return service.obtener(id); }

    @PostMapping
    @Operation(summary="Crea un tipo de contrato")
    public TipoContrato crear(@RequestBody TipoContrato body) { return service.crear(body); }

    @PutMapping("/{id}")
    @Operation(summary="Actualiza un tipo de contrato")
    public TipoContrato actualizar(@PathVariable Long id, @RequestBody TipoContrato body) {
        return service.actualizar(id, body);
    }

    @DeleteMapping("/{id}")
    @Operation(summary="Desactiva (soft delete) un tipo de contrato")
    public void desactivar(@PathVariable Long id) { service.desactivar(id); }
}