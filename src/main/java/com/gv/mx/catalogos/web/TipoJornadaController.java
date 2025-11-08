package com.gv.mx.catalogos.web;

import com.gv.mx.catalogos.domain.TipoJornada;
import com.gv.mx.catalogos.service.TipoJornadaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/catalogos/tipos-jornada")
@Tag(name = "Catálogos - Tipos de Jornada")
@RequiredArgsConstructor
public class TipoJornadaController {
    private final TipoJornadaService service;

    @GetMapping
    @Operation(summary="Lista tipos de jornada (paginado, filtro q)")
    public Page<TipoJornada> listar(@RequestParam(required=false) String q,
                                    @ParameterObject @PageableDefault(sort="nombre") Pageable pageable) {
        return service.listar(q, pageable);
    }

    @GetMapping("/{id}") @Operation(summary="Obtiene un tipo de jornada por id")
    public TipoJornada obtener(@PathVariable Long id) { return service.obtener(id); }

    @PostMapping @Operation(summary="Crea un tipo de jornada")
    public TipoJornada crear(@RequestBody TipoJornada body) { return service.crear(body); }

    @PutMapping("/{id}") @Operation(summary="Actualiza un tipo de jornada")
    public TipoJornada actualizar(@PathVariable Long id, @RequestBody TipoJornada body) {
        return service.actualizar(id, body);
    }

    @DeleteMapping("/{id}") @Operation(summary="Desactiva (soft delete) un tipo de jornada")
    public void desactivar(@PathVariable Long id) { service.desactivar(id); }
}