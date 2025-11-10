package com.gv.mx.asistencias.web;

import com.gv.mx.asistencias.application.AsistenciaFilter;
import com.gv.mx.asistencias.application.AsistenciaService;
import com.gv.mx.asistencias.dto.AsistenciaDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/asistencias")
@RequiredArgsConstructor
@Tag(name = "Asistencias")
public class AsistenciaController {

    private final AsistenciaService service;

    @GetMapping
    @Operation(
            summary = "Listar asistencias (paginado + filtros)",
            description = "Filtra por empleado, rango de fechas y estado."
    )
    public Page<AsistenciaDTO> listar(@Valid AsistenciaFilter filtro, Pageable pageable) {
        filtro.validate(); // si desde>hasta -> IllegalArgumentException -> 400 con JSON uniforme
        return service.listar(filtro, pageable);
    }

    @GetMapping("/{empleadoId}/{yyyyMMdd}")
    @Operation(
            summary = "Consultar asistencia por empleado y fecha",
            description = "Devuelve la asistencia de un empleado para el día YYYYMMDD.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = AsistenciaDTO.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "No encontrada")
            }
    )
    public AsistenciaDTO porDia(
            @PathVariable @NotNull Long empleadoId,
            @PathVariable String yyyyMMdd
    ) {
        return service.porDia(empleadoId, yyyyMMdd); // si no encuentra -> lanza y el handler da 404
    }

    @PostMapping("/recalcular")
    @Operation(
            summary = "Recalcular asistencias",
            description = "Recalcula asistencias en un rango de fechas.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = AsistenciaFilter.class),
                            examples = @ExampleObject(
                                    name = "Rango noviembre",
                                    value = """
                    {
                      "empleadoId": 1,
                      "desde": "2025-11-01",
                      "hasta": "2025-11-30"
                    }"""
                            )
                    )
            )
    )
    public int recalcular(@Valid @RequestBody AsistenciaFilter filtro) {
        filtro.validate();
        return service.recalcular(filtro); // devuelve cuántos días tocó
    }
}
