// IncidenciaController.java
package com.gv.mx.incidencias.web;

import com.gv.mx.incidencias.application.IncidenciaService;
import com.gv.mx.incidencias.dto.IncidenciaDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/incidencias")
@Tag(name="Incidencias")
public class IncidenciaController {
    private final IncidenciaService service;
    public IncidenciaController(IncidenciaService s){ this.service=s; }

    @GetMapping @Operation(summary="Lista incidencias (paginado; filtrar por empleadoId)")
    public Page<IncidenciaDTO> listar(@RequestParam(required=false) Long empleadoId, Pageable pageable){
        return service.listar(empleadoId, pageable);
    }

    @GetMapping("/{id}") @Operation(summary="Obtiene una incidencia por id")
    public IncidenciaDTO obtener(@PathVariable Long id){ return service.obtener(id); }

    @PostMapping @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    @Operation(summary="Crea una incidencia")
    public IncidenciaDTO crear(@Valid @RequestBody IncidenciaDTO dto){ return service.crear(dto); }

    @PutMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    @Operation(summary="Actualiza una incidencia")
    public IncidenciaDTO actualizar(@PathVariable Long id, @Valid @RequestBody IncidenciaDTO dto){
        return service.actualizar(id, dto);
    }

    @DeleteMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    @Operation(summary="Elimina una incidencia")
    public void eliminar(@PathVariable Long id){ service.eliminar(id); }

    @PostMapping("/{id}/aprobar") @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    public IncidenciaDTO aprobar(@PathVariable Long id){ return service.aprobar(id); }

    @PostMapping("/{id}/rechazar") @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    public IncidenciaDTO rechazar(@PathVariable Long id){ return service.rechazar(id); }

    @PostMapping("/{id}/cancelar") @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    public IncidenciaDTO cancelar(@PathVariable Long id){ return service.cancelar(id); }
}
