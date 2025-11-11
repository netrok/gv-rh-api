// src/main/java/com/gv/mx/vacaciones/application/VacacionesService.java
package com.gv.mx.vacaciones.application;

import com.gv.mx.vacaciones.domain.SolicitudVacaciones;
import com.gv.mx.vacaciones.dto.PeriodoDTO;
import com.gv.mx.vacaciones.dto.SaldoDTO;
import com.gv.mx.vacaciones.dto.SolicitudDTO;
import com.gv.mx.vacaciones.web.dto.DisponibilidadDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface VacacionesService {
    // Periodos
    PeriodoDTO upsertPeriodo(PeriodoDTO dto);

    // Saldos
    SaldoDTO asignarSaldo(Long empleadoId, Integer anio, Integer diasAsignados);

    // Solicitudes
    Page<SolicitudDTO> listarSolicitudes(Pageable pageable);
    SolicitudDTO crearSolicitud(Long empleadoId, LocalDate desde, LocalDate hasta, String motivo, String adjunto);
    SolicitudDTO aprobar(Long id);
    SolicitudDTO rechazar(Long id, String motivo);
    SolicitudDTO cancelar(Long id);

    // Disponibilidad anual (usa el DTO del web layer que consume el controller)
    DisponibilidadDTO disponibilidad(Long empleadoId, Integer anio);

    // Listado para export
    List<SolicitudVacaciones> buscarSolicitudes(Integer anio, String estado);
}
