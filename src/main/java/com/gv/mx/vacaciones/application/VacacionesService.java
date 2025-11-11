// src/main/java/com/gv/mx/vacaciones/application/VacacionesService.java
package com.gv.mx.vacaciones.application;

import com.gv.mx.vacaciones.domain.SolicitudVacaciones;
import com.gv.mx.vacaciones.dto.PeriodoDTO;
import com.gv.mx.vacaciones.dto.SaldoDTO;
import com.gv.mx.vacaciones.dto.SolicitudDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface VacacionesService {
    // periodos
    PeriodoDTO upsertPeriodo(PeriodoDTO dto);

    // saldos
    SaldoDTO asignarSaldo(Long empleadoId, Integer anio, Integer diasAsignados);

    // solicitudes
    Page<SolicitudDTO> listarSolicitudes(Pageable p);
    SolicitudDTO crearSolicitud(Long empleadoId, LocalDate desde, LocalDate hasta, String motivo, String adjunto);
    SolicitudDTO aprobar(Long id);
    SolicitudDTO rechazar(Long id, String motivo);
    SolicitudDTO cancelar(Long id);

    // disponibilidad anual
    record Disponibilidad(Integer asignados, Integer disfrutados, Integer pendientes) {}
    Disponibilidad disponibilidad(Long empleadoId, Integer anio);

    // export/listado para exports
    List<SolicitudVacaciones> buscarSolicitudes(Integer anio, String estado);
}
