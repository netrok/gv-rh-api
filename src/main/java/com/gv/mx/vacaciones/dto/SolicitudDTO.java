package com.gv.mx.vacaciones.dto;
import java.time.LocalDate;
public record SolicitudDTO(Long id, Long empleadoId, Integer anio, LocalDate fechaDesde, LocalDate fechaHasta,
                           Integer dias, String estado, String motivo, String adjunto) {}