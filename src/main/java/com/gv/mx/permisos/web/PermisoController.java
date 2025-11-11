package com.gv.mx.permisos.web;

import com.gv.mx.permisos.application.PermisoService;
import com.gv.mx.permisos.dto.PermisoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/permisos")
@Tag(name = "Permisos")
public class PermisoController {

    private final PermisoService service;
    public PermisoController(PermisoService service){ this.service = service; }

    @GetMapping
    @Operation(summary = "Lista permisos (paginado; filtrar por empleadoId)")
    public Page<PermisoDTO> listar(@RequestParam(required = false) Long empleadoId, Pageable pageable){
        return service.listar(empleadoId, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un permiso por id")
    public PermisoDTO obtener(@PathVariable Long id){ return service.obtener(id); }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    @Operation(summary = "Crea un permiso")
    public PermisoDTO crear(@Valid @RequestBody PermisoDTO dto){ return service.crear(dto); }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    @Operation(summary = "Actualiza un permiso")
    public PermisoDTO actualizar(@PathVariable Long id, @Valid @RequestBody PermisoDTO dto){
        return service.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    @Operation(summary = "Elimina un permiso")
    public void eliminar(@PathVariable Long id){ service.eliminar(id); }

    @PostMapping("/{id}/aprobar")
    @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    @Operation(summary = "Aprueba un permiso")
    public PermisoDTO aprobar(@PathVariable Long id){ return service.aprobar(id); }

    @PostMapping("/{id}/rechazar")
    @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    @Operation(summary = "Rechaza un permiso")
    public PermisoDTO rechazar(@PathVariable Long id){ return service.rechazar(id); }

    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    @Operation(summary = "Cancela un permiso")
    public PermisoDTO cancelar(@PathVariable Long id){ return service.cancelar(id); }
}
