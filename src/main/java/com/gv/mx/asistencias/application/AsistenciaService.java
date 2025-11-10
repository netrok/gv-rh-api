package com.gv.mx.asistencias.application;

import com.gv.mx.asistencias.domain.Asistencia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface AsistenciaService {
    Page<Asistencia> listar(AsistenciaFilter f, Pageable pageable);

    /**
     * Genera o recalcula la asistencia de un empleado para una fecha a partir de checadas.
     * Retorna el registro resultante.
     */
    Asistencia generarParaFecha(Long empleadoId, LocalDate fecha);
}
