package com.gv.mx.calendario.web;

import com.gv.mx.calendario.application.CalendarioService;
import com.gv.mx.calendario.web.dto.FeriadoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/calendario")
@Tag(name = "Calendario")
public class CalendarioController {

    private final CalendarioService service;
    public CalendarioController(CalendarioService service) { this.service = service; }

    @GetMapping("/feriados")
    @Operation(summary = "Lista feriados (opcional: rango)")
    public List<FeriadoDTO> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return service.listar(desde, hasta);
    }

    @PostMapping("/feriados")
    @Operation(summary = "Crea feriado")
    public FeriadoDTO crear(@RequestBody FeriadoDTO dto) { return service.crear(dto); }

    @PutMapping("/feriados/{id}")
    @Operation(summary = "Actualiza feriado")
    public FeriadoDTO actualizar(@PathVariable Long id, @RequestBody FeriadoDTO dto) { return service.actualizar(id, dto); }

    @DeleteMapping("/feriados/{id}")
    @Operation(summary = "Elimina feriado")
    public void eliminar(@PathVariable Long id) { service.eliminar(id); }

    @PostMapping("/evaluar")
    @Operation(summary = "Evalúa si una fecha es laborable (L-V sin feriados)")
    public ResponseEntity<Map<String,Object>> evaluar(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha){
        boolean laborable = service.esLaborable(fecha);
        return ResponseEntity.ok(Map.of("fecha", fecha, "laborable", laborable));
    }
}
