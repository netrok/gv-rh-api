package com.gv.mx.catalogos.service;

import com.gv.mx.catalogos.domain.TipoJornada;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TipoJornadaService {
    Page<TipoJornada> listar(String q, Pageable pageable);
    TipoJornada obtener(Long id);
    TipoJornada crear(TipoJornada body);
    TipoJornada actualizar(Long id, TipoJornada body);
    void desactivar(Long id);
}