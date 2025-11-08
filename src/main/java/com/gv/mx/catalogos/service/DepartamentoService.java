package com.gv.mx.catalogos.service;

import com.gv.mx.catalogos.domain.Departamento;
import org.springframework.data.domain.*;

public interface DepartamentoService {
    Page<Departamento> listar(String q, Pageable pageable);
    Departamento crear(Departamento d);
    Departamento actualizar(Long id, Departamento d);
    void desactivar(Long id); // delete lógico (activo=false)
    Departamento obtener(Long id);
}