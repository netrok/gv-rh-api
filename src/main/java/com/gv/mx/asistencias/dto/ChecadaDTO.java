package com.gv.mx.asistencias.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record ChecadaDTO(
        @NotNull Long empleadoId,
        @NotBlank String tipoChecada,           // "ENT" | "SAL" ...
        @NotNull LocalDateTime fechaHora,
        @Size(max=100) String dispositivo,
        @Size(max=255) String ubicacion
) {}
