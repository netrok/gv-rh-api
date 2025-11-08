package com.gv.mx.catalogos.service;

import com.gv.mx.catalogos.domain.Turno;
import org.springframework.data.domain.*;

public interface TurnoService {
    Page<Turno> listar(String q, Pageable pageable);
    Turno crear(Turno t);
    Turno actualizar(Long id, Turno t);
    void desactivar(Long id);
    Turno obtener(Long id);
}