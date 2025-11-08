package com.gv.mx.catalogos.service;

import com.gv.mx.catalogos.domain.Puesto;
import org.springframework.data.domain.*;

public interface PuestoService {
    Page<Puesto> listar(String q, Long departamentoId, Pageable pageable);
    Puesto crear(Puesto p);
    Puesto actualizar(Long id, Puesto p);
    void desactivar(Long id);
    Puesto obtener(Long id);
}