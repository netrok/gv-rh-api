package com.gv.mx.asistencias.application;

import com.gv.mx.asistencias.dto.AsistenciaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AsistenciaService {
    Page<AsistenciaDTO> listar(AsistenciaFilter filtro, Pageable pageable);
    AsistenciaDTO porDia(Long empleadoId, String yyyyMMdd);
    int recalcular(AsistenciaFilter filtro);
}
