package com.gv.mx.catalogos.service;

import com.gv.mx.catalogos.domain.TipoContrato;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TipoContratoService {
    Page<TipoContrato> listar(String q, Pageable pageable);
    TipoContrato obtener(Long id);
    TipoContrato crear(TipoContrato body);
    TipoContrato actualizar(Long id, TipoContrato body);
    void desactivar(Long id); // soft delete
}