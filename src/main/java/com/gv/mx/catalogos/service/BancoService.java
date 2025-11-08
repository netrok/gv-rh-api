package com.gv.mx.catalogos.service;

import com.gv.mx.catalogos.domain.Banco;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BancoService {
    Page<Banco> listar(String q, Pageable pageable);
    Banco obtener(Long id);
    Banco guardar(Banco data);
    Banco actualizar(Long id, Banco data);
    void eliminar(Long id);
}