// src/main/java/com/gv/mx/empleados/web/EmpleadoController.java
package com.gv.mx.empleados.web;

import com.gv.mx.empleados.application.EmpleadoService;
import com.gv.mx.empleados.dto.EmpleadoDTO;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/empleados")
@RequiredArgsConstructor
public class EmpleadoController {

    private final EmpleadoService service;

    @GetMapping
    public Page<EmpleadoDTO> listar(@ParameterObject Pageable pageable) {
        return service.listar(pageable);
    }

    @GetMapping("/{id}")
    public EmpleadoDTO obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    public EmpleadoDTO crear(@RequestBody EmpleadoDTO dto) {
        return service.crear(dto);
    }

    @PutMapping("/{id}")
    public EmpleadoDTO actualizar(@PathVariable Long id, @RequestBody EmpleadoDTO dto) {
        return service.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
