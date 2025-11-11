// VacacionesService.java
package com.gv.mx.vacaciones.application;

import com.gv.mx.vacaciones.dto.*;
import org.springframework.data.domain.Page; import org.springframework.data.domain.Pageable;
import java.time.LocalDate;

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
}
