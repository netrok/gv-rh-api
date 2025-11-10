package com.gv.mx.asistencias.application;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ChecadaFilter(
        Long empleadoId,
        LocalDateTime desde,
        LocalDateTime hasta,
        String tipo // "ENT" | "SAL" | null
) {}
