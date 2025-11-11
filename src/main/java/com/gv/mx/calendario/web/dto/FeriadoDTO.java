package com.gv.mx.calendario.web.dto;

import java.time.LocalDate;

public record FeriadoDTO(
        Long id,
        LocalDate fecha,
        String descripcion,
        Boolean nacional
) {}
