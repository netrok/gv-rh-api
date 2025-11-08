package com.gv.mx.catalogos.service;

import com.gv.mx.catalogos.domain.MotivoBaja;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MotivoBajaService {
    Page<MotivoBaja> listar(String q, Pageable pageable);
    MotivoBaja obtener(Long id);
    MotivoBaja crear(MotivoBaja body);
    MotivoBaja actualizar(Long id, MotivoBaja body);
    void desactivar(Long id);
}