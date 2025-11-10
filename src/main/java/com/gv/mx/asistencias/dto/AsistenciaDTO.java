package com.gv.mx.asistencias.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record AsistenciaDTO(
        @NotNull Long empleadoId,
        @NotNull LocalDate fecha,
        @NotBlank String estado,              // "OK" | "RETARDO" | "FALTA" | ...
        Integer minutosRetardo,
        Integer minutosAnticipoSalida,
        @Size(max=255) String notas
) {}
