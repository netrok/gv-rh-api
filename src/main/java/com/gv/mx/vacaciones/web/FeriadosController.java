// src/main/java/com/gv/mx/vacaciones/web/FeriadosController.java
package com.gv.mx.vacaciones.web;

import com.gv.mx.vacaciones.domain.Feriado;
import com.gv.mx.vacaciones.infrastructure.FeriadoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vacaciones/feriados")
@Tag(name = "Vacaciones - Feriados")
public class FeriadosController {

    private final FeriadoRepository repo;

    public FeriadosController(FeriadoRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    @Operation(summary = "Lista de feriados")
    public List<Feriado> listar() {
        return repo.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    @Operation(summary = "Crear feriado (fecha única)")
    public Feriado crear(@RequestBody Feriado f) {
        if (f.getFecha() == null || f.getDescripcion() == null || f.getDescripcion().isBlank()) {
            throw new IllegalArgumentException("fecha y descripcion son obligatorias");
        }
        if (repo.existsByFecha(f.getFecha())) {
            throw new IllegalStateException("Ya existe un feriado con esa fecha");
        }
        return repo.save(f);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RRHH')")
    @Operation(summary = "Eliminar feriado por id")
    public void eliminar(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
