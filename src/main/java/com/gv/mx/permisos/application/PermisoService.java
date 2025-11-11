package com.gv.mx.permisos.application;

import com.gv.mx.permisos.dto.PermisoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PermisoService {
    Page<PermisoDTO> listar(Long empleadoId, Pageable pageable);
    PermisoDTO obtener(Long id);
    PermisoDTO crear(PermisoDTO dto);
    PermisoDTO actualizar(Long id, PermisoDTO dto);
    void eliminar(Long id);

    PermisoDTO aprobar(Long id);
    PermisoDTO rechazar(Long id);
    PermisoDTO cancelar(Long id);
}
