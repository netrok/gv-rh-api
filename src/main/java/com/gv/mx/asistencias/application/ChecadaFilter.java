package com.gv.mx.asistencias.application;

import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record ChecadaFilter(
        Long empleadoId,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,
        @Pattern(regexp = "ENT|SAL", message = "tipo debe ser ENT o SAL") String tipo
) {
    public void validate() {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new IllegalArgumentException("Rango de fechas inválido (desde > hasta).");
        }
    }
}
