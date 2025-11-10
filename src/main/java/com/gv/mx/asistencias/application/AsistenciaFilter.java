package com.gv.mx.asistencias.application;

import com.gv.mx.asistencias.domain.AsistenciaEstado;

import java.time.LocalDate;

public record AsistenciaFilter(
        Long empleadoId,
        LocalDate fechaDesde,
        LocalDate fechaHasta,
        AsistenciaEstado estado
) {}
