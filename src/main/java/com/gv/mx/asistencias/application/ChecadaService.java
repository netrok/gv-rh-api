package com.gv.mx.asistencias.application;

import com.gv.mx.asistencias.dto.ChecadaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChecadaService {
    Page<ChecadaDTO> listar(ChecadaFilter filtro, Pageable pageable);
    ChecadaDTO crear(ChecadaDTO dto);
    void eliminar(Long id);
}
