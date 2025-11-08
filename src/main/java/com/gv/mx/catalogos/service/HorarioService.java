package com.gv.mx.catalogos.service;

import com.gv.mx.catalogos.domain.Horario;
import org.springframework.data.domain.*;

public interface HorarioService {
    Page<Horario> listar(String q, Pageable pageable);
    Horario crear(Horario h);
    Horario actualizar(Long id, Horario h);
    void desactivar(Long id);
    Horario obtener(Long id);
}