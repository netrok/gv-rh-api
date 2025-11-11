// PeriodoDTO.java
package com.gv.mx.vacaciones.dto;
import java.time.LocalDate;
public record PeriodoDTO(Long id, Integer anio, LocalDate fechaInicio, LocalDate fechaFin) {}