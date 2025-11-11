// IncidenciaDTO.java
package com.gv.mx.incidencias.dto;
import java.time.LocalDate;

public record IncidenciaDTO(
        Long id, Long empleadoId, String tipo, String estado,
        LocalDate fecha, Integer minutos, String motivo, String adjunto
) {}
