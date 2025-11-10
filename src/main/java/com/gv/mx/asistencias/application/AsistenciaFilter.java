package com.gv.mx.asistencias.application;

import com.gv.mx.asistencias.domain.AsistenciaEstado;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record AsistenciaFilter(
        Long empleadoId,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
        AsistenciaEstado estado
) {
    public void validate() {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new IllegalArgumentException("Rango de fechas inválido (desde > hasta).");
        }
    }
}
