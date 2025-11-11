package com.gv.mx.vacaciones.web.dto;

public record DisponibilidadDTO(
        Long empleadoId,
        int anio,
        int asignados,      // días asignados del saldo anual
        int disfrutados,    // días aprobados y disfrutados en el año
        int pendientes      // asignados - disfrutados (no negativos)
) {}
