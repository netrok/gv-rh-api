package com.gv.mx.asistencias.web;

import com.gv.mx.asistencias.application.ChecadaFilter;
import com.gv.mx.asistencias.application.ChecadaService;
import com.gv.mx.asistencias.dto.ChecadaDTO;
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
@RequestMapping("/api/checadas")
@RequiredArgsConstructor
@Tag(name = "Checadas")
public class ChecadaController {

    private final ChecadaService service;

    @GetMapping
    @Operation(
            summary = "Listar checadas (paginado + filtros)",
            description = "Permite filtrar por empleado, rango de fechas y tipo de checada."
    )
    public Page<ChecadaDTO> listar(@Valid ChecadaFilter filtro, Pageable pageable) {
        filtro.validate(); // lanza IllegalArgumentException -> lo formatea el GlobalExceptionHandler
        return service.listar(filtro, pageable);
    }

    @PostMapping
    @Operation(
            summary = "Crear checada",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ChecadaDTO.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Entrada 8:05",
                                            value = """
                        {
                          "empleadoId": 1,
                          "tipoChecada": "ENT",
                          "fechaHora": "2025-11-10T08:05:00",
                          "dispositivo": "reloj-1",
                          "ubicacion": "Oficina Matriz"
                        }"""
                                    )
                            }
                    )
            )
    )
    public ChecadaDTO crear(@Valid @RequestBody ChecadaDTO dto) {
        return service.crear(dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar checada por ID")
    public void eliminar(@PathVariable @NotNull Long id) {
        service.eliminar(id); // si no existe, el service lanza IllegalArgumentException o NoSuchElement -> 404/400
    }
}
