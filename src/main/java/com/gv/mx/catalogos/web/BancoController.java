package com.gv.mx.catalogos.web;

import com.gv.mx.catalogos.domain.Banco;
import com.gv.mx.catalogos.service.BancoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
// import org.springframework.security.access.prepost.PreAuthorize; // cuando controles por rol

@RestController
@RequestMapping("/api/catalogos/bancos")
@Tag(name = "Catálogos - Bancos")
@RequiredArgsConstructor
public class BancoController {

    private final BancoService service;

    @GetMapping
    @Operation(summary = "Lista bancos (paginado, filtro q)")
    public Page<Banco> listar(@RequestParam(required = false) String q, Pageable pageable) {
        return service.listar(q, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene banco por id")
    public Banco obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    @Operation(summary = "Crea banco")
    public Banco guardar(@Valid @RequestBody Banco data) {
        return service.guardar(data);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza banco")
    public Banco actualizar(@PathVariable Long id, @Valid @RequestBody Banco data) {
        return service.actualizar(id, data);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina banco")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}