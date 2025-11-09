// src/main/java/com/gv/mx/empleados/web/EmpleadoController.java
package com.gv.mx.empleados.web;

import com.gv.mx.empleados.application.EmpleadoFilter;
import com.gv.mx.empleados.application.EmpleadoService;
import com.gv.mx.empleados.dto.EmpleadoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/empleados")
@RequiredArgsConstructor
public class EmpleadoController {

    private final EmpleadoService service;

    @GetMapping
    @Operation(
            summary = "Lista empleados (paginado + filtros)",
            description = "Filtros: q, departamentoId, puestoId, activo, fechaIngresoDesde, fechaIngresoHasta"
    )
    public Page<EmpleadoDTO> listar(
            @Parameter(description = "Búsqueda libre (numEmpleado, nombre, email)")
            @RequestParam(required = false) String q,
            @Parameter(description = "ID de Departamento")
            @RequestParam(required = false) Long departamentoId,
            @Parameter(description = "ID de Puesto")
            @RequestParam(required = false) Long puestoId,
            @Parameter(description = "Activo (true/false)")
            @RequestParam(required = false) Boolean activo,
            @Parameter(description = "Fecha ingreso desde (YYYY-MM-DD)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaIngresoDesde,
            @Parameter(description = "Fecha ingreso hasta (YYYY-MM-DD)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaIngresoHasta,
            @ParameterObject Pageable pageable
    ) {
        var f = new EmpleadoFilter(q, departamentoId, puestoId, activo, fechaIngresoDesde, fechaIngresoHasta);
        return service.listar(f, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un empleado por id")
    public EmpleadoDTO obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    @Operation(summary = "Crea un empleado")
    public EmpleadoDTO crear(@Valid @RequestBody EmpleadoDTO dto) {
        return service.crear(dto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza un empleado")
    public EmpleadoDTO actualizar(@PathVariable Long id, @Valid @RequestBody EmpleadoDTO dto) {
        return service.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un empleado")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}