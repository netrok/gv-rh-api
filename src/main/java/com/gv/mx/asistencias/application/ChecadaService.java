package com.gv.mx.asistencias.application;

import com.gv.mx.asistencias.domain.Checada;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChecadaService {
    Checada registrar(Checada c);
    Page<Checada> listar(ChecadaFilter f, Pageable pageable);
}
