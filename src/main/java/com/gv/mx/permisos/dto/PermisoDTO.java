package com.gv.mx.permisos.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PermisoDTO(
        Long id,
        Long empleadoId,
        String tipo,      // enum name
        String estado,    // enum name
        LocalDate fechaDesde,
        LocalDate fechaHasta,
        BigDecimal horas,
        String motivo,
        String adjunto
) {}
