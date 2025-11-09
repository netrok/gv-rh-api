package com.gv.mx.catalogos.service;

import com.gv.mx.catalogos.domain.Horario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HorarioService {
    Page<Horario> listar(String q, Pageable pageable);
    Horario obtener(Long id);
    Horario crear(Horario body);
    Horario actualizar(Long id, Horario body);
    void desactivar(Long id);
}