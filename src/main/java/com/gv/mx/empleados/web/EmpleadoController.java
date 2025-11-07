// src/main/java/com/gv/mx/empleados/web/EmpleadoController.java
package com.gv.mx.empleados.web;

import com.gv.mx.empleados.application.EmpleadoService;
import com.gv.mx.empleados.dto.EmpleadoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/empleados")
@Tag(name = "Empleados")
@RequiredArgsConstructor
public class EmpleadoController {

    private final EmpleadoService service;

    @GetMapping
    @Operation(summary = "Lista empleados (paginable, con filtro opcional ?q=)")
    @PreAuthorize("hasAnyRole('ADMIN','RRHH','SUPERVISOR')")
    public Page<EmpleadoDTO> listar(
            @RequestParam(value = "q", required = false) String q,
            @ParameterObject Pageable pageable
    ) {
        return service.listar(q, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un empleado por id")
    @PreAuthorize("hasAnyRole('ADMIN','RRHH','SUPERVISOR')")
    public EmpleadoDTO obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    @Operation(summary = "Crea empleado")
    @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    public EmpleadoDTO crear(@Valid @RequestBody EmpleadoDTO dto) {
        return service.crear(dto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza empleado")
    @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    public EmpleadoDTO actualizar(@PathVariable Long id, @Valid @RequestBody EmpleadoDTO dto) {
        return service.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina empleado")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
