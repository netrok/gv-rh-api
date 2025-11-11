// IncidenciaService.java
package com.gv.mx.incidencias.application;

import com.gv.mx.incidencias.dto.IncidenciaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IncidenciaService {
    Page<IncidenciaDTO> listar(Long empleadoId, Pageable pageable);
    IncidenciaDTO obtener(Long id);
    IncidenciaDTO crear(IncidenciaDTO dto);
    IncidenciaDTO actualizar(Long id, IncidenciaDTO dto);
    void eliminar(Long id);
    IncidenciaDTO aprobar(Long id);
    IncidenciaDTO rechazar(Long id);
    IncidenciaDTO cancelar(Long id);
}
